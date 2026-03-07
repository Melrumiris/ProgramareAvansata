package gov.Lab3;

public class Relationship {
    public enum Type {
        COWORKERS,
        CLASSMATES,
        FAMILY,
        FRIENDS,
        WORKS_FOR_COMPANY,
        WORKED_FOR_COMPANY,
        STUDIED_AT_COMPANY,
        REPRESENTS_COMPANY,
        PARTNER_OF_COMPANY;
    }
    String details;
    Type type;
    public Relationship(String specifications) {
        String[] parts = specifications.split(":");
        switch (parts.length) {
            case 1 -> {
                this.type = Type.valueOf(parts[0].trim().toUpperCase().replace(' ', '_'));
                this.details = "";
            }
            case 2 -> {
                this.type = Type.valueOf(parts[0].trim().toUpperCase().replace(' ', '_'));
                this.details = parts[1].trim();
            }
            default -> throw new IllegalArgumentException("Invalid relationship specification: " + specifications);
        }
    }
}
