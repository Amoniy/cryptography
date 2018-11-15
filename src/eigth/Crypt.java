package eigth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Base64;

public class Crypt {

    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String cipheredString = bufferedReader.readLine();
        String iv = bufferedReader.readLine();
        System.out.println("NO");
        byte[] ivBytes = Base64.getDecoder().decode(iv);
        ivBytes[2]++;
        System.out.println(cipheredString);
        System.out.println(Base64.getEncoder().encodeToString(ivBytes));
        if (bufferedReader.readLine().equals("Wrong padding")) {
            System.out.println("YES");
            System.out.println("No");
            return;
        }

        System.out.println("NO");
        System.out.println(cipheredString);
        ivBytes = Base64.getDecoder().decode(iv);
        ivBytes[2] = (byte) (65 ^ ivBytes[2] ^ 14); // 'A' == 65
        for (int i = 3; i < 16; i++) {
            ivBytes[i] = (byte) (ivBytes[i] ^ 3);
        }
        System.out.println(Base64.getEncoder().encodeToString(ivBytes));
        if (bufferedReader.readLine().equals("Ok")) {
            System.out.println("YES");
            System.out.println("N/A");
            return;
        }
        System.out.println("YES");
        System.out.println("Yes");
    }
}
