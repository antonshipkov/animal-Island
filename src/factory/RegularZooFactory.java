package factory;

import com.typesafe.config.Config;
import config.Configuration;
import logics.Position;
import logics.ScentStorage;
import logics.Sex;
import model.*;

import java.util.concurrent.ThreadLocalRandom;

public class RegularZooFactory implements ZooFactory {

    private static final Config RABBIT_CONF = Configuration.ROOT.getConfig ( "rabbit" );
    private static final Config WOLF_CONF = Configuration.ROOT.getConfig ( "wolf" );
    private static final Config FOX_CONF = Configuration.ROOT.getConfig ( "fox" );
    private static final Config HORSE_CONF = Configuration.ROOT.getConfig ( "horse" );

    public static final int RABBIT_MAX_AGE = RABBIT_CONF.getInt ( "maxAge" );
    public static final int WOLF_MAX_AGE = WOLF_CONF.getInt ( "maxAge" );
    public static final int FOX_MAX_AGE = FOX_CONF.getInt ( "maxAge" );
    public static final int HORSE_MAX_AGE = HORSE_CONF.getInt ( "maxAge" );


    @Override
    public Rabbit createRabbit (Position position ,ScentStorage scentStorage) {
        return new LivingRabbit ( position ,randomAge ( RABBIT_MAX_AGE ) ,Sex.random ( ) ,scentStorage );
    }

    @Override
    public Wolf createWolf (Position position ,ScentStorage scentStorage) {
        return new LivingWolf ( position ,randomAge ( WOLF_MAX_AGE ) ,Sex.random ( ) ,scentStorage );
    }

    @Override
    public Fox createFox (Position position ,ScentStorage scentStorage) {
        return new LivingFox ( position ,randomAge ( FOX_MAX_AGE ) ,Sex.random ( ) ,scentStorage );
    }

    @Override
    public Horse createHorse (Position position ,ScentStorage scentStorage) {
        return new LivingHorse ( position ,randomAge ( HORSE_MAX_AGE ) ,Sex.random ( ) ,scentStorage );
    }


    private int randomAge (int max) {
        return ThreadLocalRandom.current ( ).nextInt ( max / 2 );
    }
}
