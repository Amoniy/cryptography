package third;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CryptB {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 2000; i++) {
            int currentChar = Integer.parseInt(bufferedReader.readLine());
            System.out.println(0);
            String currentAnswer = bufferedReader.readLine();
            if ("YES".equals(currentAnswer) && currentChar == 0) {
                builder.append("0");
            } else if ("NO".equals(currentAnswer) && currentChar == 1) {
                builder.append("0");
            } else {
                builder.append("1");
            }
        }
        String first2k = builder.toString();
        int len = 1000;
        for (; len > 0; len--) {
            String currentString = first2k.substring(0, len);
            if (first2k.substring(len, 2 * len).equals(currentString)) {
                break;
            }
        }
        int curLen = 2000;

        for (int i = 0; i < 8000; i++) {
            int currentChar = Integer.parseInt(bufferedReader.readLine());
            if (currentChar == first2k.charAt(curLen % len) - '0') {
                System.out.println(0);
            } else {
                System.out.println(1);
            }
            bufferedReader.readLine();
            curLen++;
        }
    }
}
