package logics;

import java.awt.*;

public class ColorUtils {

    private ColorUtils() {
    }

    public static Color modify(Color color,float fraction) {
        int red = Math.round(Math.min(200, color.getRed() + 200 * fraction));
        int green = Math.round(Math.min(200, color.getGreen() + 200 * fraction));
        int blue = Math.round(Math.min(200, color.getBlue() + 200 * fraction));

        int alpha = color.getAlpha();

        return new Color(red, green, blue, alpha);
    }

    public static Color parse(String rgbHex) {
        return new Color(Integer.parseInt(rgbHex, 16));
    }
}
