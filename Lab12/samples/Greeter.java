public class Greeter {

    // Private field — displayed in the prototype but not directly invokable
    private String name = "World";

    // Instance no-arg method annotated with @Execute
    @Execute
    public void greet() {
        System.out.println("Hello, " + name + "!");
    }

    // Static no-arg method annotated with @Execute — invoked without an instance
    @Execute
    public static void announce() {
        System.out.println("Greeter is announcing!");
    }
}
