package gov.Lab3;

import java.util.function.Function;

public class Programmer extends Person {
    public Programmer(long ID, String name) {
        super(ID, name);
    }
    public <T,R> R executeCode(Function<T,R> code, T input){
        return code.apply(input);
    }
}
