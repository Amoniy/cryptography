package first;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Crypt {

    private static final int A_INDEX = 97;
    private static final int ALPHABET_SIZE = 26;
    private static final double IDEAL_COEFF = 0.065;
    private static final double[] ETALON = new double[]{0.08167, 0.01492, 0.02782, 0.04253, 0.12702, 0.02228, 0.02015, 0.06094, 0.06966, 0.00153, 0.00772, 0.04025, 0.02406, 0.06749, 0.07507, 0.01929, 0.00095, 0.05987, 0.06327, 0.09056, 0.02758, 0.00978, 0.0236, 0.0015, 0.01975, 0.00074};

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("cipher.txt")));
        double[] d = new double[10]; // коэффициенты для разных длин ключа
        String cipher = bufferedReader.readLine();
        for (int len = 1; len < 11; len++) {
            StringBuilder currentCipherBuilder = new StringBuilder();
            for (int i = 0; i < cipher.length(); i += len) {
                currentCipherBuilder.append(cipher.charAt(i));
            }
            String currentCipher = currentCipherBuilder.toString();
            int[] frequencies = new int[ALPHABET_SIZE];
            for (int i = 0; i < currentCipher.length(); i++) {
                frequencies[currentCipher.charAt(i) - A_INDEX]++;
            }
            long sum = 0;
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                sum += frequencies[i] * frequencies[i];
            }
            d[len - 1] = ((double) sum) / (currentCipher.length() * currentCipher.length());
        }
        double min = 1d;
        int length = 0;
        for (int i = 5; i < 10; i++) {
            double currentMin = Math.abs(IDEAL_COEFF - d[i]);
            if (currentMin < min) {
                min = currentMin;
                length = i + 1;
            }
        }

        int[] chars = new int[length];
        double[] squareEtalon = new double[ALPHABET_SIZE]; // метод разности квадратов
        for (int i = 0; i < ALPHABET_SIZE; i++) {
            squareEtalon[i] = ETALON[i] * ETALON[i];
        }
        for (int i = 0; i < length; i++) { // проход по символам шифра
            double[] frequencies = new double[ALPHABET_SIZE];
            for (int j = i; j < cipher.length(); j+= length) {
                frequencies[cipher.charAt(j) - A_INDEX]++;
            }
            for (int j = 0; j < ALPHABET_SIZE; j++) {
                frequencies[j] *= length;
                frequencies[j] /= cipher.length();
            }

            double frequencyMin = Integer.MAX_VALUE;
            int neededChar = -1;
            for (int curChar = 0; curChar < ALPHABET_SIZE; curChar++) { // перебор буквы шифра
                double currentFrequency = 0;
                double[] currentFrequencies = new double[ALPHABET_SIZE];
                for (int k = 0; k < ALPHABET_SIZE; k++) { // сдвиг на текущую букву
                    currentFrequencies[k] = frequencies[(k + curChar) % ALPHABET_SIZE];
                }
                for (int k = 0; k < ALPHABET_SIZE; k++) { // разность квадратов
                    currentFrequencies[k] *= currentFrequencies[k];
                    currentFrequency += Math.abs(currentFrequencies[k] - squareEtalon[k]);
                }

                if (frequencyMin > currentFrequency) {
                    frequencyMin = currentFrequency;
                    neededChar = curChar;
                }
            }
            chars[i] = neededChar;
        }
        System.out.print("Кодовое слово - ");
        for (int i = 0; i < length; i++) {
            System.out.print((char)(chars[i] + A_INDEX));
        }
        System.out.println();
        char[] text = new char[cipher.length()];
        for (int i = 0; i < length; i++) {
            for (int j = i; j < cipher.length(); j+= length) {
                text[j] = (char) ((cipher.charAt(j) - A_INDEX + ALPHABET_SIZE - (chars[i])) % ALPHABET_SIZE + A_INDEX);
            }
        }
        System.out.println(String.format("Текст - %s", new String(text)));
    }
}
