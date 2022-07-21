package logics;

import model.Animal;

import java.util.stream.Stream;

public interface Visibility extends Size {

    int Width();

    int Height();

    Stream<Animal> units();

    Stream<Cell> cells();

    Visibility local(Position p);
}
