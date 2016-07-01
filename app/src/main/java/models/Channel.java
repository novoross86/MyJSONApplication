package models;


public class Channel {

    private long id;
    private String chName;
    private String chDescription;
    private String chImage;

    public Channel(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getChName() {
        return chName;
    }

    public void setChName(String chName) {
        this.chName = chName;
    }

    public String getChDescription() {
        return chDescription;
    }

    public void setChDescription(String chDescription) {
        this.chDescription = chDescription;
    }

    public String getChImage() {
        return chImage;
    }

    public void setChImage(String chImage) {
        this.chImage = chImage;
    }
}
