package draw;

import model.Wolf;

public class DrawableWolf extends DrawableAnimal<Wolf, WolfDrawer> {

    public DrawableWolf (Wolf object ,WolfDrawer drawer) {
        super ( object ,drawer );
    }

    public Wolf getWolf ( ) {
        return object;
    }
}

