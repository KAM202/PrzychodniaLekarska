package przychodnialekarska.objectClass;

import java.util.Date;

public class Wizyta {

    final private int id;
    final private String pesel;
    final private String idDoctor;
    final private String date;


    public Wizyta(int id, String pesel, String idDoctor, String date) {
        this.id = id;
        this.pesel = pesel;
        this.idDoctor = idDoctor;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getPesel() {
        return pesel;
    }

    public String getIdDoctor() {
        return idDoctor;
    }

    public String getDate() {
        return date;
    }
}
