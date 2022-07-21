package factory;

import config.Configuration;
import logics.Grass;

public class GrassFactory {

    protected static final com.typesafe.config.Config CONF = Configuration.ROOT.getConfig("grass");

    private static final int FOOD_VALUE = CONF.getInt("foodValue");
    private static final int MAX = CONF.getInt("max");
    private static final int INCREASE_RATE = CONF.getInt("increaseRate");

    public Grass createGrass() {
        return new Grass(FOOD_VALUE, MAX, INCREASE_RATE);
    }
}
