package cipher;
import java.util.Arrays;

public class CryptoUtil {
    public static String encrypt(String algorithm, String key, String text) throws Exception {
        switch (algorithm) {
            case "Caesar Cipher":
                return caesarEncrypt(text, Integer.parseInt(key));
            case "Mono Alphabetic Cipher":
                return monoAlphabeticEncrypt(text, key);
            case "Rail Fence Cipher":
                return railFenceEncrypt(text, Integer.parseInt(key));
            case "Hill Cipher":
                return hillCipherEncrypt(text, key);
            case "Playfair Cipher":
                return playfairEncrypt(text, key);
            default:
                throw new IllegalArgumentException("Invalid algorithm selected.");
        }
    }

    public static String decrypt(String algorithm, String key, String text) throws Exception {
        switch (algorithm) {
            case "Caesar Cipher":
                return caesarDecrypt(text, Integer.parseInt(key));
            case "Mono Alphabetic Cipher":
                return monoAlphabeticDecrypt(text, key);
            case "Rail Fence Cipher":
                return railFenceDecrypt(text, Integer.parseInt(key));
            case "Hill Cipher":
                return hillCipherDecrypt(text, key);
            case "Playfair Cipher":
                return playfairDecrypt(text, key);
            default:
                throw new IllegalArgumentException("Invalid algorithm selected.");
        }
    }

    private static String caesarEncrypt(String text, int shift) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char shifted = (char) (((ch - 'a' + shift) % 26) + 'a');
                result.append(shifted);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private static String caesarDecrypt(String text, int shift) {
        return caesarEncrypt(text, 26 - shift);
    }

