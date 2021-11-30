package przychodnialekarska.objectClass;

public class WynikiBadan {
    private final int id;
    private final int idService;
    private final int idVisit;
    private final String peselPatient;
    private final String description;
    private final String result;
    private final String recommendations;
    private final String recipt;

    public WynikiBadan(int id, int idService, int idVisit, String peselPatient, String description, String result, String recommendations, String recipt) {
        this.id = id;
        this.idService = idService;
        this.idVisit = idVisit;
        this.peselPatient = peselPatient;
        this.description = description;
        this.result = result;
        this.recommendations = recommendations;
        this.recipt = recipt;
    }

    public int getId() {
        return id;
    }

    public int getIdService() {
        return idService;
    }

    public int getIdVisit() {
        return idVisit;
    }

    public String getPeselPatient() {
        return peselPatient;
    }

    public String getDescription() {
        return description;
    }

    public String getResult() {
        return result;
    }

    public String getRecommendations() {
        return recommendations;
    }

    public String getRecipt() {
        return recipt;
    }
}
