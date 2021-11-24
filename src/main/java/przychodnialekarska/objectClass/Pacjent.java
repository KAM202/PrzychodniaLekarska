package przychodnialekarska.objectClass;

public class Pacjent {

    final private String pesel;
    final private String name;
    final private String surname;
    final private int numberPhone;
    final private String address;
    final private String zipPostCode;
    final private String email;


    public Pacjent(String pesel, String name, String surname, int numberPhone, String address, String zipPostCode, String email) {
        this.pesel = pesel;
        this.name = name;
        this.surname = surname;
        this.numberPhone = numberPhone;
        this.address = address;
        this.zipPostCode = zipPostCode;
        this.email = email;
    }

    public String getAddress() {
        return address;
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
