package gov.Lab1.Compulsory;

/**
 * The type Main.
 */
public class Main {
    /**
     * Nr to digit int.
     *
     * @param n the n
     * @return the int
     */
    static int nrToDigit(int n) {
        while (n > 9){
            int sum = 0;
            while (n > 0) {
                int digit = n % 10;
                n /= 10;
                sum += digit;
            }n = sum;
        }
        return n;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        System.out.println("Hello World!");

        String[] Languages = {"C", "C++", "C#", "Python", "Go", "Rust", "JavaScript", "PHP", "Swift", "Java"};

        int n = (int) (Math.random() * 1_000_000);
        n = (n * 3 + 0b10101 + 0xFF) * 6;

        System.out.println("Willy-nilly, this semester I will learn " + Languages[nrToDigit(n)]);
    }
}