package pom.rewrite.screen.click;

import io.wispforest.owo.ui.core.Color;
import io.wispforest.owo.ui.core.OwoUIGraphics;
import io.wispforest.owo.ui.core.Surface;
import net.minecraft.util.Mth;

public class ScreenUtil {


    private static final int thickness = 2;

    public static Surface customSurface(Color color, boolean invert) {

        return ((context, component) -> fillAndOutline(context, color, component.x(), component.y(), component.width(), component.height(), invert ? -.3f : .3f));
    }

    public static Surface customSurface(Color color) {
        return customSurface(color, false);
    }

    public static void fillAndOutline(OwoUIGraphics context, Color color, int x, int y, int width, int height) {
        fillAndOutline(context, color, x, y, width, height, .3f);
    }

    public static void fillAndOutline(OwoUIGraphics context, Color color, int x, int y, int width, int height, float factor) {

        Color lighter = brighten(color, 1 + factor);
        Color darker = darken(color, 1 - factor);

        // Fill solid color
        context.fill(x, y, x + width, y + height, color.argb());

        // Lighter lines
        context.drawLine(x, y, x, y + height, thickness, lighter);
        context.drawLine(x, y, x + width, y,  thickness, lighter);

        // Darker lines
        context.drawLine(x, y + height , x + width , y + height , thickness, darker);
        context.drawLine(x + width , y, x + width , y + height, thickness, darker);
    }

    private static int margin = 3;
    public static void drawSwitch(OwoUIGraphics context, Color backgroundColor, Color buttonColor, int x, int y, int width, int height, boolean enabled) {
        fillAndOutline(context, backgroundColor, x, y + height / 3, width, height / 3, -.3f);

        int size = height;
        int dY = y;
        int dX = enabled
                ? (x + width - size / 4)
                : (x);

        fillAndOutline(context, buttonColor, dX, dY, size / 4, size, .3f);
    }

    public static Color adjustBrightness(Color color, float factor) {
        float r = color.red();
        float g = color.green();
        float b = color.blue();

        float cmax = Math.max(Math.max(r, g), b);
        float cmin = Math.min(Math.min(r, g), b);
        float diff = cmax - cmin;

        float hue = 0f;
        float saturation = 0f;
        float value = cmax;

        if (cmax != 0) {
            saturation = diff / cmax;
        }

        if (saturation != 0) {
            if (r == cmax) {
                hue = (g - b) / diff;
            } else if (g == cmax) {
                hue = 2.0f + (b - r) / diff;
            } else {
                hue = 4.0f + (r - g) / diff;
            }

            hue = hue / 6.0f;
            if (hue < 0) {
                hue += 1.0f;
            }
        }

        float newValue = Math.max(0.0f, Math.min(1.0f, value * factor));

        int rgbInt = Mth.hsvToRgb(hue, saturation, newValue);

        return new Color(
                ((rgbInt >> 16) & 0xFF) / 255f,
                ((rgbInt >> 8) & 0xFF) / 255f,
                (rgbInt & 0xFF) / 255f,
                color.alpha()
        );
    }
        public static Color brighten(Color color, float factor) {
            return adjustBrightness(color, factor);
        }

        public static Color darken(Color color, float factor) {
            return adjustBrightness(color, factor);
        }
    }
