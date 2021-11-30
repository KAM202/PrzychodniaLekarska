package przychodnialekarska.objectClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VisitWrapper {

    private Pacjent pacjent;
    private Wizyta wizyta;
    private Lekarz lekarz;
    private ArrayList<Usluga> uslugi;

    public ArrayList<Usluga> getUslugi() {
        return uslugi;
    }

    public VisitWrapper(Pacjent pacjent, Wizyta wizyta, Lekarz lekarz, ArrayList<Usluga> uslugi){
        this.wizyta = wizyta;
        this.pacjent = pacjent;
        this.lekarz = lekarz;
        this.uslugi = uslugi;
    }

    public int getId(){
        return wizyta != null ? wizyta.getId() : null;
    }

    public String getDate(){
        return wizyta != null ? wizyta.getDate() : null;
    }
    public String getNamePatient() {
        return pacjent != null ? pacjent.getName() : null;
    }

    public String getPeselPatient() {
        return pacjent != null ? pacjent.getPesel() : null;
    }
    public Integer getNumberPatient() {
        return pacjent != null ? pacjent.getNumberPhone() : null;
    }
    public String getSurnamePatient() {
        return pacjent != null ? pacjent.getSurname() : null;
    }

    public String getNameDoctor() {
        return lekarz != null ? lekarz.getName() : null;
    }

    public String getSurnameDoctor() {
        return lekarz != null ? lekarz.getSurname() : null;
    }
}
