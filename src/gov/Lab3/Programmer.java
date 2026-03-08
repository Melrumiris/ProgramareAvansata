package gov.Lab3;

import java.util.function.Function;

/**
 * A {@link Person} who is also a software programmer.
 * <p>
 * In addition to everything a regular person can do, a {@code Programmer} can
 * execute arbitrary code via {@link #executeCode}.
 * </p>
 */
public class Programmer extends Person {

    /**
     * Constructs a new {@code Programmer} with the given ID and name.
     *
     * @param ID   the unique numeric identifier
     * @param name the display name
     */
    public Programmer(long ID, String name) {
        super(ID, name);
    }

    /**
     * Applies the supplied function to the given input and returns the result.
     * <p>
     * This models the ability of a programmer to execute code.
     * </p>
     *
     * @param <T>   the type of the function's input
     * @param <R>   the type of the function's output
     * @param code  the function to execute
     * @param input the argument passed to the function
     * @return the result of {@code code.apply(input)}
     */
    public <T, R> R executeCode(Function<T, R> code, T input) {
        return code.apply(input);
    }
}
