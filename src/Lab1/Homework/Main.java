package Lab1.Homework;

public class Main {
    static void circle(Image image, int centerX, int centerY, int radius) {
        System.out.println(image.fillImage(255).generateWhiteCircle(centerX,centerY,radius).toString());
    }
    static void rectangle(Image image, int x, int y, int width, int height) {
        System.out.println(image.fillImage(0).generateBlackRectangle(x,y,width,height).toString());
    }
    public static void main(String[] args) {
        if (args == null || args.length < 2) {
            System.err.println("Invalid number of arguments");
            return;
        }
        int n = Integer.parseInt(args[0]);
        Image image = new Image(n);
        if (args[1].equals("circle")) {
            if (args.length < 5) {
                System.err.println("Invalid number of arguments for circle");
                return;
            }
            circle(image, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]));
        } else if (args[1].equals("rectangle")) {
            if (args.length < 6) {
                System.err.println("Invalid number of arguments for rectangle");
                return;
            }
            rectangle(image, Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]));
        } else {
            System.err.println("Invalid shape");
        }
    }
}
