package draw;

import model.Animal;

public class DrawableAnimal <T extends Animal,D extends Drawer<T>> extends DrawableObject<T, D> {

    public DrawableAnimal (T object ,D drawer) {
        super ( object ,drawer );
    }

    public T getAnimal ( ) {
        return object;
    }
}

