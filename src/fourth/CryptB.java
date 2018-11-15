package fourth;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;

public class CryptB {

    private static byte[] zero = new byte[]{0b00000000};
    private static byte[] one = new byte[]{0b00000001};
    private static byte[] two = new byte[]{0b00000010};
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

    public static void main(String[] args) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        int height = Integer.parseInt(bufferedReader.readLine());

        int givenLeaves = Integer.parseInt(bufferedReader.readLine());
        int pow = (int) Math.pow(2, height);

        HashMap<Integer, byte[]> map = new HashMap<>();
        HashMap<Integer, String> initialValues = new HashMap<>();
        int[] leaves = new int[givenLeaves];
        for (int i = 0; i < givenLeaves; i++) {
            String[] block = bufferedReader.readLine().split(" ");
            int index = Integer.parseInt(block[0]);
            String value = block[1];
            byte[] decodedLeaf = Base64.getDecoder().decode(value);
            index += pow - 1;
            byte[] hash = digest.digest(concat(zero, decodedLeaf));
            map.put(index, hash);
            initialValues.put(index, value);
            leaves[i] = index;
        }
        int numberOfLeaves = givenLeaves;
        for (int currentLevel = height; currentLevel > 0; currentLevel--) {
            int currentCount = 0;
            for (int i = 0; i < numberOfLeaves; i++) {
                if (!map.containsKey((leaves[i] - 1) / 2)) {
                    byte[] left;
                    byte[] right;
                    if (leaves[i] % 2 == 1) { // left
                        left = map.get(leaves[i]);
                        right = map.get(leaves[i] + 1);
                    } else { // right
                        right = map.get(leaves[i]);
                        left = map.get(leaves[i] - 1);
                    }
                    byte[] hash = digest.digest(concat(concat(one, left), concat(two, right)));

                    map.put((leaves[i] - 1) / 2, hash);

                    leaves[currentCount] = (leaves[i] - 1) / 2;
                    currentCount++;
                }
            }
            numberOfLeaves = currentCount;
        }
        int rounds = Integer.parseInt(bufferedReader.readLine());

        String[] temp = bufferedReader.readLine().split(" ");
        int[] requests = new int[rounds];
        for (int i = 0; i < rounds; i++) {
            requests[i] = Integer.parseInt(temp[i]) + pow - 1;
            int currentIndex = requests[i];
            System.out.print(temp[i] + " ");
            if (map.containsKey(currentIndex)) {
                System.out.println(initialValues.get(currentIndex));
            } else {
                System.out.println(nullStr);
            }
            while (currentIndex > 0) {
                if (currentIndex%2==1){
                    currentIndex++;
                } else {
                    currentIndex--;
                }
                if (map.containsKey(currentIndex)) {
                    System.out.println(new String(Base64.getEncoder().encode(map.get(currentIndex))));
                } else {
                    System.out.println(nullStr);
                }
                currentIndex--;
                currentIndex /= 2;
            }
        }
        System.out.println();
    }
}
