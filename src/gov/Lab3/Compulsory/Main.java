package gov.Lab3.Compulsory;

import gov.Lab3.Company;
import gov.Lab3.Person;
import gov.Lab3.Profile;

import java.util.Arrays;
import java.util.Random;

public class Main {
    static public void main(String[] args) {
        Random random = new Random();
        Profile[] profiles = new Profile[10];
        for(int i = 0; i < profiles.length; i++) {
            if(i % 2 == 0) {
                profiles[i] = new Person(random.nextLong(), "Person" + i);
            } else {
                profiles[i] = new Company(random.nextLong(), "Company" + i);
            }
        }
        Arrays.sort(profiles);
        for (Profile profile : profiles) {
            System.out.print(profile + " ");
        }
    }
}
