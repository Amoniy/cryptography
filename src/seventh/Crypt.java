package seventh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;


public class Crypt {

    private static final int ROUNDS = 1000;
    private static final int COUNT_OF_LEAVES = 256;
    private static final int HEIGHT = 8;

    private static byte[] zero = new byte[]{0b00000000};
    private static byte[] one = new byte[]{0b00000001};
    private static byte[] two = new byte[]{0b00000010};

    private static BufferedReader bufferedReader;
    private static MessageDigest digest;

    private static String zeroes = "";
    private static String ones = "";

    private static int[] knownCodes = new int[COUNT_OF_LEAVES];
    private static int knownIndex = -1;
    private static byte[][][] firstCodes = new byte[256][256][32];
    private static byte[][][] secondCodes = new byte[256][256][32];

    private static String root; // in Base64
    private static String nullStr = "null";
    private static boolean isPossible = false;

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

    private static boolean playRound() throws IOException {
        int index = Integer.parseInt(bufferedReader.readLine());
        if (knownCodes[index] == 0) {
            System.out.println(zeroes);
        } else {
            System.out.println(ones);
        }
        String signature = bufferedReader.readLine();
        String value = bufferedReader.readLine();

        byte[] decodedSignature = Base64.getDecoder().decode(signature);
        byte[] decodedValue = Base64.getDecoder().decode(value);

//        if (!new String(digest.digest(Base64.getDecoder().decode(signature))).equals(new String(Base64.getDecoder().decode(value)))) {

        if(!checkSignature(decodedSignature, decodedValue, knownCodes[index] > 0)){
            for (int i = 0; i < HEIGHT; i++) {
                bufferedReader.readLine();
            }
            return false;
        }
        if (knownCodes[index] == 0) {
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 32; j++) {
                    firstCodes[index][i][j] = decodedSignature[32 * i + j];
                }
            }
//            firstCodes[index] = value;
        } else {
            for (int i = 0; i < 256; i++) {
                for (int j = 0; j < 32; j++) {
                    secondCodes[index][i][j] = decodedSignature[32 * i + j];
                }
            }
//            secondCodes[index] = value;
        }
        knownCodes[index]++;
        if (knownCodes[index] > 1) {
            knownIndex = index;
            isPossible = true;
        }
        index += Math.pow(2, HEIGHT) - 1;

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
        for (int i = 0; i < 32; i++) {
            ones += "11111111";
            zeroes += "00000000";
        }
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        digest = MessageDigest.getInstance("SHA-256");

        root = bufferedReader.readLine();

        String hash = "";
        for (int i = 0; i < ROUNDS; i++) {
            if (playRound()) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
            hash = bufferedReader.readLine();
            if (isPossible) {
                System.out.println("YES");
                break;
            } else {
                System.out.println("NO");
            }
        }
//        throw new RuntimeException();

        byte[] answerSignature = new byte[256 * 32];
        for (int i = 0; i < 256; i++) {
            if (hash.charAt(i) == '0') {
                for (int j = 0; j < 32; j++) {
                    answerSignature[32 * i + j] = firstCodes[knownIndex][i][j];
                }
            } else {
                for (int j = 0; j < 32; j++) {
                    answerSignature[32 * i + j] = secondCodes[knownIndex][i][j];
                }
            }
        }
        System.out.println(new String(Base64.getEncoder().encode(answerSignature)));
//        String answerSignature = "";
//        for (int i = 0; i < 256; i++) {
//            if (hash.charAt(i) == '0') {
//                answerSignature += firstCodes[knownIndex].charAt(i);
//            } else {
//                answerSignature += secondCodes[knownIndex].charAt(i);
//            }
//        }
//        System.out.println((answerSignature));
    }

    public static boolean checkSignature(byte[] signature, byte[] publicKey, boolean isSecond) {
        byte[] signatureSHA256 = new byte[256 * 32];
        for (int i = 0; i < 256; i++) {
            byte[] piece = new byte[32];
            System.arraycopy(signature, 32 * i, piece, 0, 32);
            byte[] shaPiece = digest.digest(piece);
            System.arraycopy(shaPiece, 0, signatureSHA256, i * 32, 32);
        }

        for (int i = 0; i < signatureSHA256.length; i++) {
            if (signatureSHA256[i] != publicKey[isSecond ? 32 * 256 + i : i]) {
                return false;
            }
        }
        return true;
    }
}
