package model;

import logics.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LivingRabbit extends Rabbit {

    private static final Map<LivingRabbitAttitude, Integer> VALUE_MAP = new EnumMap<> ( LivingRabbitAttitude.class );

    static {
        VALUE_MAP.put ( LivingRabbitAttitude.PREDATOR ,-20 );
        VALUE_MAP.put ( LivingRabbitAttitude.COMPETITOR ,-5 );
        VALUE_MAP.put ( LivingRabbitAttitude.MATE ,10 );
        VALUE_MAP.put ( LivingRabbitAttitude.FOOD ,10 );
    }

    public LivingRabbit (Position position ,int age ,Sex sex ,ScentStorage scentStorage) {
        super ( position ,age ,sex ,scentStorage );
    }

    @Override
    protected LivingRabbit newRabbit ( ) {
        return new LivingRabbit ( getPosition ( ) ,0 ,Sex.random ( ) ,getScentStorage ( ) );
    }

    @Override
    public Optional<Position> move (Visibility visibility) {
        Map<Position, Set<LivingRabbitAttitude>> positionValues = updateWithUnits ( visibility.units ( ) );

        HashMap<Position, Integer> positionValueMap = positionValues.entrySet ( ).stream ( )
                .collect ( Collectors.toMap (
                        Map.Entry::getKey ,e -> e.getValue ( ).stream ( ).mapToInt ( VALUE_MAP::get ).sum ( ) ,
                        Integer::sum ,HashMap::new ) );

        Map<Position, Integer> predatorValueMap = updateProliferatingValues ( visibility ,LivingRabbitAttitude.PREDATOR ,positionValues );
        Map<Position, Integer> competitorValueMap = updateProliferatingValues ( visibility ,LivingRabbitAttitude.COMPETITOR ,positionValues );

        Integer foodCellValue = VALUE_MAP.get ( LivingRabbitAttitude.FOOD );
        Map<Position, Integer> cellValues = visibility.cells ( )
                .collect ( Collectors.toMap (
                        Cell::getPosition ,
                        c -> c.getGrass ( ).getFoodCurrent ( ) >= c.getGrass ( ).getFoodValue ( ) ? foodCellValue : 0 ) );

        Map<Position, Integer> totalValueMap = CollectionUtils.mergeMaps (
                Integer::sum ,predatorValueMap ,competitorValueMap ,positionValueMap ,cellValues );
        return totalValueMap.entrySet ( ).stream ( )
                .max ( Comparator.comparingInt ( Map.Entry::getValue ) )
                .map ( max -> {
                    List<Direction> availableDirections = Direction.shuffledValues ( )
                            .filter ( dir -> getPosition ( ).by ( dir ).adjustableIn ( 0 ,0 ,visibility.Width ( ) ,visibility.Height ( ) ) )
                            .collect ( Collectors.toList ( ) );
                    return getPosition ( ).inDirectionTo ( max.getKey ( ) ,availableDirections );
                } );
    }

    private Map<Position, Integer> updateProliferatingValues (Visibility visibility ,LivingRabbitAttitude key ,
                                                              Map<Position, Set<LivingRabbitAttitude>> positionValues) {
        List<Position> negativePositions = positionValues.entrySet ( ).stream ( )
                .filter ( e -> e.getValue ( ).contains ( key ) )
                .map ( Map.Entry::getKey )
                .collect ( Collectors.toList ( ) );

        Integer epicenterValue = VALUE_MAP.get ( key );

        return negativePositions.stream ( )
                .map ( position ->
                        (Map<Position, Integer>) visibility.cells ( )
                                .map ( Cell::getPosition )
                                .collect ( Collectors.toMap (
                                        Function.identity ( ) ,
                                        pos -> epicenterValue + position.distance ( pos ) ,
                                        Integer::sum ,HashMap::new ) ) )
                .reduce ( (map1 ,map2) -> CollectionUtils.mergeMaps ( Integer::sum ,map1 ,map2 ) ).orElse ( Collections.emptyMap ( ) );
    }

    private Map<Position, Set<LivingRabbitAttitude>> updateWithUnits (Stream<Animal> units) {
        Map<Position, Set<LivingRabbitAttitude>> positionValues = new HashMap<> ( );
        Map<Position, List<Animal>> positionUnits = units.collect ( Collectors.groupingBy ( Animal::getPosition ) );

        positionUnits.forEach ( (key ,value) -> value.stream ( )
                .map ( InterestUnit::new )
                .forEach ( iu -> {
                    if (iu.asPredatorWolf != null || iu.asPredatorFox != null) {
                        positionValues.computeIfAbsent ( key ,(pos) -> new HashSet<> ( ) ).add ( LivingRabbitAttitude.PREDATOR);
                    }
                    if (iu.asMate != null) {
                        LivingRabbitAttitude attitude = goodPartner ( iu.asMate ) ? LivingRabbitAttitude.MATE : LivingRabbitAttitude.COMPETITOR;
                        positionValues.computeIfAbsent ( key ,(pos) -> new HashSet<> ( ) ).add ( attitude );
                    }
                } ) );

        return positionValues;
    }

    private boolean goodPartner (Rabbit candidate) {
        return candidate.adult ( )
                && candidate.sex != this.sex
                && candidate.pregnancy ( ).isEmpty ( )
                && pregnancy ( ).isEmpty ( );
    }

    @Override
    protected Optional<Food> feed (Visibility visibility) {
        if (!wantsToEat ( )) {
            return Optional.empty ( );
        }
        return visibility.cells ( )
                .filter ( c -> c.getPosition ( ).equals ( getPosition ( ) ) )
                .findAny ( )
                .map ( Cell::getGrass );
    }

    private boolean wantsToEat ( ) {
        return health.part ( ) < 0.5d;
    }

    private static class InterestUnit {

        final Animal asUnit;
        final Rabbit asMate;
        final Wolf asPredatorWolf;
        final Fox asPredatorFox;
        int probabilityToEat = ThreadLocalRandom.current ( ).nextInt ( 0 ,100 );


        InterestUnit (Animal unit) {
            this.asUnit = unit;
            if (unit instanceof Rabbit) {
                this.asMate = (Rabbit) unit;
            } else {
                this.asMate = null;
            }
            if (unit instanceof Wolf && probabilityToEat <= 30) {
                this.asPredatorWolf = (Wolf) unit;
            } else {
                this.asPredatorWolf = null;
            }
            if (unit instanceof Fox && probabilityToEat <= 30) {
                this.asPredatorFox = (Fox) unit;
            } else {
                this.asPredatorFox = null;
            }
        }

    }
}
