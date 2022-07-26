package draw;

import factory.CellFactory;
import factory.DrawableZooFactory;
import factory.GrassFactory;
import logics.Cell;
import logics.Field;
import logics.Position;
import logics.UpdateResult;
import model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static draw.CellDrawer.SIZE;

public class DrawableField implements Drawable {

    private final Field field;

    private final DrawableZooFactory drawableZooFactory;

    private final List<DrawableCell> drawableCells;
    private List<DrawableRabbit> drawableRabbits = new ArrayList<> ( );
    private List<DrawableWolf> drawableWolves = new ArrayList<> ( );
    private List<DrawableFox> drawableFoxes = new ArrayList<> ( );
    private List<DrawableHorse> drawableHorses = new ArrayList<> ( );


    private DrawableField (Field field ,DrawableZooFactory drawableZooFactory ,List<DrawableCell> drawableCells) {
        this.field = field;
        this.drawableZooFactory = drawableZooFactory;
        this.drawableCells = drawableCells;
    }

    public static DrawableField create (int width ,int height ,
                                        CellFactory cellFactory ,
                                        GrassFactory grassFactory ,
                                        DrawableZooFactory drawableZooFactory) {
        ArrayList<DrawableCell> drawableCells = new ArrayList<> ( width * height );
        IntStream.range ( 0 ,width * height )
                .forEach ( i -> {
                    Position position = Position.on ( i % width ,i / width );
                    drawableCells.add ( cellFactory.create ( position ,grassFactory.createGrass ( ) ) );
                } );
        List<Cell> cells = drawableCells.stream ( ).map ( DrawableCell::getCell ).collect ( Collectors.toList ( ) );
        Field field = new Field ( width ,height ,cells );
        return new DrawableField ( field ,drawableZooFactory ,drawableCells );
    }

    public UpdateResult update ( ) {
        long st = System.currentTimeMillis ( );
        field.updateCells ( );

        List<Rabbit> newRabbits = field.updateRabbits ( );
        drawableRabbits = drawableRabbits.stream ( ).filter ( du -> du.getRabbit ( ).isAlive ( ) ).collect ( Collectors.toList ( ) );
        newRabbits.forEach ( newRabbit -> drawableRabbits.add ( drawableZooFactory.wrap ( newRabbit ) ) );

        List<Wolf> newWolves = field.updateWolves ( );
        drawableWolves = drawableWolves.stream ( ).filter ( du -> du.getWolf ( ).isAlive ( ) ).collect ( Collectors.toList ( ) );
        newWolves.forEach ( newWolf -> drawableWolves.add ( drawableZooFactory.wrap ( newWolf ) ) );

        List<Fox> newFoxes = field.updateFoxes ( );
        drawableFoxes = drawableFoxes.stream ( ).filter ( du -> du.getFox ( ).isAlive ( ) ).collect ( Collectors.toList ( ) );
        newFoxes.forEach ( newFox -> drawableFoxes.add ( drawableZooFactory.wrap ( newFox ) ) );

        List<Horse> newHorses = field.updateHorses ( );
        drawableHorses = drawableHorses.stream ( ).filter ( du -> du.getHorse ( ).isAlive ( ) ).collect ( Collectors.toList ( ) );
        newHorses.forEach ( newHorse -> drawableHorses.add ( drawableZooFactory.wrap ( newHorse ) ) );


        long end = System.currentTimeMillis ( );
        return new UpdateResult ( end - st ,field.rabbitsTotal ( ) ,field.wolvesTotal ( ) ,field.foxTotal ( ) ,field.horseTotal ( ) );
    }

    @Override
    public void draw (Graphics2D g) {
        drawableCells.forEach ( cell -> cell.draw ( (Graphics2D) g.create (
                cell.getCell ( ).getX ( ) * SIZE ,
                cell.getCell ( ).getY ( ) * SIZE ,
                SIZE - 1 ,
                SIZE - 1 )
        ) );

        Stream.of (
                new ArrayList<> ( drawableRabbits ).stream ( ) ,
                new ArrayList<> ( drawableWolves ).stream ( ) ,
                new ArrayList<> ( drawableFoxes ).stream ( ) ,
                new ArrayList<> ( drawableHorses ).stream ( )

        ).flatMap ( x -> x ).collect (
                Collectors.groupingBy ( du -> du.getAnimal ( ).getPosition ( ) )
        ).forEach ( (position ,units) -> {
                    units.stream ( ).filter ( u -> u.getAnimal ( ) instanceof Wolf ).findAny ( ).ifPresent ( unit ->
                            unit.draw ( (Graphics2D) g.create (
                                    position.getX ( ) * SIZE ,
                                    position.getY ( ) * SIZE ,
                                    SIZE - 1 ,
                                    SIZE - 1 )
                            )
                    );
                    units.stream ( ).filter ( u -> u.getAnimal ( ) instanceof Rabbit ).findAny ( ).ifPresent ( unit ->
                            unit.draw ( (Graphics2D) g.create (
                                    position.getX ( ) * SIZE ,
                                    position.getY ( ) * SIZE ,
                                    SIZE - 1 ,
                                    SIZE - 1 )
                            )

                    );
                    units.stream ( ).filter ( u -> u.getAnimal ( ) instanceof Fox ).findAny ( ).ifPresent ( unit ->
                            unit.draw ( (Graphics2D) g.create (
                                    position.getX ( ) * SIZE ,
                                    position.getY ( ) * SIZE ,
                                    SIZE - 1 ,
                                    SIZE - 1 )
                            )

                    );
                    units.stream ( ).filter ( u -> u.getAnimal ( ) instanceof Horse ).findAny ( ).ifPresent ( unit ->
                            unit.draw ( (Graphics2D) g.create (
                                    position.getX ( ) * SIZE ,
                                    position.getY ( ) * SIZE ,
                                    SIZE - 1 ,
                                    SIZE - 1 )
                            )

                    );
                    if (units.size ( ) > 1) {
                        g.setColor ( Color.BLACK );
                        g.drawString ( Integer.toString ( units.size ( ) ) ,
                                position.getX ( ) * SIZE ,
                                (position.getY ( ) + 1) * SIZE );
                    }
                }
        );
    }

    public void addRabbitOn (Position position) {
        DrawableRabbit drawable = drawableZooFactory.createRabbit ( position ,field );
        field.addRabbit ( drawable.getRabbit ( ) );
        drawableRabbits.add ( drawable );
    }

    public void addWolfOn (Position position) {
        DrawableWolf drawable = drawableZooFactory.createWolf ( position ,field );
        field.addWolf ( drawable.getWolf ( ) );
        drawableWolves.add ( drawable );
    }

    public void addFoxOn (Position position) {
        DrawableFox drawable = drawableZooFactory.createFox ( position ,field );
        field.addFox ( drawable.getFox ( ) );
        drawableFoxes.add ( drawable );
    }

    public void addHorseOn (Position position) {
        DrawableHorse drawable = drawableZooFactory.createHorse ( position ,field );
        field.addHorse ( drawable.getHorse ( ) );
        drawableHorses.add ( drawable );
    }


    public Stream<Animal<?>> unitsOn (Position position) {
        return (Stream<Animal<?>>) field.unitsOn ( position );
    }
}


