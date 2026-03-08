package gov.Lab1.Homework;

import java.util.Arrays;

/**
 * The type Image.
 */
public class Image {
    private final short[][] image;

    /**
     * Instantiates a new Image.
     *
     * @param n the n
     */
    Image(int n) {
        image = new short[n][n];
    }

    /**
     * Get matrix short [ ] [ ].
     *
     * @return the short [ ] [ ]
     */
    public short[][] getMatrix() {
        return image;
    }

    /**
     * Generate black rectangle image.
     *
     * @param x      the x
     * @param y      the y
     * @param width  the width
     * @param height the height
     * @return the image
     */
    public Image generateBlackRectangle(int x, int y, int width, int height) {
        if (x < 0)            x = 0;
        if (y < 0)            y = 0;
        if (x + width > image.length)  width = image.length - x;
        if (y + height > image[0].length) height = image[0].length - y;

        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                image[i][j] = 255;
            }
        }return this;
    }

    /**
     * Generate white circle image.
     *
     * @param centerX the center x
     * @param centerY the center y
     * @param radius  the radius
     * @return the image
     */
    public Image generateWhiteCircle(int centerX, int centerY, int radius) {
        for (int i = 0; i < image.length; i++) {
            for (int j = 0; j < image[i].length; j++) {
                if (Math.pow(i - centerX, 2) + Math.pow(j - centerY, 2) <= Math.pow(radius, 2)) {
                    image[i][j] = 0;
                }
            }
        }return this;
    }

    /**
     * Fill image image.
     *
     * @param value the value
     * @return the image
     */
    public Image fillImage(int value) {
        for (short[] colors : image) {
            Arrays.fill(colors, (short) value);
        }return this;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (short[] colors : image) {
            for (short rColor : colors) {
                if (rColor <= 0) {
                    result.append("█");
                } else if (rColor <= 96) {
                    result.append("▓");
                } else if (rColor <= 164) {
                    result.append("▒");
                } else if (rColor < 255) {
                    result.append("░");
                } else {
                    result.append(" ");
                }
            }
            result.append("\n");
        }
        return result.toString();
    }
}
