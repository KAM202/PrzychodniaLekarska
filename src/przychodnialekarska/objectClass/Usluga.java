package przychodnialekarska.objectClass;

public class Usluga {

    private int idService;
    private String nameService;
    private String descriptionService;
    private double costService;

    public Usluga(int idService, String nameService, String descriptionService, double costService) {
        this.idService = idService;
        this.nameService = nameService;
        this.descriptionService = descriptionService;
        this.costService = costService;
    }

    public int getIdService() {
        return idService;
    }

    public String getNameService() {
        return nameService;
    }

    public String getDescriptionService() {
        return descriptionService;
    }

    public double getCostService() {
        return costService;
    }

    public void setNameService(String nameService) {
        this.nameService = nameService;
    }

    public void setDescriptionService(String descriptionService) {
        this.descriptionService = descriptionService;
    }

    public void setCostService(double costService) {
        this.costService = costService;
    }
}
