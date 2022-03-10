import java.io.BufferedReader;
import java.io.IOException;

public class Util {
    public static int choice(Object[] array, BufferedReader reader) throws IOException {
        int index = -1;
        while (index == -1) {
            try {
                int input = Integer.parseInt(reader.readLine());
                index = input > 0 && input <= array.length ? input - 1 : -1;
                if (index == -1) break;
            } catch (NumberFormatException e) {
                System.out.println("Wrong input.");
            }
        }
        return index;
    }

    public static void printStringArray(String[] array) {
        for (String s: array) {
            System.out.println(s);
        }
    }
}
