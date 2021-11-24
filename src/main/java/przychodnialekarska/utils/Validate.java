package przychodnialekarska.utils;

public class Validate {

    public static boolean ValidateName(String name){
        if(name.length() < 2 || name.length() > 16) return false;

        return true;
    }
    public static boolean ValidateSurname(String surname){
        if(surname.length() < 1 || surname.length() > 32) return false;

        return true;
    }

    public static boolean ValidatePesel(String pesel){
        if(pesel.length() != 11) return false;

        byte peselByte[] = new byte[11];

        for(int i = 0; i < 11; i++) peselByte[i] = Byte.parseByte(pesel.substring(i, i+1));

        return (checkSumPesel(peselByte) && checkMonth(peselByte) && checkDay(peselByte));
    }


    private static boolean checkSumPesel(byte peselByte[]){
        int sum = 1 * peselByte[0] +
                3 * peselByte[1] +
                7 * peselByte[2] +
                9 * peselByte[3] +
                1 * peselByte[4] +
                3 * peselByte[5] +
                7 * peselByte[6] +
                9 * peselByte[7] +
                1 * peselByte[8] +
                3 * peselByte[9];
        sum %= 10;
        sum = 10 - sum;
        sum %= 10;

        return sum == peselByte[10];
    }

    private static boolean checkMonth(byte peselByte[]) {
        int month = getBirthMonth(peselByte);
        int day = getBirthDay(peselByte);
        return (month > 0 && month < 13);
    }

    private static int getBirthDay(byte peselByte[]) {
        int day;
        day = 10 * peselByte[4];
        day += peselByte[5];
        return day;
    }

    private static int getBirthMonth(byte peselByte[]) {
        int month;
        month = 10 * peselByte[2];
        month += peselByte[3];
        if (month > 80 && month < 93) {
            month -= 80;
        }
        else if (month > 20 && month < 33) {
            month -= 20;
        }
        else if (month > 40 && month < 53) {
            month -= 40;
        }
        else if (month > 60 && month < 73) {
            month -= 60;
        }
        return month;
    }

    private static boolean checkDay(byte peselByte[]) {
        int year = getBirthYear(peselByte);
        int month = getBirthMonth(peselByte);
        int day = getBirthDay(peselByte);
        if ((day >0 && day < 32) &&
                (month == 1 || month == 3 || month == 5 ||
                        month == 7 || month == 8 || month == 10 ||
                        month == 12)) {
            return true;
        }
        else if ((day >0 && day < 31) &&
                (month == 4 || month == 6 || month == 9 ||
                        month == 11)) {
            return true;
        }
        else if ((day >0 && day < 30 && leapYear(year)) ||
                (day >0 && day < 29 && !leapYear(year))) {
            return true;
        }
        else {
            return false;
        }
    }

    private static int getBirthYear(byte peselByte[]) {
        int year;
        int month;
        year = 10 * peselByte[0];
        year += peselByte[1];
        month = 10 * peselByte[2];
        month += peselByte[3];
        if (month > 80 && month < 93) {
            year += 1800;
        }
        else if (month > 0 && month < 13) {
            year += 1900;
        }
        else if (month > 20 && month < 33) {
            year += 2000;
        }
        else if (month > 40 && month < 53) {
            year += 2100;
        }
        else if (month > 60 && month < 73) {
            year += 2200;
        }
        return year;
    }

    private static boolean leapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0 || year % 400 == 0);
    }
}
