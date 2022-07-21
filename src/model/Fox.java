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

public abstract class Fox extends Animal<Fox> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Fox.class);

    protected static final Config CONF =  Configuration.ROOT.getConfig("fox");
    protected static final int SIGHT = CONF.getInt("sight");
    protected static final int SPEED = CONF.getInt("speed");
    protected static final int MAX_AGE = CONF.getInt("maxAge");
    protected static final double BIRTH_RATE = CONF.getDouble("birthRate");
    protected static final int ADULT = CONF.getInt("adult");
    protected static final int PREGNANCY_TIME = CONF.getInt("pregnancyTime");

    protected final Sex sex;
    private Optional<Pregnancy> pregnancy = Optional.empty();

    public Fox(Position position,int age,Sex sex,ScentStorage scentStorage) {
        super(SIGHT, new NumberRate (age, 0, MAX_AGE), SPEED, position,
                Collections.singletonList(Rabbit.class),Collections.singletonList(Horse.class), scentStorage);
        this.sex = sex;
    }

    public Optional<Pregnancy> pregnancy() {
        return pregnancy;
    }

    public Sex sex() {
        return sex;
    }

    @Override
    protected void multiply(Visibility visibility) {
        if (this.sex != Sex.FEMALE || !adult() || pregnancy().isPresent()) {
            return;
        }
        visibility.units()
                .map(unit -> unit instanceof Fox ? (Fox) unit : null)
                .filter( Objects::nonNull)
                .filter(otherFox -> otherFox.sex != this.sex && otherFox.adult())
                .findAny()
                .ifPresent(mate -> pregnancy = Optional.of(new Pregnancy(PREGNANCY_TIME)));
    }

    public final boolean adult() {
        return this.age.getCurrent() >= ADULT;
    }

    protected void updateGauges() {
        health.minus(1);
        pregnancy.ifPresent(p -> {
            if (p.incAndWhelp(g -> {
                do {
                    birth(newFox());
                    LOGGER.debug("A new fox was born on {}", getPosition());
                } while (ThreadLocalRandom.current().nextDouble() < BIRTH_RATE);
            })) {
                pregnancy = Optional.empty();
            }
        });
    }

    @Override
    protected void leaveScent() {
    }

    protected abstract Fox newFox();
}
