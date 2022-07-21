package swing;

import com.typesafe.config.Config;
import config.Configuration;
import draw.CellDrawer;
import draw.DrawableField;
import factory.CellFactory;
import factory.DrawableZooFactoryImpl;
import factory.GrassFactory;
import logics.Position;
import logics.UpdateResult;

import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.IntStream;

import static java.awt.RenderingHints.*;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;

public class FieldPanel extends JPanel {

    private static final Config CONF = Configuration.ROOT.getConfig ( "field" );

    public static final int WIDTH = CONF.getInt ( "width" );
    public static final int HEIGHT = CONF.getInt ( "height" );

    private volatile DrawableField field;
    private volatile java.util.Timer timer;

    private Consumer<String> infoConsumer;

    public FieldPanel ( ) {
        super ( true );
        setPreferredSize ( new Dimension ( CellDrawer.SIZE * WIDTH ,CellDrawer.SIZE * HEIGHT ) );
    }

    public void init ( ) {
        field = DrawableField.create ( WIDTH ,HEIGHT ,new CellFactory ( ) ,new GrassFactory ( ) ,new DrawableZooFactoryImpl ( ) );
        IntStream.range ( 0 ,CONF.getInt ( "rabbits" ) ).forEach ( i -> {
            int randX = ThreadLocalRandom.current ( ).nextInt ( WIDTH );
            int randY = ThreadLocalRandom.current ( ).nextInt ( HEIGHT );
            field.addRabbitOn ( Position.on ( randX ,randY ) );
        } );
        IntStream.range ( 0 ,CONF.getInt ( "wolves" ) ).forEach ( i -> {
            int randX = ThreadLocalRandom.current ( ).nextInt ( WIDTH );
            int randY = ThreadLocalRandom.current ( ).nextInt ( HEIGHT );
            field.addWolfOn ( Position.on ( randX ,randY ) );
        } );
        IntStream.range ( 0 ,CONF.getInt ( "foxes" ) ).forEach ( i -> {
            int randX = ThreadLocalRandom.current ( ).nextInt ( WIDTH );
            int randY = ThreadLocalRandom.current ( ).nextInt ( HEIGHT );
            field.addFoxOn ( Position.on ( randX ,randY ) );
        } );
        IntStream.range ( 0 ,CONF.getInt ( "horses" ) ).forEach ( i -> {
            int randX = ThreadLocalRandom.current ( ).nextInt ( WIDTH );
            int randY = ThreadLocalRandom.current ( ).nextInt ( HEIGHT );
            field.addHorseOn ( Position.on ( randX ,randY ) );
        } );

        repaint ( );
    }

    public void setInfoConsumer (Consumer<String> infoConsumer) {
        this.infoConsumer = infoConsumer;
    }

    public void stopOrResume ( ) {
        if (timer != null) {
            timer.cancel ( );
            timer = null;
        } else {
            start ( );
        }
    }

    private void start ( ) {
        timer = new Timer (false);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateGame();
            }
        }, 0, 100);


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(KEY_RENDERING, VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
        paintField(g2d);
    }

    private void paintField (Graphics2D g) {
        field.draw ( g );
    }

    public void updateGame ( ) {
        UpdateResult result = field.update ( );
        repaint ( );
        if (infoConsumer != null) {
            infoConsumer.accept (
                    "Кролики: " + result.getRabbitsTotal ( )
                            + ", Волки: " + result.getWolvesTotal ( ) + ", Лисы: " + result.getFoxTotal ( )
                            + ", Лошади: " + result.getHorseTotal ( ) + ", Время одного цикла: "
                            + result.getElapsed ( ) + "ms" );
        }
    }

    public void putWolfOn (Point point) {
        if (timer == null) {
            field.addRabbitOn ( positionBy ( point ) );
            repaint ( );
        }
    }

    public void putRabbitOn (Point point) {
        if (timer == null) {
            field.addWolfOn ( positionBy ( point ) );
            repaint ( );
        }
    }

    public void putFoxOn (Point point) {
        if (timer == null) {
            field.addFoxOn ( positionBy ( point ) );
            repaint ( );
        }
    }

    public void putHorseOn (Point point) {
        if (timer == null) {
            field.addHorseOn ( positionBy ( point ) );
            repaint ( );
        }
    }

    private Position positionBy (Point point) {
        int x = (int) (point.getX ( ) / CellDrawer.SIZE);
        int y = (int) (point.getY ( ) / CellDrawer.SIZE);
        return Position.on ( x ,y );
    }
}

