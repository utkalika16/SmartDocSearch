
public class KMP {
    private static int[] failure(String p) {
        int[] f = new int[p.length()];
        int j = 0;
        for (int i = 1; i < p.length(); i++) {
            while (j > 0 && p.charAt(i) != p.charAt(j)) j = f[j - 1];
            if (p.charAt(i) == p.charAt(j)) j++;
            f[i] = j;
        }
        return f;
    }

    public static int count(String text, String pattern) {
        if (pattern.length() == 0) return 0;
        int[] f = failure(pattern);
        int j = 0, count = 0;
        for (int i = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = f[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == pattern.length()) {
                count++;
                j = f[j - 1];
            }
        }
        return count;
    }

    public static int first(String text, String pattern) {
        if (pattern.length() == 0) return 0;
        int[] f = failure(pattern);
        int j = 0;
        for (int i = 0; i < text.length(); i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = f[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == pattern.length()) return i - pattern.length() + 1;
        }
        return -1;
    }
}
