package third;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CryptA {

    public static final int RETRIES = 2000;
    public static void main(String[] args) throws IOException {
        int zeroCount = 0;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        for (int i = 0; i < RETRIES; i++) {
            int currentChar = Integer.parseInt(bufferedReader.readLine());
            System.out.println(0);
            String currentAnswer = bufferedReader.readLine();
            if ("YES".equals(currentAnswer) && currentChar == 0) {
                zeroCount++;
            } else if ("NO".equals(currentAnswer) && currentChar == 1) {
                zeroCount++;
            }
        }
        int cypher = 0;
        if (zeroCount < RETRIES / 2) {
            cypher = 1;
        }
        for (int i = 0; i < 10000 - RETRIES; i++) {
            int currentChar = Integer.parseInt(bufferedReader.readLine());
            if (currentChar == cypher) {
                System.out.println(0);
            } else {
                System.out.println(1);
            }
            bufferedReader.readLine();
        }
    }
}
