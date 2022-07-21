package draw;

import model.Fox;

public class DrawableFox extends DrawableAnimal<Fox, FoxDrawer> {

    public DrawableFox (Fox object ,FoxDrawer drawer) {
        super ( object ,drawer );
    }

    public Fox getFox ( ) {
        return object;
    }
}

