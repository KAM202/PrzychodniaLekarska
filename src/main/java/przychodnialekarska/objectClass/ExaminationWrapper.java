package przychodnialekarska.objectClass;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class ExaminationWrapper {

    private Pacjent pacjent;
    private WynikiBadan wynikiBadan;
    private Lekarz lekarz;
    private Wizyta wizyta;
    private Usluga usluga;
    private ArrayList<File> files;

    public ExaminationWrapper(Pacjent pacjent, WynikiBadan wynikiBadan, Lekarz lekarz, Wizyta wizyta, Usluga usluga, ArrayList<File> files) {
        this.pacjent = pacjent;
        this.wynikiBadan = wynikiBadan;
        this.lekarz = lekarz;
        this.wizyta = wizyta;
        this.usluga = usluga;
        this.files = files;
    }

    public String getDescription(){ return wynikiBadan != null ? wynikiBadan.getDescription() : null;}
    public String getResult(){ return wynikiBadan != null ? wynikiBadan.getResult(): null;}
    public String getRecommendations(){ return wynikiBadan != null ? wynikiBadan.getRecommendations() : null;}
    public String getRecipt(){ return wynikiBadan != null ? wynikiBadan.getRecipt() : null;}

    public int getId(){
        return wynikiBadan != null ? wynikiBadan.getId() : null;
    }

    public String getPeselPatient(){
        return pacjent != null ? pacjent.getPesel() : null;
    }
    public String getSurnamePatient() {
        return pacjent != null ? pacjent.getSurname() : null;
    }
    public String getNamePatient() {
        return pacjent != null ? pacjent.getName() : null;
    }

    public String getDoctorName() {
        return lekarz != null ? lekarz.getName() + " " + lekarz.getSurname() : null;
    }
    public String getServiceName() {
        return usluga != null ? usluga.getNameService() : null;
    }
    public String getDate(){
        return wizyta != null ? wizyta.getDate() : null;
    }

    public ArrayList<File> getFiles(){ return files;}
}
