
public class RabinKarp {
    private static final long MOD1 = 1000000007L;
    private static final long MOD2 = 1000000009L;
    private static final long BASE = 911382323L;

    private static long hash(String s, long mod) {
        long h = 0;
        for (int i = 0; i < s.length(); i++) h = (h * 911382323L + s.charAt(i)) % mod;
        return h;
    }

    public static int count(String text, String pattern) {
        if (pattern.length() == 0 || pattern.length() > text.length()) return 0;
        long hp1 = hash(pattern, MOD1);
        long hp2 = hash(pattern, MOD2);
        long power1 = 1, power2 = 1;
        for (int i = 0; i < pattern.length(); i++) {
            power1 = (power1 * BASE) % MOD1;
            power2 = (power2 * BASE) % MOD2;
        }
        long h1 = 0, h2 = 0;
        int m = pattern.length(), count = 0;
        for (int i = 0; i < text.length(); i++) {
            h1 = (h1 * BASE + text.charAt(i)) % MOD1;
            h2 = (h2 * BASE + text.charAt(i)) % MOD2;
            if (i >= m) {
                h1 = (h1 - (text.charAt(i - m) * power1) % MOD1 + MOD1) % MOD1;
                h2 = (h2 - (text.charAt(i - m) * power2) % MOD2 + MOD2) % MOD2;
            }
            if (i >= m - 1 && h1 == hp1 && h2 == hp2) {
                int start = i - m + 1;
                boolean same = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(start + j) != pattern.charAt(j)) { same = false; break; }
                }
                if (same) count++;
            }
        }
        return count;
    }
}
