package gov.Lab3;

/**
 * Describes the nature of a connection between two {@link Profile} instances.
 * <p>
 * A relationship combines a {@link Type} (e.g. {@code FRIENDS}, {@code WORKS_FOR_COMPANY})
 * with an optional free-text {@code details} string (e.g. "3 years").
 * </p>
 * <p>
 * Relationships are created from a specification string in one of two formats:
 * <ul>
 *   <li>{@code "TYPE"} — just the type, no details</li>
 *   <li>{@code "TYPE: details"} — type and additional details separated by a colon</li>
 * </ul>
 * Type names are case-insensitive and may use spaces instead of underscores.
 * </p>
 *
 * <p><b>Example:</b></p>
 * <pre>{@code
 *   new Relationship("Coworkers: 5 years")
 *   new Relationship("Family")
 * }*</pre>
 */
public class Relationship {

    /**
     * Enumeration of all recognised relationship types between profiles.
     */
    public enum Type {
        /**
         * Two people work at the same company.
         */
        COWORKERS,
        /**
         * Two people attended the same school or class.
         */
        CLASSMATES,
        /**
         * Two people are related by blood or marriage.
         */
        FAMILY,
        /**
         * Two people are friends.
         */
        FRIENDS,
        /**
         * A person currently works for a company.
         */
        WORKS_FOR_COMPANY,
        /**
         * A person previously worked for a company.
         */
        WORKED_FOR_COMPANY,
        /**
         * A person studied at an educational company/institution.
         */
        STUDIED_AT_COMPANY,
        /**
         * A person officially represents a company.
         */
        REPRESENTS_COMPANY,
        /**
         * Two companies are business partners.
         */
        PARTNER_OF_COMPANY;
    }

    /**
     * Optional free-text description (e.g. duration).
     */
    String details;
    /**
     * The category of this relationship.
     */
    Type type;

    /**
     * Constructs a {@code Relationship} by parsing a specification string.
     *
     * @param specifications the specification in {@code "TYPE"} or {@code "TYPE: details"} format
     * @throws IllegalArgumentException if the specification cannot be parsed or the type is unknown
     */
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

    /**
     * Returns the type of this relationship.
     *
     * @return the relationship type; never {@code null}
     */
    public Type getType() {
        return type;
    }

    /**
     * Returns additional details about this relationship.
     *
     * @return details string; empty string if none were provided
     */
    public String getDetails() {
        return details;
    }

    /**
     * Returns a human-readable representation of this relationship.
     *
     * @return formatted string, e.g. {@code "FRIENDS: 3 years"} or {@code "FAMILY"}
     */
    @Override
    public String toString() {
        return details.isEmpty() ? type.toString() : type + ": " + details;
    }
}
