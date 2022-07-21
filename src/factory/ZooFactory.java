package factory;

import logics.Position;
import logics.ScentStorage;
import model.Fox;
import model.Horse;
import model.Rabbit;
import model.Wolf;

public interface ZooFactory {
    Rabbit createRabbit (Position position ,ScentStorage scentStorage);

    Wolf createWolf (Position position ,ScentStorage scentStorage);

    Fox createFox (Position position ,ScentStorage scentStorage);

    Horse createHorse (Position position ,ScentStorage scentStorage);
}
