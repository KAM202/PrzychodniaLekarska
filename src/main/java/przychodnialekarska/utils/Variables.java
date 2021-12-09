package przychodnialekarska.utils;

import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.Arrays;

public class Variables {
    public static ArrayList<Button> buttonArrayList = new ArrayList<Button>();
    public static int poziomUprawnien = -1;
    public static String imie;
    public static String nazwisko;
    public static Integer id_pracownika;

    public static final String additionalQuery = " WHERE id_pracownika = '" + id_pracownika + "'";

    public static String addittionalQuery(){
        return " WHERE id_pracownika = '" + id_pracownika + "'";
    }

    public static ArrayList<ArrayList<Integer>> windowsIds = new ArrayList(
            Arrays.asList(
                    new ArrayList(Arrays.asList(0,1,2,3)),
                    new ArrayList(Arrays.asList(2,4,5,6)),
                    new ArrayList(Arrays.asList(2,3,7,8)),
                    new ArrayList(Arrays.asList(0,1,2,3,4,5,6,7,8))
            )
            );
    public static ArrayList<String> windowsNames = new ArrayList(
            Arrays.asList(
                    "Zarejestruj pacjenta",
                    "Umów wizytę/badanie",
                    "Lista wizyt",
                    "Lista pacjentów",
                    "Wystawienie skierowania/e-recepty",
                    "Dodaj opis badania",
                    "Lista badań",
                    "Lista usług",
                    "Raporty przychodów/kosztów"
            ));
    public static ArrayList<String> resourceWindows = new ArrayList(
            Arrays.asList(
                    "/fxml/AddPatientForm.fxml",
                    "/fxml/MakeVisitForm.fxml",
                    "/fxml/VisitListForm.fxml",
                    "/fxml/PatientListForm.fxml",
                    "/fxml/MakeEDoc.fxml",
                    "/fxml/AddDescriptionOfExaminationForm.fxml",
                    "/fxml/ExaminationListForm.fxml",
                    "/fxml/ServiceForm.fxml",
                    "/fxml/ReportForm.fxml"
            )
    );


    public enum UPRAWIENIA{
        REJESTRATOR,
        LEKARZ,
        KIEROWNIK,
        ADMINISTRATOR
    }
}
