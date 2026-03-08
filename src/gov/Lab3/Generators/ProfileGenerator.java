package gov.Lab3.Generators;

import gov.Lab3.*;

/**
 * The type Profile generator.
 */
public class ProfileGenerator {
    /**
     * Generate profile.
     *
     * @param idGen          the id gen
     * @param nameGen        the name gen
     * @param companyNameGen the company name gen
     * @return the profile
     */
    public static Profile generate(Identifier idGen, Namer nameGen, CompanyNamer companyNameGen){
        return switch ((int)(Math.random()*4)){
            case 0 -> new Company(idGen.getID(), companyNameGen.getName());
            case 1 -> new Person(idGen.getID(), nameGen.getName());
            case 2 -> new Programmer(idGen.getID(), nameGen.getName());
            case 3 -> new Designer(idGen.getID(), nameGen.getName());
            default -> throw new IllegalStateException("Unexpected value");
        };
    }
}
