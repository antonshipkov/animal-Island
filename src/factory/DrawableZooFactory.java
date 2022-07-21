package factory;

import draw.DrawableFox;
import draw.DrawableHorse;
import draw.DrawableRabbit;
import draw.DrawableWolf;
import logics.Position;
import logics.ScentStorage;
import model.Fox;
import model.Horse;
import model.Rabbit;
import model.Wolf;

public interface DrawableZooFactory {

    DrawableRabbit createRabbit(Position position,ScentStorage scentStorage);

    DrawableWolf createWolf(Position position,ScentStorage scentStorage);

    DrawableFox createFox(Position position,ScentStorage scentStorage);

    DrawableHorse createHorse(Position position,ScentStorage scentStorage);

    DrawableRabbit wrap(Rabbit rabbit);

    DrawableWolf wrap(Wolf wolf);

    DrawableFox wrap (Fox fox);

    DrawableHorse wrap (Horse horse);
}