    private static String monoAlphabeticEncrypt(String text, String key) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] shuffledAlphabet = key.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char encrypted;
                if (Character.isUpperCase(ch)) {
                    encrypted = Character.toUpperCase(shuffledAlphabet[ch - 'A']);
                } else {
                    encrypted = shuffledAlphabet[ch - 'a'];
                }
                result.append(encrypted);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private static String monoAlphabeticDecrypt(String text, String key) {
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        char[] shuffledAlphabet = key.toCharArray();
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (Character.isLetter(ch)) {
                char decrypted;
                if (Character.isUpperCase(ch)) {
                    decrypted = (char) ('A' + new String(shuffledAlphabet).indexOf(Character.toLowerCase(ch)));
                } else {
                    decrypted = (char) ('a' + new String(shuffledAlphabet).indexOf(ch));
                }
                result.append(decrypted);
            } else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    private static String railFenceEncrypt(String text, int rails) {
        StringBuilder[] fence = new StringBuilder[rails];
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
        }
        int rail = 0;
        boolean down = true;
        for (char ch : text.toCharArray()) {
            fence[rail].append(ch);
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        StringBuilder result = new StringBuilder();
        for (StringBuilder row : fence) {
            result.append(row);
        }
        return result.toString();
    }

    private static String railFenceDecrypt(String text, int rails) {
        StringBuilder[] fence = new StringBuilder[rails];
        int[] lengths = new int[rails];
        for (int i = 0; i < rails; i++) {
            fence[i] = new StringBuilder();
            lengths[i] = 0;
        }
        int rail = 0;
        boolean down = true;
        for (int i = 0; i < text.length(); i++) {
            lengths[rail]++;
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        int pointer = 0;
        for (int i = 0; i < rails; i++) {
            for (int j = 0; j < lengths[i]; j++) {
                fence[i].append(text.charAt(pointer));
                pointer++;
            }
        }
        StringBuilder result = new StringBuilder();
        rail = 0;
        down = true;
        int[] currentLengths = lengths.clone();
        for (int i = 0; i < text.length(); i++) {
            result.append(fence[rail].charAt(0));
            fence[rail].deleteCharAt(0);
            currentLengths[rail]--;
            if (down) {
                rail++;
                if (rail == rails - 1) down = false;
            } else {
                rail--;
                if (rail == 0) down = true;
            }
        }
        return result.toString();
    }

    private static String hillCipherEncrypt(String text, String key) {
        int dimension = (int) Math.sqrt(key.length());
        if (text.length() % dimension != 0) {
            throw new IllegalArgumentException("Text length must be a multiple of the key's dimension.");
        }
        int[][] matrix = new int[dimension][dimension];
        int[] vector = new int[dimension];
        for (int i = 0; i < key.length(); i++) {
            matrix[i / dimension][i % dimension] = key.charAt(i) - 'a';
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += dimension) {
            for (int j = 0; j < dimension; j++) {
                vector[j] = text.charAt(i + j) - 'a';
            }
            int[] product = multiplyMatrix(matrix, vector);
            for (int j = 0; j < dimension; j++) {
                result.append((char) (product[j] % 26 + 'a'));
            }
        }
        return result.toString();
    }

    private static String hillCipherDecrypt(String text, String key) {
        int dimension = (int) Math.sqrt(key.length());
        if (text.length() % dimension != 0) {
            throw new IllegalArgumentException("Text length must be a multiple of the key's dimension.");
        }
        int[][] matrix = new int[dimension][dimension];
        int[] vector = new int[dimension];
        for (int i = 0; i < key.length(); i++) {
            matrix[i / dimension][i % dimension] = key.charAt(i) - 'a';
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += dimension) {
            for (int j = 0; j < dimension; j++) {
                vector[j] = text.charAt(i + j) - 'a';
            }
            int[] inverse = invertMatrix(matrix);
            int[] product = multiplyMatrix(new int[][]{inverse}, vector);
            for (int j = 0; j < dimension; j++) {
                result.append((char) ((product[j] + 26) % 26 + 'a'));
            }
        }
        return result.toString();
    }

    private static int[] multiplyMatrix(int[][] matrix, int[] vector) {
        int[] product = new int[vector.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < vector.length; j++) {
                product[i] += matrix[i][j] * vector[j];
            }
        }
        return product;
    }

    private static int[] invertMatrix(int[][] matrix) {
        int determinant = determinant(matrix);
        int[][] adjoint = adjoint(matrix);
        int inverseDeterminant = modInverse(determinant);
        int[][] inverse = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                inverse[i][j] = adjoint[i][j] * inverseDeterminant % 26;
            }
        }
        return Arrays.copyOf(inverse[0], inverse[0].length);
    }

    private static int determinant(int[][] matrix) {
        if (matrix.length == 1) {
            return matrix[0][0];
        }
        int det = 0;
        for (int i = 0; i < matrix.length; i++) {
            int[][] minor = new int[matrix.length - 1][matrix.length - 1];
            for (int j = 1; j < matrix.length; j++) {
                for (int k = 0; k < matrix.length; k++) {
                    if (k < i) {
                        minor[j - 1][k] = matrix[j][k];
                    } else if (k > i) {
                        minor[j - 1][k - 1] = matrix[j][k];
                    }
                }
            }
            det += Math.pow(-1, i) * matrix[0][i] * determinant(minor);
        }
        return det;
    }

    private static int[][] adjoint(int[][] matrix) {
        int[][] adjoint = new int[matrix.length][matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix.length; j++) {
                int[][] minor = new int[matrix.length - 1][matrix.length - 1];
                for (int k = 0; k < matrix.length; k++) {
                    for (int l = 0; l < matrix.length; l++) {
                        if (k != i && l != j) {
                            minor[k < i ? k : k - 1][l < j ? l : l - 1] = matrix[k][l];
                        }
                    }
                }
                adjoint[j][i] = (int) (Math.pow(-1, i + j) * determinant(minor));
            }
        }
        return adjoint;
    }

    private static int modInverse(int num) {
        for (int i = 1; i < 26; i++) {
            if ((num * i) % 26 == 1) return i;
        }
        return -1;
    }

    private static String playfairEncrypt(String text, String key) {
        char[][] matrix = generatePlayfairMatrix(key);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = (i + 1 < text.length()) ? text.charAt(i + 1) : 'x';
            int[] firstPosition = findPosition(matrix, first);
            int[] secondPosition = findPosition(matrix, second);
            if (firstPosition[0] == secondPosition[0]) {
                result.append(matrix[firstPosition[0]][(firstPosition[1] + 1) % 5]);
                result.append(matrix[secondPosition[0]][(secondPosition[1] + 1) % 5]);
            } else if (firstPosition[1] == secondPosition[1]) {
                result.append(matrix[(firstPosition[0] + 1) % 5][firstPosition[1]]);
                result.append(matrix[(secondPosition[0] + 1) % 5][secondPosition[1]]);
            } else {
                result.append(matrix[firstPosition[0]][secondPosition[1]]);
                result.append(matrix[secondPosition[0]][firstPosition[1]]);
            }
        }
        return result.toString();
    }

    private static String playfairDecrypt(String text, String key) {
        char[][] matrix = generatePlayfairMatrix(key);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += 2) {
            char first = text.charAt(i);
            char second = text.charAt(i + 1);
            int[] firstPosition = findPosition(matrix, first);
            int[] secondPosition = findPosition(matrix, second);
            if (firstPosition[0] == secondPosition[0]) {
                result.append(matrix[firstPosition[0]][(firstPosition[1] - 1 + 5) % 5]);
                result.append(matrix[secondPosition[0]][(secondPosition[1] - 1 + 5) % 5]);
            } else if (firstPosition[1] == secondPosition[1]) {
                result.append(matrix[(firstPosition[0] - 1 + 5) % 5][firstPosition[1]]);
                result.append(matrix[(secondPosition[0] - 1 + 5) % 5][secondPosition[1]]);
            } else {
                result.append(matrix[firstPosition[0]][secondPosition[1]]);
                result.append(matrix[secondPosition[0]][firstPosition[1]]);
            }
        }
        return result.toString();
    }

    private static char[][] generatePlayfairMatrix(String key) {
        String alphabet = "abcdefghiklmnopqrstuvwxyz";
        char[][] matrix = new char[5][5];
        key = key.replaceAll("[^a-zA-Z]", "").toLowerCase();
        key += alphabet;
        int index = 0;
        for (char ch : key.toCharArray()) {
            if (ch == 'j') ch = 'i';
            if (alphabet.indexOf(ch) != -1) {
                matrix[index / 5][index % 5] = ch;
                alphabet = alphabet.replace(ch, ' ');
                index++;
            }
        }
        return matrix;
    }

    private static int[] findPosition(char[][] matrix, char ch) {
        int[] position = new int[2];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == ch) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }
        return position;
    }
}
