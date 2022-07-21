package logics;

import model.Animal;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorldVisibility implements Visibility {

    private final int width;
    private final int height;
    private final Collection<Animal> units;
    private final Collection<Cell> cells;

    public WorldVisibility(int width,int height,Collection<Animal> units,Collection<Cell> cells) {
        this.width = width;
        this.height = height;
        this.units = units;
        this.cells = cells;
    }

    @Override
    public int Width() {
        return width;
    }

    @Override
    public int Height() {
        return height;
    }

    @Override
    public Stream<Animal> units() {
        return units.stream();
    }

    @Override
    public Stream<Cell> cells() {
        return cells.stream();
    }

    @Override
    public WorldVisibility local(Position p) {

        return new WorldVisibility(
                Width(),
                Height(),
                units().filter(u -> u.getPosition().equals(p)).collect( Collectors.toList()),
                cells().filter(u -> u.getPosition().equals(p)).collect(Collectors.toList())
        );
    }
}
