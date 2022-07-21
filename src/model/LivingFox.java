package model;

import logics.*;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LivingFox extends Fox {

    private static final Map<LivingFoxAttitude, Function<LivingFox, Integer>> VALUE_MAP
            = new EnumMap<> ( LivingFoxAttitude.class);

    static {
        VALUE_MAP.put( LivingFoxAttitude.COMPETITOR,w -> -5);
        VALUE_MAP.put( LivingFoxAttitude.MATE,w -> w.wantsToMultiply() ? 15 : 0);
        VALUE_MAP.put( LivingFoxAttitude.FOOD,w -> w.wantsToEat() ? 10 : 0);
    }

    public LivingFox (Position position ,int age ,Sex sex ,ScentStorage scentStorage) {
        super(position, age, sex, scentStorage);
    }

    @Override
    protected Optional<Food> feed(Visibility visibility) {
        if (this.wantsToEat()) {
            return visibility.units()
                    .map(unit -> unit instanceof Food ? (Food) unit : null)
                    .filter( Objects::nonNull)
                    .filter(unit -> this.canEatFox(unit.getClass()))
                    .findAny();
        }
        return Optional.empty();
    }

    private boolean wantsToEat() {
        return health.part() < 0.5d;
    }

    private boolean wantsToMultiply() {
        return pregnancy().isEmpty () && adult();
    }

    @Override
    protected Optional<Position> move(Visibility visibility) {
        Map<Position, Set<LivingFoxAttitude>> positionValues = updateWithUnits(visibility.units());
        Map<Position, Integer> cellValues = updateWithCells(visibility.cells());

        Map<Position, Integer> competitorValueMap = updateProliferatingValues(visibility,positionValues);

        Map<Position, Integer> positionValueMap = positionValues.entrySet().stream()
                .collect( Collectors.toMap(Map.Entry::getKey,e -> e.getValue().stream()
                        .mapToInt(attr -> VALUE_MAP.get(attr).apply(this)).sum()));
        cellValues.forEach ( (key ,value) -> positionValueMap.merge ( key ,value ,Integer::sum ) );

        return CollectionUtils.mergeMaps(Integer::sum, positionValueMap, competitorValueMap).entrySet().stream()
                .max( Comparator.comparingInt ( Map.Entry::getValue ) )
                .map(max -> {
                    List<Direction> availableDirections = Direction.shuffledValues()
                            .filter(dir -> getPosition ( ).by ( dir ).adjustableIn ( 0 ,0 ,visibility.Width ( ) ,visibility.Height ( ) ) )
                            .collect(Collectors.toList());
                    return getPosition().inDirectionTo(max.getKey(), availableDirections);
                });
    }

    private Map<Position, Set<LivingFoxAttitude>> updateWithUnits(Stream<Animal> units) {
        Map<Position, Set<LivingFoxAttitude>> positionValues = new HashMap<>();
        Map<Position, List<Animal>> positionUnits = units.collect(Collectors.groupingBy( Animal::getPosition));

        positionUnits.forEach ( (key ,value) -> value.stream ( )
                .map ( InterestUnit::new )
                .forEach ( iu -> {
                    if (iu.asFood != null) {
                        positionValues.computeIfAbsent ( key ,(pos) -> new HashSet<> ( ) ).add ( LivingFoxAttitude.FOOD );
                    }
                    if (iu.asMate != null) {
                        LivingFoxAttitude attitude = goodPartner ( iu.asMate ) ? LivingFoxAttitude.MATE : LivingFoxAttitude.COMPETITOR;
                        positionValues.computeIfAbsent ( key ,(pos) -> new HashSet<> ( ) ).add ( attitude );
                    }
                } ) );

        return positionValues;
    }

    private Map<Position, Integer> updateProliferatingValues (Visibility visibility ,
                                                              Map<Position, Set<LivingFoxAttitude>> positionValues) {
        List<Position> negativePositions = positionValues.entrySet().stream()
                .filter(e -> e.getValue().contains( LivingFoxAttitude.COMPETITOR ))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        Integer epicenterValue = VALUE_MAP.get( LivingFoxAttitude.COMPETITOR ).apply(this);

        return negativePositions.stream()
                .map(position ->
                        (Map<Position, Integer>) visibility.cells()
                                .map(Cell::getPosition)
                                .collect(Collectors.toMap(
                                        Function.identity(),
                                        pos -> epicenterValue + position.distance(pos),
                                        Integer::sum, HashMap::new)))
                .reduce((map1, map2) -> CollectionUtils.mergeMaps(Integer::sum, map1, map2)).orElse(Collections.emptyMap());
    }

    private boolean goodPartner(Fox candidate) {
        return candidate.adult()
                && candidate.sex != this.sex
                && candidate.pregnancy().isEmpty ()
                && pregnancy().isEmpty ();
    }

    private Map<Position, Integer> updateWithCells(Stream<Cell> cells) {
        return cells.collect(Collectors.toMap(Cell::getPosition, c -> c.getScent().get() / 2));
    }

    protected final LivingFox newFox() {
        return new LivingFox (getPosition(), 0, Sex.random(), getScentStorage());
    }

    private static class InterestUnit {

        final Animal asUnit;
        final Food asFood;
        final Fox asMate;

        InterestUnit(Animal unit) {
            this.asUnit = unit;
            if (unit instanceof Food) {
                this.asFood = (Food) unit;
            } else {
                this.asFood = null;
            }
            if (unit instanceof Fox) {
                this.asMate = (Fox) unit;
            } else {
                this.asMate = null;
            }
        }
    }
}
