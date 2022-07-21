package factory;

import draw.CellDrawer;
import draw.DrawableCell;
import logics.Cell;
import logics.Grass;
import logics.Position;

public class CellFactory {

    private static final CellDrawer DRAWER = new CellDrawer();

    public DrawableCell create(Position position,Grass grass) {
        return new DrawableCell(new Cell (position, grass), DRAWER);
    }
}
