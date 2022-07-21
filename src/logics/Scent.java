package logics;

public class Scent {

    private final NumberRate power = new NumberRate ( 5 ,0 ,5 );

    public void update ( ) {
        power.minus ( 1 );
    }

    public void restore ( ) {
        power.setCurrent ( power.getMax ( ) );
    }

    public Integer get ( ) {
        return power.getCurrent ( );
    }
}
