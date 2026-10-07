public class BenchmarkService {
    private static final int WARMUP = 10;
    private static final int RUNS = 50;

    public static String compare(String text, String query) {
        String q = TextNormalizer.normalize(query);
        String t = TextNormalizer.normalize(text);
        String[] terms = TextNormalizer.tokenize(q);
        String bestTerm = terms.length == 0 ? q : terms[0];
        String[] names = {"Naive", "KMP", "Rabin-Karp", "Z-Function", "Edit Distance", "Suffix Array + LCP", "Aho-Corasick"};
        long[] times = new long[names.length];
        double[] values = new double[names.length];
        String[] uses = {"Simple exact matching", "Exact phrase matching", "Hash-based exact matching", "Prefix-based exact matching", "Spelling / fuzzy matching", "Prefix / substring similarity", "Multiple-pattern matching"};
        String[] complexities = {"O(nm)", "O(n+m)", "Expected O(n+m)", "O(n+m)", "O(nm)", "Build O(n log² n), LCP O(n)", "O(n + total pattern length + matches)"};

        for (int i = 0; i < WARMUP; i++) run(i, t, q, terms, bestTerm);
        for (int i = 0; i < names.length; i++) {
            long total = 0;
            for (int r = 0; r < RUNS; r++) {
                long s = System.nanoTime();
                values[i] = run(i, t, q, terms, bestTerm);
                total += System.nanoTime() - s;
            }
            times[i] = Math.max(1, total / RUNS);
        }

        long fastest = Long.MAX_VALUE;
        for (long time : times) if (time < fastest) fastest = time;
        StringBuilder j = new StringBuilder("{\"runs\":" + RUNS + ",\"benchmarks\":[");
        int bestIndex = -1;
        double bestOverall = -1;
        for (int i = 0; i < names.length; i++) {
            boolean useful = isUseful(i, values[i], terms);
            double quality = quality(i, values[i], q);
            double performance = 100.0 * fastest / times[i];
            double overall = useful ? 0.65 * quality + 0.35 * performance : 0.0;
            if (useful && overall > bestOverall) { bestOverall = overall; bestIndex = i; }
            if (i > 0) j.append(',');
            j.append("{\"algorithm\":\"").append(esc(names[i])).append("\",\"value\":")
             .append(String.format(java.util.Locale.US, "%.4f", values[i]))
             .append(",\"timeNs\":").append(times[i])
             .append(",\"quality\":").append(String.format(java.util.Locale.US, "%.1f", quality))
             .append(",\"performance\":").append(String.format(java.util.Locale.US, "%.1f", performance))
             .append(",\"overall\":").append(String.format(java.util.Locale.US, "%.1f", overall))
             .append(",\"useful\":").append(useful)
             .append(",\"use\":\"").append(esc(uses[i])).append("\",\"complexity\":\"").append(esc(complexities[i])).append("\"}");
        }
        j.append("],\"best\":\"").append(bestIndex < 0 ? "No suitable match" : esc(names[bestIndex]))
         .append("\",\"bestReason\":\"").append(bestIndex < 0 ? "Try a broader or fuzzy query." : esc(names[bestIndex] + " gave the best combined quality and measured performance for this query."))
         .append("\"}");
        return j.toString();
    }

    private static double run(int i, String t, String q, String[] terms, String bestTerm) {
        if (i == 0) return NaiveSearch.count(t, q);
        if (i == 1) return KMP.count(t, q);
        if (i == 2) return RabinKarp.count(t, q);
        if (i == 3) return ZSearch.count(t, q);
        if (i == 4) {
            if (bestTerm.length() == 0) return 0;
            String[] docTerms = TextNormalizer.tokenize(t);
            double best = 0;
            for (String token : docTerms) best = Math.max(best, EditDistance.similarity(bestTerm, token));
            return best;
        }
        if (i == 5) return new SuffixArray(t).longestPrefix(q);
        return AhoCorasick.count(t, terms);
    }

    private static boolean isUseful(int i, double value, String[] terms) {
        if (i == 4) return value >= 0.55;
        if (i == 5) return value >= 2;
        return value > 0;
    }
    private static double quality(int i, double value, String q) {
        if (i == 4) return value * 100.0;
        if (i == 5) return q.length() == 0 ? 0 : Math.min(100.0, value * 100.0 / q.length());
        return value > 0 ? 100.0 : 0.0;
    }
    public static String guide() {
        return "[{\"algorithm\":\"Naive\",\"purpose\":\"Simple exact pattern search\",\"advantage\":\"Very simple baseline\",\"complexity\":\"O(nm)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"KMP\",\"purpose\":\"Exact phrase search\",\"advantage\":\"Linear search after prefix preprocessing\",\"complexity\":\"O(n+m)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Rabin-Karp\",\"purpose\":\"Exact search using rolling hash\",\"advantage\":\"Useful for hash-based matching\",\"complexity\":\"Expected O(n+m)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Z-Function\",\"purpose\":\"Pattern matching\",\"advantage\":\"Linear prefix-based matching\",\"complexity\":\"O(n+m)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Aho-Corasick\",\"purpose\":\"Multiple-pattern matching\",\"advantage\":\"Search many patterns in one text pass\",\"complexity\":\"O(n + total pattern length + matches)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Levenshtein Edit Distance\",\"purpose\":\"Fuzzy matching\",\"advantage\":\"Handles insertions, deletions and substitutions\",\"complexity\":\"O(nm)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Needleman-Wunsch\",\"purpose\":\"Global sequence alignment\",\"advantage\":\"Compares complete sequences\",\"complexity\":\"O(nm)\",\"status\":\"Implemented as Algorithm Lab\"},"+
        "{\"algorithm\":\"Smith-Waterman\",\"purpose\":\"Local sequence alignment\",\"advantage\":\"Finds the best local matching region\",\"complexity\":\"O(nm)\",\"status\":\"Implemented as Algorithm Lab\"},"+
        "{\"algorithm\":\"Suffix Array + Kasai LCP\",\"purpose\":\"Suffix ordering and similarity\",\"advantage\":\"Useful for prefix/substring structure\",\"complexity\":\"Build O(n log² n), LCP O(n)\",\"status\":\"Implemented\"},"+
        "{\"algorithm\":\"Bitmask DP / TSP\",\"purpose\":\"Small-state combinatorial optimization\",\"advantage\":\"Exact solution for small n\",\"complexity\":\"O(2^n n²)\",\"status\":\"Course concept / extension\"},"+
        "{\"algorithm\":\"Interval DP\",\"purpose\":\"Problems built from intervals\",\"advantage\":\"Useful when combining subintervals\",\"complexity\":\"Typically O(n³)\",\"status\":\"Course concept / extension\"},"+
        "{\"algorithm\":\"Tree DP / Rerooting\",\"purpose\":\"Tree-structured optimization\",\"advantage\":\"Reuses subtree results\",\"complexity\":\"Typically O(n) or O(n log n)\",\"status\":\"Course concept / extension\"}]";
    }
    public static String alignment(String a, String b) {
        int nw=AlignmentDP.needlemanWunsch(a,b), sw=AlignmentDP.smithWaterman(a,b), ed=EditDistance.levenshtein(a,b);
        return "{\"needlemanWunsch\":"+nw+",\"smithWaterman\":"+sw+",\"editDistance\":"+ed+"}";
    }
    private static String esc(String s){return s.replace("\\","\\\\").replace("\"","\\\"");}
}
