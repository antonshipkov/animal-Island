package draw;

import model.Rabbit;

public class DrawableRabbit extends DrawableAnimal<Rabbit, RabbitDrawer> {

    public DrawableRabbit (Rabbit object ,RabbitDrawer drawer) {
        super ( object ,drawer );
    }

    public Rabbit getRabbit ( ) {
        return object;
    }
}

