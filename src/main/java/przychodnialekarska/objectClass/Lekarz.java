package przychodnialekarska.objectClass;

public class Lekarz {
    final private int id;
    final private String pesel;
    final private String name;
    final private String surname;
    final private int numberPhone;
    final private String zipPostCode;
    final private String email;

    public Lekarz(int id, String pesel, String name, String surname, int numberPhone, String zipPostCode, String email) {
        this.id = id;
        this.pesel = pesel;
        this.name = name;
        this.surname = surname;
        this.numberPhone = numberPhone;
        this.zipPostCode = zipPostCode;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getPesel() {
        return pesel;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public int getNumberPhone() {
        return numberPhone;
    }

    public String getZipPostCode() {
        return zipPostCode;
    }

    public String getEmail() {
        return email;
    }
}
