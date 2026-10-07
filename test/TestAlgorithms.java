
public class TestAlgorithms {
    private static void check(String name, boolean condition) {
        System.out.println((condition ? "PASS" : "FAIL") + " - " + name);
    }

    public static void main(String[] args) {
        check("KMP count", KMP.count("ababcababc", "abc") == 2);
        check("KMP first", KMP.first("hello world", "world") == 6);
        check("Rabin-Karp count", RabinKarp.count("aaaaa", "aa") == 4);
        check("Edit distance", EditDistance.levenshtein("kitten", "sitting") == 3);
        check("Edit similarity", EditDistance.similarity("book", "book") == 1.0);

        SuffixArray sa = new SuffixArray("banana");
        check("Suffix prefix", sa.longestPrefix("ban") == 3);

        System.out.println("Algorithm tests completed.");
    }
}
