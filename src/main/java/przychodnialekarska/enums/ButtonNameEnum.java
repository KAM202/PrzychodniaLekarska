package przychodnialekarska.enums;

import javafx.event.ActionEvent;
import przychodnialekarska.controller.MenuController;

import java.util.ArrayList;
import java.util.Arrays;

public class ButtonNameEnum {

    public enum UPRAWIENIA{
        REJESTRATOR,
        LEKARZ,
        KIEROWNIK,
        ADMINISTRATOR
    }

    public static ArrayList<ArrayList<Integer>> windowsIds = new ArrayList(
            Arrays.asList(
                    new ArrayList(Arrays.asList(0,1,2,3)),
                    new ArrayList(Arrays.asList(2,3,4,5)),
                    new ArrayList(Arrays.asList(2,3,6,7)),
                    new ArrayList(Arrays.asList(0,1,2,3,4,5,6,7))
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
                    "Lista usług",
                    "Raporty przychodów/kosztów"
            ));
    public static ArrayList<String> resourseWindows = new ArrayList(
            Arrays.asList(
                    "/fxml/AddPatientForm.fxml",
                    "/fxml/MakeVisitForm.fxml",
                    "/fxml/VisitListForm.fxml",
                    "/fxml/PatientListForm.fxml",
                    "/fxml/AddPatientForm.fxml",
                    "/fxml/AddPatientForm.fxml",
                    "/fxml/ServiceForm.fxml",
                    "/fxml/AddPatientForm.fxml"
            )
    );
}
