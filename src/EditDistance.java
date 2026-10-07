
public class EditDistance {
    public static int levenshtein(String a, String b) {
        int n = a.length(), m = b.length();
        int[] prev = new int[m + 1];
        int[] cur = new int[m + 1];
        for (int j = 0; j <= m; j++) prev[j] = j;

        for (int i = 1; i <= n; i++) {
            cur[0] = i;
            for (int j = 1; j <= m; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                int del = prev[j] + 1;
                int ins = cur[j - 1] + 1;
                int sub = prev[j - 1] + cost;
                cur[j] = min(del, ins, sub);
            }
            int[] t = prev; prev = cur; cur = t;
        }
        return prev[m];
    }

    private static int min(int a, int b, int c) {
        int x = a < b ? a : b;
        return x < c ? x : c;
    }

    public static double similarity(String a, String b) {
        if (a.length() == 0 && b.length() == 0) return 1.0;
        int d = levenshtein(a, b);
        int max = a.length() > b.length() ? a.length() : b.length();
        return 1.0 - ((double)d / max);
    }
}
