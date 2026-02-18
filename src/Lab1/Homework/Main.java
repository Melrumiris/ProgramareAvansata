package Lab1.Homework;

import Lab1.Advanced.Scanner;

public class Main {
    static String circle(Image image, int centerX, int centerY, int radius) {
        return image.fillImage(255).generateWhiteCircle(centerX,centerY,radius).toString();
    }
    static String rectangle(Image image, int x, int y, int width, int height) {
        return image.fillImage(0).generateBlackRectangle(x,y,width,height).toString();
    }
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Invalid number of arguments");
            return;
        }
        int index = 0;
        boolean test = false;

        if (args[0].equals("test")) { index = 1; test = true; }

        int n = Integer.parseInt(args[index++]);

        var timer = System.currentTimeMillis();
        Image image = new Image(n);

        if (args[index].equals("circle")) {
            if (args.length <= 3 + index) {
                System.err.println("Invalid number of arguments for circle");
                return;
            }
            if (test) {
                circle(image, Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index]));
                System.out.println((System.currentTimeMillis() - timer) + " ms");
            } else
                System.out.println(circle(image, Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index])));
        }
        else if (args[index].equals("rectangle")) {
            if (args.length <= 4 + index) {
                System.err.println("Invalid number of arguments for rectangle");
                return;
            }
            if (test) {
                rectangle(image, Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index]));
                System.out.println((System.currentTimeMillis() - timer) + "ms");
            } else
                System.out.println(rectangle(image, Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index]), Integer.parseInt(args[++index])));
        } else {
            System.err.println("Invalid shape: " + args[index]);
            return;
        }
        if (args.length > ++index){
            if (args[index].equals("bounded")) {
                System.out.println("Bounding Box " + Scanner.Bounding(image));
            }else {
                System.err.println("Invalid argument: " + args[index]);
            }
        }
    }
}
