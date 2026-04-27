package pl.projekt.models;

public class Statistics{
    private final String albumNumber;
    private final String firstName;
    private final String lastName;
    private final Integer present;
    private final Integer all;
    private final Double mean;

    public Statistics(String albumNumber, String firstName, String lastName, int present, int all, double mean){
        this.albumNumber = albumNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.present = present;
        this.all = all;
        this.mean = mean;
    }
    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getAlbumNumber() {return albumNumber;}
    public Integer getPresent() {return present;}
    public Integer getAll() {return all;}
    public Double getMean() {return mean;}
}