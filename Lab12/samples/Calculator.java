// Demo class showcasing both supported annotation/invocation patterns:
//   - @Execute on a no-argument method
//   - @Compute on a single-integer-argument method
// Also has a non-annotated method to verify it is correctly skipped.
public class Calculator {

    private int result = 0;

    // No-argument method — will be discovered via @Execute and invoked directly
    @Execute
    public void add() {
        result += 10;
        System.out.println("Calculator.add()  →  result = " + result);
    }

    @Compute(count = 3)
    public void multiply(int factor) {
        result *= factor;
        System.out.println("Calculator.multiply(" + factor + ")  →  result = " + result);
    }

    // Not annotated — should be silently skipped by the framework
    public int getResult() {
        return result;
    }
}
