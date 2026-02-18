package Lab1.Advanced;

import Lab1.Homework.Image;

import java.util.HashMap;

public class Scanner {
    static short determineBG(short[][] matrix){
        var colorCount = new HashMap<Short, Integer>();
        int value;

        colorCount.put(matrix[matrix[0].length - 1][matrix[0].length - 1], 1);

        value = colorCount.get(matrix[0][matrix[0].length - 1]) == null ? 1 : colorCount.get(matrix[0][matrix[0].length - 1]) + 1;
        colorCount.put(matrix[0][matrix[0].length - 1], value);

        value = colorCount.get(matrix[matrix[0].length - 1][0]) == null ? 1 : colorCount.get(matrix[matrix[0].length - 1][0]) + 1;
        colorCount.put(matrix[matrix[0].length - 1][0], value);

        value = colorCount.get(matrix[0][0]) == null ? 1 : colorCount.get(matrix[0][0]) + 1;
        colorCount.put(matrix[0][0], value);

        short bg_color = 0;

        int max = 0;
        for (var entry : colorCount.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
                bg_color = entry.getKey();
            }
        }
        return bg_color;
    }
    public static Rectangle Bounding(Image image) {
        var matrix = image.getMatrix();
        var result = new Rectangle();
        short bg_color = determineBG(matrix);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != bg_color) {
                    if (i < result.x) result.x = i;
                    if (j < result.y) result.y = j;
                    if (i >= result.x + result.width)  result.width = i - result.x + 1;
                    if (j >= result.y + result.height) result.height = j - result.y + 1;
                }
            }
        }
        return result;
    }

    public static class Rectangle {
        int x, y, width, height;
        Rectangle() {
            x = Integer.MAX_VALUE;
            y = Integer.MAX_VALUE;
        }
        @Override
        public String toString() {
            return "{\n\tx: " + x + ",\n\ty: " + y + ",\n\twidth: " + width + ",\n\theight: " + height + "\n}";
        }
    }
}
