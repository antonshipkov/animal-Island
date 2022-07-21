package draw;

import com.typesafe.config.Config;
import config.Configuration;
import logics.ColorUtils;
import logics.Sex;
import model.Fox;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FoxDrawer implements Drawer<Fox> {

    private static final Config CONF = Configuration.ROOT.getConfig("fox.draw");

    private static final int FETUS_SIZE = CONF.getInt("fetus.size");
    private static final int SIZE = CONF.getInt("size");
    private static final Color MALE_COLOR = ColorUtils.parse(CONF.getString("male.color"));
    private static final Color FEMALE_COLOR = ColorUtils.parse(CONF.getString("female.color"));

    private static BufferedImage IMG;

    static {
        try {
            URL resource = Fox.class.getClassLoader().getResource( "resources/fox.png" );
            IMG = resource == null ? null : ImageIO.read(resource);
        } catch (IOException e) {
            IMG = null;
        }
    }

    @Override
    public void draw(Fox fox, Graphics2D g) {
        int size = fox.adult() ? SIZE : (SIZE / 2);

        int xPadding = (int) (g.getClipBounds().getWidth() - size) / 2 + 1;
        int yPadding = (int) (g.getClipBounds().getHeight() - size) / 2 + 1;

        g.drawImage(IMG, xPadding, yPadding, size - 2, size - 2, null);

        g.setColor(bySex(fox.sex()));
        g.fillRect(1, 1, (int) ((g.getClipBounds().getWidth() - 1) * fox.health().part()), 2);

        fox.pregnancy().ifPresent(gauge -> g.fillRect(
                1,
                (int) g.getClipBounds().getHeight() - FETUS_SIZE,
                FETUS_SIZE - 2,
                FETUS_SIZE - 2));
    }

    private Color bySex(Sex sex) {
        return sex == Sex.FEMALE ? FEMALE_COLOR : MALE_COLOR;
    }
}


