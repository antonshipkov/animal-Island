package draw;

import model.Horse;

public class DrawableHorse extends DrawableAnimal<Horse, HorseDrawer> {

    public DrawableHorse (Horse object ,HorseDrawer drawer) {
        super ( object ,drawer );
    }

    public Horse getHorse ( ) {
        return object;
    }
}

