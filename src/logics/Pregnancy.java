package logics;

import java.util.function.Consumer;

public class Pregnancy {

    private final NumberRate pregnancyCounter;

    public Pregnancy (Integer awaitTime) {
        this.pregnancyCounter = new NumberRate ( 0 ,0 ,awaitTime );
    }

    public boolean incAndWhelp (Consumer<NumberRate> birth) {
        pregnancyCounter.plus ( 1 );
        if (pregnancyCounter.atMax ( )) {
            birth.accept ( pregnancyCounter );
            return true;
        }
        return false;
    }
}
