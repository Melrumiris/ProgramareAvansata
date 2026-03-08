package gov.Lab3;

/**
 * A {@link Person} who is also a visual or UX designer.
 * <p>
 * In addition to the base person data, a {@code Designer} tracks the list of
 * design artefacts they have created.
 * </p>
 */
public class Designer extends Person {

    /**
     * The array of design names created by this designer.
     */
    String[] createdDesigns;

    /**
     * Constructs a {@code Designer} with a pre-populated list of designs.
     *
     * @param ID             the unique numeric identifier
     * @param name           the display name
     * @param createdDesigns the initial array of design names
     */
    public Designer(long ID, String name, String[] createdDesigns) {
        super(ID, name);
        this.createdDesigns = createdDesigns;
    }

    /**
     * Constructs a {@code Designer} with no designs yet.
     *
     * @param ID   the unique numeric identifier
     * @param name the display name
     */
    public Designer(long ID, String name) {
        super(ID, name);
        this.createdDesigns = new String[0];
    }

    /**
     * Returns the array of design names created by this designer.
     *
     * @return non-null array of design name strings (may be empty)
     */
    String[] getCreatedDesigns() {
        return createdDesigns;
    }

    /**
     * Replaces the array of created designs.
     *
     * @param createdDesigns the new array of design names; must not be {@code null}
     */
    void setCreatedDesigns(String[] createdDesigns) {
        this.createdDesigns = createdDesigns;
    }
}
