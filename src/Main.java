import swing.WindowPanel;

import java.awt.*;

public class Main {
    public static void main (String[] args) {

        EventQueue.invokeLater ( new Runnable ( ) {
            @Override
            public void run ( ) {
                WindowPanel window = new WindowPanel ( );
                window.init ( );
            }
        } );

    }
}
