public class ZSearch {
    private static int[] zArray(String s) {
        int n = s.length(); int[] z = new int[n]; int l = 0, r = 0;
        for (int i = 1; i < n; i++) {
            if (i <= r) z[i] = Math.min(r - i + 1, z[i - l]);
            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) z[i]++;
            if (i + z[i] - 1 > r) { l = i; r = i + z[i] - 1; }
        }
        return z;
    }
    public static int count(String text, String pattern) {
        if (pattern.length() == 0 || pattern.length() > text.length()) return 0;
        String s = pattern + '\u0000' + text;
        int[] z = zArray(s); int c = 0;
        for (int i = pattern.length() + 1; i < z.length; i++) if (z[i] >= pattern.length()) c++;
        return c;
    }
}
