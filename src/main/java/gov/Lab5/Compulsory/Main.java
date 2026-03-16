package gov.Lab5.Compulsory;

import gov.Lab5.Repository;

import java.awt.*;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Repository repository = new Repository();
        repository.add("knuth67","The Art of Computer Programming","d:/books/programming/tacp.ps").put("year","1967").put("author","Donald E. Knuth");
        repository.add("jvm25", "The Java Virtual Machine Specification", "https://docs.oracle.com/javase/specs/jvms/se25/html/index.html").put("year", "2025").put("author", "Tim Lindholm, Frank Yellin, Gilad Bracha, Alex Buckley");
        repository.add("java25","The Java Language Specification","https://docs.oracle.com/javase/specs/jls/se25/jls25.pdf").put("year","2025").put("author","James Gosling, Bill Joy, Guy Steele, Gilad Bracha, Alex Buckley");
        repository.openSource("jvm25");
        repository.save();
    }
}
