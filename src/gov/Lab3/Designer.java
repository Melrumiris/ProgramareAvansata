package gov.Lab3;

public class Designer extends Person{
    String[] createdDesigns;
    public Designer(long ID, String name, String[] createdDesigns) {
        super(ID, name);
        this.createdDesigns = createdDesigns;
    }
    public Designer(long ID, String name) {
        super(ID, name);
        this.createdDesigns = new String[0];
    }
    String[] getCreatedDesigns() {
        return createdDesigns;
    }
    void setCreatedDesigns(String[] createdDesigns) {
        this.createdDesigns = createdDesigns;
    }
}
