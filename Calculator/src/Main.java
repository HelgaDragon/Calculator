import java.util.*;

public class Main {
    public static void main(String[] args) {
        String str = "";
        Scanner in = new Scanner(System.in);
        try {
            while (true) {
                System.out.print("Input: ");
                str = in.nextLine();
                System.out.print("Output: ");
                System.out.println(calc(str));
            }
        }
        catch (FormatException | ArithmeticException e) {
            System.out.println(e.getMessage());
        }
        finally {
            System.out.println("Работа калькулятора завершена");
            in.close();
        }
    }

    public static String calc(String input) throws FormatException, ArithmeticException {
        Analyzer analyzer = new Analyzer();
        ArrayList<String> list = analyzer.analyze(input);

        if(list.get(0) == "arabic") {
            ArabicCalc ac = new ArabicCalc();
            return ac.calc(list);
        } else if(list.get(0) == "roman") {
            RomanCalc rc = new RomanCalc();
            return rc.calc(list);
        }
        return "";
    }
}

class Analyzer {
    public ArrayList<String> analyze(String input) throws FormatException {
        //Раскладывание строки на составляющие
        input = input.replaceAll(" ", "");
        char[] charArray = input.toCharArray();

        boolean num_a_roman = true;
        boolean num_b_roman = true;

        String num_a = "";
        String num_b = "";
        String operation = "";
        String ArabicOrRoman = "";

        boolean position = false;
        byte countOfOperations = 0;

        for (int i = 0; i < charArray.length; i++) {

            if(charArray[i] != '+' && charArray[i] != '-' && charArray[i] != '*' && charArray[i] != '/') {
                if(!position) {
                    num_a += charArray[i];
                } else {
                    num_b += charArray[i];
                }
            } else {
                operation += charArray[i];
                position = true;
                countOfOperations++;
            }
        }
        if(num_a == "" || num_b == "" || countOfOperations == 0 || countOfOperations > 1) {
            throw new FormatException("Неверный синтаксис выражения");
        }

        char[] first_num = num_a.toCharArray();
        for(int i = 0; i < num_a.length(); i++) {
            if(first_num[i] != 'I' && first_num[i] != 'V' && first_num[i] != 'X'){
                num_a_roman = false;
            }
        }

        char[] second_num = num_b.toCharArray();
        for(int i = 0; i < num_b.length(); i++) {
            if(second_num[i] != 'I' && second_num[i] != 'V' && second_num[i] != 'X'){
                num_b_roman = false;
            }
        }

        if(num_a_roman && num_b_roman) {
            ArabicOrRoman = "roman";
        } else if(!num_a_roman && !num_b_roman) {
            ArabicOrRoman = "arabic";
        } else {
            throw new FormatException("Используются разные системы счисления");
        }

        ArrayList<String> list = new ArrayList<>();
        list.add(ArabicOrRoman);
        list.add(operation);
        list.add(num_a);
        list.add(num_b);
        return list;
    }
}

class ArabicCalc {
    public String calc(ArrayList<String> parts) throws ArithmeticException {

        int num_a;
        int num_b;

        try {
            num_a = Integer.parseInt(parts.get(2));
            num_b = Integer.parseInt(parts.get(3));
        } catch (NumberFormatException e) {
            throw new ArithmeticException("Один или оба операнда не является(ются) числом ");
        }

        if(num_a < 1 || num_b < 1 || num_a > 10 || num_b > 10) {
            throw new ArithmeticException("Введенные числа должны быть в диапазоне от 1 до 10 включительно");
        }

        switch (parts.get(1)) {
            case ("+"):
                return Integer.toString(num_a + num_b);
            case ("-"):
                return Integer.toString(num_a - num_b);
            case ("*"):
                return Integer.toString(num_a * num_b);
            case ("/"):
                return Integer.toString(num_a / num_b);
        }
        return "";
    }
}

class RomanCalc {
    public String calc(ArrayList<String> parts) throws ArithmeticException {
        int arabic_a = Roman2Arabic.toArabic(parts.get(2));
        int arabic_b = Roman2Arabic.toArabic(parts.get(3));

        if(arabic_a < 1 || arabic_b < 1 || arabic_a > 10 || arabic_b > 10) {
            throw new ArithmeticException("Введенные числа должны быть в диапазоне от I до X включительно");
        }

        switch (parts.get(1)) {
            case ("+"):
                return Roman2Arabic.toRoman(arabic_a + arabic_b);
            case ("-"):
                int result = arabic_a - arabic_b;
                if(result < 1) {
                    throw new ArithmeticException("Результат или равен 0 или меньше 0");
                }
                return Roman2Arabic.toRoman(arabic_a - arabic_b);
            case ("*"):
                return Roman2Arabic.toRoman(arabic_a * arabic_b);
            case ("/"):
                result = arabic_a - arabic_b;
                if(result < 1) {
                    throw new ArithmeticException("Результат равен 0");
                }
                return Roman2Arabic.toRoman(arabic_a / arabic_b);
        }
        return "";
    }
}

class FormatException extends Exception {
    public FormatException(String message){
        super(message);
    }
}

class Roman2Arabic {
    private static int[] intervals={0, 1, 4, 5, 9, 10, 40, 50, 90, 100, 400, 500, 900, 1000};
    private static String[] numerals={"", "I", "IV", "V", "IX", "X", "XL", "L", "XC", "C", "CD", "D", "CM", "M"};

    private static int findFloor(final int number, final int firstIndex, final int lastIndex) {
        if(firstIndex==lastIndex)
            return firstIndex;
        if(intervals[firstIndex]==number)
            return firstIndex;
        if(intervals[lastIndex]==number)
            return lastIndex;
        final int median=(lastIndex+firstIndex)/2;
        if(median==firstIndex)
            return firstIndex;
        if(number == intervals[median])
            return median;
        if(number > intervals[median])
            return findFloor(number, median, lastIndex);
        else
            return findFloor(number, firstIndex, median);

    }

    public static String toRoman(final int number) {
        int floorIndex=findFloor(number, 0, intervals.length-1);
        if(number==intervals[floorIndex])
            return numerals[floorIndex];
        return numerals[floorIndex]+toRoman(number-intervals[floorIndex]);
    }

    public static int toArabic(String roman) {
        int result = 0;
        for (int i = intervals.length-1; i >= 0; i-- ) {
            while (roman.indexOf(numerals[i]) == 0 && numerals[i].length() > 0) {
                result += intervals[i];
                roman = roman.substring(numerals[i].length());
            }
        }
        return result;
    }
}