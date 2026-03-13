package gov.Lab4;

import com.github.javafaker.Faker;

import java.util.function.Supplier;

public class IntersectionSupplier implements Supplier<Intersection> {
    private static IntersectionSupplier instance;
    Faker faker = new Faker();
    public static IntersectionSupplier getInstance() {
        if (instance == null){
            instance = new IntersectionSupplier();
        }
        return instance;
    }

    private IntersectionSupplier() {}

    @Override
    public Intersection get() {
        return new Intersection(faker.address().streetAddress());
    }
}
