package model;

import logics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

public abstract class Animal<T extends Animal<T>> {

    private final static Logger LOGGER = LoggerFactory.getLogger( Animal.class);

    private boolean alive = true;

    private final int sight;
    private final int speed;
    private final ScentStorage scentStorage;
    protected final NumberRate age;
    protected final NumberRate health = new NumberRate (0, 100);
    private final List<Consumer<T>> birthListeners = new LinkedList<> ();
    private final List<Class<? extends Food>> canEatWolf;
    private final List<Class<? extends Food>> canEatFox;


    private Position position;

    public Animal (int sight,NumberRate age,int speed,
                   Position position,
                   List<Class<? extends Food>> canEatWolf, List<Class<? extends Food>> canEatFox,
                   ScentStorage scentStorage) {
        this.sight = sight;
        this.speed = speed;
        this.age = age;
        this.position = position;
        this.canEatWolf = canEatWolf;
        this.scentStorage = scentStorage;
        this.canEatFox = canEatFox;
    }

    public int getSight() {
        return sight;
    }

    public Position getPosition() {
        return position;
    }

    private void setPosition(Position position) {
        this.position = position;
    }

    public ScentStorage getScentStorage() {
        return scentStorage;
    }

    public final void update(Function<Animal, Visibility> getVisibility) {
        updateUnitState();

        IntStream.range(0, speed)
                .forEach(step -> {
                    Visibility visibility = getVisibility.apply(this);
                    move(visibility)
                            .filter(p -> p.distance(getPosition()) <= speed)
                            .ifPresent(p -> {
                                if (p.in(visibility)) {
                                    setPosition(p);
                                    leaveScent();
                                }
                            });
                });
        Visibility localVisibility = getVisibility.apply(this).local(getPosition());
        Optional<Food> fed = feed(localVisibility);
        fed.ifPresent(d -> {
            if (d.eaten()) {
                LOGGER.debug("A {}[{}] on {} is eating", getClass().getSimpleName(), health, getPosition());
                health.setCurrent(health.getCurrent() + d.getFoodValue());
            }
        });
        if (health.part() > 0.6d) {
            multiply(localVisibility);
        }
    }

    protected void leaveScent() {
        scentStorage.update(getPosition());
    }

    protected abstract Optional<Position> move(Visibility visibility);

    protected abstract Optional<Food> feed(Visibility visibility);

    protected abstract void multiply(Visibility visibility);

    private void updateUnitState() {
        if (!alive) {
            return;
        }
        age.plus(1);
        updateGauges();
        if (dead()) {
            kill();
        }
    }

    private boolean dead() {
        return health.atMin() || age.atMax();
    }

    public boolean isAlive() {
        return alive;
    }

    public void birth(T animal) {
        birthListeners.forEach(bl -> bl.accept(animal));
    }

    public boolean kill() {
        if (alive) {
            LOGGER.debug("A {} died on {}", getClass().getSimpleName(), getPosition());
            alive = false;
            birthListeners.clear();
            return true;
        }
        return false;
    }

    public void addBirthListener(Consumer<T> listener) {
        birthListeners.add(listener);
    }

    protected abstract void updateGauges();

    public final boolean canEatWolf(Class<? extends Food> foodClass) {
        return canEatWolf.stream().anyMatch(eatable -> eatable.isAssignableFrom(foodClass));
    }
    public final boolean canEatFox(Class<? extends Food> foodClass) {
        return canEatFox.stream().anyMatch(eatable -> eatable.isAssignableFrom(foodClass));
    }

    public NumberRate health() {
        return health;
    }
}
