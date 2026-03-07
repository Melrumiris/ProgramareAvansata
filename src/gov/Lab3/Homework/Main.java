package gov.Lab3.Homework;

import gov.Lab3.*;
import gov.Lab3.Generators.CompanyNamer;
import gov.Lab3.Generators.Identifier;
import gov.Lab3.Generators.Namer;
import gov.Lab3.Generators.ProfileGenerator;

public class Main {
    public static void main(String[] args) {
        Network network = new Network();
        for (int i = 0; i < 10; i++) {
            network.addProfile(ProfileGenerator.generate(Identifier.getInstance(), Namer.getInstance(), CompanyNamer.getInstance()));
        }

        Profile[] profiles = network.getProfiles().toArray(new Profile[0]);
        for (int i = 0; i < 30; i++) {
            Profile p1 = profiles[(int)(Math.random()*profiles.length)];
            Profile p2 = profiles[(int)(Math.random()*profiles.length)];
            if (p1.getClass() == Company.class || p2.getClass() == Company.class) {
                switch ((int)(Math.random()*4)) {
                    case 0 -> network.addRelation(p1, p2, new Relationship("Works for company"));
                    case 1 -> network.addRelation(p1, p2, new Relationship("Worked for company"));
                    case 2 -> network.addRelation(p1, p2, new Relationship("Represents company"));
                    case 3 -> network.addRelation(p1, p2, new Relationship("Partner of company"));
                }
            }else{
                switch ((int)(Math.random()*4)) {
                    case 0 -> network.addRelation(p1, p2, new Relationship("Coworkers: " + (int)(Math.random()*10) + " years"));
                    case 1 -> network.addRelation(p1, p2, new Relationship("Classmates: " + (int)(Math.random()*10) + " years"));
                    case 2 -> network.addRelation(p1, p2, new Relationship("Family: " + (int)(Math.random()*10) + " years"));
                    case 3 -> network.addRelation(p1, p2, new Relationship("Friends: " + (int)(Math.random()*10) + " years"));
                }
            }
        }
        System.out.println(network);
    }
}
