
public class ConsoleDemo {
    public static void main(String[] args) {
        SearchEngine engine = new SearchEngine();
        engine.loadFolder("docs");
        String[] queries = {
            "machine learning",
            "machine lerning",
            "graph algorithms",
            "database indexing"
        };

        System.out.println("Indexed documents: " + engine.getDocumentCount());
        for (int q = 0; q < queries.length; q++) {
            ResultList results = engine.search(queries[q]);
            System.out.println("\nQUERY: " + queries[q]);
            int limit = results.size() < 3 ? results.size() : 3;
            for (int i = 0; i < limit; i++) {
                SearchResult r = results.get(i);
                System.out.println((i + 1) + ". " + r.document.getName()
                    + " | score=" + String.format("%.2f", r.score)
                    + " | " + r.reason);
            }
        }
    }
}
