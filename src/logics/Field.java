package logics;

import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Field implements ScentStorage, Size {

    private final static Logger LOGGER = LoggerFactory.getLogger ( Field.class );

    private final int width;
    private final int height;

    private final List<Cell> cells;

    private List<Rabbit> rabbits = new ArrayList<> ( );
    private final List<Rabbit> justBornRabbits = new ArrayList<> ( );

    private List<Wolf> wolves = new ArrayList<> ( );
    private final List<Wolf> justBornWolves = new ArrayList<> ( );

    private List<Fox> foxes = new ArrayList<> ( );
    private final List<Fox> justBornFoxes = new ArrayList<> ( );

    private List<Horse> horses = new ArrayList<> ( );
    private final List<Horse> justBornHorses = new ArrayList<> ( );


    public Field (int width ,int height ,
                  List<Cell> cells) {
        this.width = width;
        this.height = height;
        this.cells = cells;
    }

    public List<Wolf> updateWolves ( ) {
        new ArrayList<> ( wolves ).forEach ( this::unitTurn );
        wolves = wolves.stream ( )
                .filter ( Animal::isAlive )
                .collect ( Collectors.toList ( ) );
        justBornWolves.forEach ( this::addWolf );
        ArrayList<Wolf> backup = new ArrayList<> ( justBornWolves );
        justBornWolves.clear ( );
        return backup;
    }

    public List<Rabbit> updateRabbits ( ) {
        new ArrayList<> ( rabbits ).forEach ( this::unitTurn );
        rabbits = rabbits.stream ( )
                .filter ( Animal::isAlive )
                .collect ( Collectors.toList ( ) );
        justBornRabbits.forEach ( this::addRabbit );
        ArrayList<Rabbit> backup = new ArrayList<> ( justBornRabbits );
        justBornRabbits.clear ( );
        return backup;
    }

    public List<Fox> updateFoxes ( ) {
        new ArrayList<> ( foxes ).forEach ( this::unitTurn );
        foxes = foxes.stream ( )
                .filter ( Animal::isAlive )
                .collect ( Collectors.toList ( ) );
        justBornFoxes.forEach ( this::addFox );
        ArrayList<Fox> backup = new ArrayList<> ( justBornFoxes );
        justBornFoxes.clear ( );
        return backup;
    }

    public List<Horse> updateHorses ( ) {
        new ArrayList<> ( horses ).forEach ( this::unitTurn );
        horses = horses.stream ( )
                .filter ( Animal::isAlive )
                .collect ( Collectors.toList ( ) );
        justBornHorses.forEach ( this::addHorse );
        ArrayList<Horse> backup = new ArrayList<> ( justBornHorses );
        justBornHorses.clear ( );
        return backup;
    }


    public void updateCells ( ) {
        new ArrayList<> ( cells ).forEach ( this::cellTurn );
    }

    private void cellTurn (Cell cell) {
        cell.update ( );
    }

    private void unitTurn (Animal<?> unit) {
        unit.update ( this::visibilityFor );

    }

    public WorldVisibility visibilityFor (Animal<?> unit) {
        return new WorldVisibility (
                Width ( ) ,
                Height ( ) ,
                Stream.of ( rabbits.stream ( ) ,
                        wolves.stream ( ) ,foxes.stream ( ) ,horses.stream ( )
                ).flatMap ( x -> x )
                        .filter ( u -> u != unit )
                        .filter ( u -> u.getPosition ( ).distance ( unit.getPosition ( ) ) <= unit.getSight ( ) )
                        .collect ( Collectors.toList ( ) ) ,
                unit.getPosition ( )
                        .around ( unit.getSight ( ) ,this )
                        .stream ( )
                        .map ( this::cellOn ).collect ( Collectors.toSet ( ) )

        );
    }


    @Override
    public void update (Position position) {
        cellOn ( position ).updateScent ( );
    }

    private Cell cellOn (Position position) {
        return cells.get ( position.getY ( ) * width + position.getX ( ) );
    }

    @Override
    public int Width ( ) {
        return width;
    }

    @Override
    public int Height ( ) {
        return height;
    }

    public int rabbitsTotal ( ) {
        return rabbits.size ( );
    }

    public int wolvesTotal ( ) {
        return wolves.size ( );
    }

    public int foxTotal ( ) {
        return foxes.size ( );
    }

    public int horseTotal ( ) {
        return horses.size ( );
    }

    public void addRabbit (Rabbit rabbit) {
        rabbit.addBirthListener ( justBornRabbits::add );
        rabbits.add ( rabbit );
    }

    public void addWolf (Wolf wolf) {
        wolf.addBirthListener ( justBornWolves::add );
        wolves.add ( wolf );
    }

    public void addFox (Fox fox) {
        fox.addBirthListener ( justBornFoxes::add );
        foxes.add ( fox );
    }

    public void addHorse (Horse horse) {
        horse.addBirthListener ( justBornHorses::add );
        horses.add ( horse );
    }


    public Stream<? extends Animal<? extends Animal<?>>> unitsOn (Position position) {
        return Stream.of (
                new ArrayList<> ( rabbits ).stream ( ) ,
                new ArrayList<> ( wolves ).stream ( ) ,
                new ArrayList<> ( foxes ).stream ( ) ,
                new ArrayList<> ( horses ).stream ( )

        ).flatMap ( x -> x ).filter ( u -> u.getPosition ( ).equals ( position ) );
    }
}
