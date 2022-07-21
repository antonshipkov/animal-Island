package factory;

import draw.*;
import logics.Position;
import logics.ScentStorage;
import model.Fox;
import model.Horse;
import model.Rabbit;
import model.Wolf;

public class DrawableZooFactoryImpl implements DrawableZooFactory {

    private final RabbitDrawer rabbitDrawer = new RabbitDrawer ( );
    private final WolfDrawer wolfDrawer = new WolfDrawer ( );
    private final FoxDrawer foxDrawer = new FoxDrawer ( );
    private final HorseDrawer horseDrawer = new HorseDrawer ( );
    private final ZooFactory zooFactory = new RegularZooFactory ( );

    @Override
    public DrawableRabbit createRabbit (Position position ,ScentStorage scentStorage) {
        return new DrawableRabbit ( zooFactory.createRabbit ( position ,scentStorage ) ,rabbitDrawer );
    }

    @Override
    public DrawableWolf createWolf (Position position ,ScentStorage scentStorage) {
        return new DrawableWolf ( zooFactory.createWolf ( position ,scentStorage ) ,wolfDrawer );
    }

    @Override
    public DrawableFox createFox (Position position ,ScentStorage scentStorage) {
        return new DrawableFox ( zooFactory.createFox ( position ,scentStorage ) ,foxDrawer );
    }

    @Override
    public DrawableHorse createHorse (Position position ,ScentStorage scentStorage) {
        return new DrawableHorse ( zooFactory.createHorse ( position ,scentStorage ) ,horseDrawer );
    }

    @Override
    public DrawableRabbit wrap (Rabbit rabbit) {
        return new DrawableRabbit ( rabbit ,rabbitDrawer );
    }

    @Override
    public DrawableWolf wrap (Wolf wolf) {
        return new DrawableWolf ( wolf ,wolfDrawer );
    }

    @Override
    public DrawableFox wrap (Fox fox) {
        return new DrawableFox ( fox ,foxDrawer );
    }

    @Override
    public DrawableHorse wrap (Horse horse) {
        return new DrawableHorse ( horse ,horseDrawer );
    }


}

