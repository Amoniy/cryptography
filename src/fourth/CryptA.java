package fourth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class CryptA {

    private static byte[] zero = new byte[]{0b00000000};
    private static byte[] one = new byte[]{0b00000001};
    private static byte[] two = new byte[]{0b00000010};
    private static BufferedReader bufferedReader;
    private static MessageDigest digest;
    private static int height;
    private static String root;
    private static String nullStr = "null";

    private static byte[] concat(byte[] left, byte[] right) {
        if (left == null && right == null) {
            return null;
        }
        if (left == null) {
            return right;
        }
        if (right == null) {
            return left;
        }
        byte[] ret = new byte[left.length + right.length];
        System.arraycopy(left, 0, ret, 0, left.length);
        System.arraycopy(right, 0, ret, left.length, right.length);
        return ret;
    }

    private static boolean checkRound() throws IOException {
        String[] block = bufferedReader.readLine().split(" ");
        int index = Integer.parseInt(block[0]);
        String value = block[1];
        index += Math.pow(2, height) - 1;

        byte[] currentNode;
        if (value.equals(nullStr)) {
            currentNode = null;
        } else {
            byte[] decodedLeaf = Base64.getDecoder().decode(value);
            currentNode = digest.digest(concat(zero, decodedLeaf));
        }

        while (index > 0) {
            String currentString = bufferedReader.readLine();
            byte[] currentBytes;
            if (currentString.equals(nullStr)) {
                currentBytes = null;
            } else {
                currentBytes = Base64.getDecoder().decode(currentString);
            }
            if (currentNode == null && currentBytes == null) {
                index--;
                index /= 2;
                continue;
            }
            if (isLeft(index)) {
                currentNode = digest.digest(concat(concat(one, currentNode), concat(two, currentBytes)));
            } else {
                currentNode = digest.digest(concat(concat(one, currentBytes), concat(two, currentNode)));
            }
            index--;
            index /= 2;
        }
        if (currentNode == null) {
            return root.equals(nullStr);
        }
        return root.equals(new String(Base64.getEncoder().encode(currentNode), StandardCharsets.UTF_8));
    }

    private static boolean isLeft(int index) {
        return (index) % 2 == 1;
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        digest = MessageDigest.getInstance("SHA-256");
        height = Integer.parseInt(bufferedReader.readLine());
        root = bufferedReader.readLine();
        int rounds = Integer.parseInt(bufferedReader.readLine());
        for (int i = 0; i < rounds; i++) {
            if (checkRound()) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }
}
