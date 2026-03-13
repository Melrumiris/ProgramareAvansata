package gov.Lab4;

import com.github.javafaker.Faker;

import java.util.function.Supplier;

public class StreetSupplier implements Supplier<Street> {
    private static StreetSupplier instance;
    Faker faker = new Faker();
    public static StreetSupplier getInstance() {
        if (instance == null){
            instance = new StreetSupplier();
        }
        return instance;
    }

    private StreetSupplier() {}

    @Override
    public Street get() {
        return new Street(faker.address().streetAddress(), faker.number().numberBetween(80, 10000));
    }
}
