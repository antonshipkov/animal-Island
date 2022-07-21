package logics;

public class NumberRate extends Rate<Integer> {

    public NumberRate (Integer current ,Integer min ,Integer max) {
        super ( current ,min ,max );
    }

    public NumberRate (Integer min ,Integer max) {
        super ( max ,min ,max );
    }

    public float part ( ) {
        return getCurrent ( ).floatValue ( ) / getMax ( ).floatValue ( );
    }

    public void plus (int delta) {
        setCurrent ( getCurrent ( ) + delta );
    }

    public void minus (int delta) {
        setCurrent ( getCurrent ( ) - delta );
    }

}
