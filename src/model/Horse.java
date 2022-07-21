package model;

import com.typesafe.config.Config;
import config.Configuration;
import logics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Horse extends Animal<Horse> implements Food {

    private static final Logger LOGGER = LoggerFactory.getLogger ( Horse.class );

    private static final Config CONF = Configuration.ROOT.getConfig ( "horse" );

    private static final int MAX_AGE = CONF.getInt ( "maxAge" );
    private static final int ADULT = CONF.getInt ( "adult" );
    private static final int SPEED = CONF.getInt ( "speed" );
    private static final int PREGNANCY_TIME = CONF.getInt ( "pregnancyTime" );
    private static final int BIRTH_RATE = CONF.getInt ( "birthRate" );
    private static final int FOOD_VALUE = CONF.getInt ( "foodValue" );

    protected final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty ( );

    public Horse (Position position ,int age ,Sex sex ,ScentStorage scentStorage) {
        super ( 3 ,new NumberRate ( age ,0 ,MAX_AGE ) ,SPEED ,position ,Collections.emptyList ( ) ,Collections.emptyList ( ) ,scentStorage );
        this.sex = sex;
    }

    public Optional<Pregnancy> pregnancy ( ) {
        return pregnancy;
    }

    public Sex sex ( ) {
        return sex;
    }

    @Override
    public int getFoodValue ( ) {
        return FOOD_VALUE;
    }

    @Override
    public boolean eaten ( ) {
        return kill ( );
    }

    @Override
    protected void multiply (Visibility visibility) {
        if (this.sex != Sex.FEMALE || !adult ( ) || pregnancy ( ).isPresent ( )) {
            return;
        }
        visibility.units ( )
                .map ( unit -> unit instanceof Horse ? (Horse) unit : null )
                .filter ( Objects::nonNull )
                .filter ( otherHorse -> otherHorse.sex != this.sex && otherHorse.adult ( ) )
                .findAny ( )
                .ifPresent ( mate -> pregnancy = Optional.of ( new Pregnancy ( PREGNANCY_TIME ) ) );
    }

    public boolean adult ( ) {
        return age.getCurrent ( ) >= ADULT;
    }

    protected abstract Horse newHorse ( );

    protected void updateGauges ( ) {
        health.minus ( 1 );
        pregnancy.ifPresent ( p -> {
            if (p.incAndWhelp ( g -> {
                do {
                    birth ( newHorse ( ) );
                    health.minus ( 50 );
                    LOGGER.debug ( "A new horse was born on {}" ,getPosition ( ) );
                } while (ThreadLocalRandom.current ( ).nextDouble ( ) < BIRTH_RATE);
            } )) {
                pregnancy = Optional.empty ( );
            }
        } );
    }
}
