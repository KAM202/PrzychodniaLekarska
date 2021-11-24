package przychodnialekarska.utils;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;
import javafx.scene.control.TextFormatter;

public class Formatter {

    public static TextFormatter onlyDoubleFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
        return Pattern.compile("\\d*|\\d+\\.\\d*").matcher(change.getControlNewText()).matches() ? change : null;
    });

    public static TextFormatter onlyEmailFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
        return Pattern.compile("^(.+)@(.+)$").matcher(change.getControlNewText()).matches() ? change : null;
    });

    public static TextFormatter onlyDigitFormatter = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
        return Pattern.compile("^[0-9]*$").matcher(change.getControlNewText()).matches() ? change : null;
    });

    public static TextFormatter<String> onlyLettersIncludePolish = new TextFormatter<String>( change ->{
        change.setText(change.getText().replaceAll("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*", ""));
        return change;
    });
    //public static TextFormatter onlyLettersIncludePolish = new TextFormatter((UnaryOperator<TextFormatter.Change>) change ->{
    //    return Pattern.compile("[A-Za-zżźćńółęąśŻŹĆĄŚĘŁÓŃ]*").matcher(change.getControlNewText()).matches() ? change : null;
    //});
}
