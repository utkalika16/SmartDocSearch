
public class SuffixArray {
    private String text;
    private int[] sa;
    private int[] lcp;

    public SuffixArray(String text) {
        this.text = text;
        build();
    }

    private void build() {
        int n = text.length();
        sa = new int[n];
        int[] rank = new int[n];
        int[] tmp = new int[n];
        for (int i = 0; i < n; i++) {
            sa[i] = i;
            rank[i] = text.charAt(i);
        }

        for (int k = 1; k < n; k *= 2) {
            for (int i = 0; i < n; i++) tmp[i] = rank[i];

            for (int i = 1; i < n; i++) {
                int key = sa[i];
                int j = i - 1;
                while (j >= 0 && compareSuffix(key, sa[j], k, tmp) < 0) {
                    sa[j + 1] = sa[j];
                    j--;
                }
                sa[j + 1] = key;
            }

            int classes = 1;
            rank[sa[0]] = 0;
            for (int i = 1; i < n; i++) {
                if (compareSuffix(sa[i - 1], sa[i], k, tmp) != 0) classes++;
                rank[sa[i]] = classes - 1;
            }
            if (classes == n) break;
        }
        buildLCP();
    }

    private int compareSuffix(int a, int b, int k, int[] rank) {
        if (rank[a] != rank[b]) return rank[a] - rank[b];
        int ra = a + k < text.length() ? rank[a + k] : -1;
        int rb = b + k < text.length() ? rank[b + k] : -1;
        return ra - rb;
    }

    private void buildLCP() {
        int n = text.length();
        lcp = new int[n];
        int[] pos = new int[n];
        for (int i = 0; i < n; i++) pos[sa[i]] = i;
        int h = 0;
        for (int i = 0; i < n; i++) {
            int p = pos[i];
            if (p == 0) continue;
            int j = sa[p - 1];
            while (i + h < n && j + h < n && text.charAt(i + h) == text.charAt(j + h)) h++;
            lcp[p] = h;
            if (h > 0) h--;
        }
    }

    public int longestPrefix(String pattern) {
        if (pattern.length() == 0 || sa.length == 0) return 0;
        int lo = 0, hi = sa.length - 1, best = 0;
        while (lo <= hi) {
            int mid = (lo + hi) >>> 1;
            int c = comparePattern(pattern, sa[mid]);
            int l = commonPrefix(pattern, sa[mid]);
            if (l > best) best = l;
            if (c == 0) return pattern.length();
            if (c < 0) hi = mid - 1;
            else lo = mid + 1;
        }
        return best;
    }

    private int comparePattern(String p, int start) {
        int n = Math.min(p.length(), text.length() - start);
        for (int i = 0; i < n; i++) {
            if (p.charAt(i) != text.charAt(start + i)) return p.charAt(i) - text.charAt(start + i);
        }
        return p.length() - (text.length() - start);
    }

    private int commonPrefix(String p, int start) {
        int n = Math.min(p.length(), text.length() - start);
        int i = 0;
        while (i < n && p.charAt(i) == text.charAt(start + i)) i++;
        return i;
    }

    public int[] getSA() { return sa; }
    public int[] getLCP() { return lcp; }
}
