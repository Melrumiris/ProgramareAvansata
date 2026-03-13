package gov.Lab4;

import com.github.javafaker.Faker;

public class FakerTest {
    static public void main(){
        Faker faker = new Faker();
        for (int i = 0; i < 10; i++) {
            System.out.println(faker.address().streetAddress());
        }
    }
}
