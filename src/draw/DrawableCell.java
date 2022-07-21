package draw;

import logics.Cell;

public class DrawableCell extends DrawableObject<Cell, CellDrawer> {

    public DrawableCell (Cell object ,CellDrawer drawer) {
        super ( object ,drawer );
    }

    public Cell getCell ( ) {
        return object;
    }
}


