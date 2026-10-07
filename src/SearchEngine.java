import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class SearchEngine {
    private DocumentStore store = new DocumentStore();
    private HashTable index = new HashTable();
    private SuffixArray[] suffixArrays = new SuffixArray[8];
    private int suffixCount = 0;

    public void loadFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) return;
        File[] files = folder.listFiles();
        if (files == null) return;
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile() && files[i].getName().toLowerCase().endsWith(".txt")) loadFile(files[i]);
        }
    }

    public void loadFile(File file) {
        try {
            StringBuilder b = new StringBuilder();
            BufferedReader r = new BufferedReader(new FileReader(file));
            String line;
            while ((line = r.readLine()) != null) b.append(line).append('\n');
            r.close();
            Document d = new Document(file.getName(), file.getAbsolutePath(), b.toString());
            int id = store.size();
            store.add(d);
            String[] tokens = TextNormalizer.tokenize(d.getNormalized());
            for (int i = 0; i < tokens.length; i++) index.add(tokens[i], id);
            if (suffixCount == suffixArrays.length) {
                SuffixArray[] n = new SuffixArray[suffixArrays.length * 2];
                for (int i = 0; i < suffixCount; i++) n[i] = suffixArrays[i];
                suffixArrays = n;
            }
            suffixArrays[suffixCount++] = new SuffixArray(d.getNormalized());
        } catch (IOException e) {
            System.out.println("Could not load: " + file.getName());
        }
    }

    public int getDocumentCount() { return store.size(); }
    public Document getDocument(int i) { return store.get(i); }

    public ResultList search(String query) { return search(query, "Automatic"); }

    public ResultList search(String query, String method) {
        ResultList results = new ResultList();
        String q = TextNormalizer.normalize(query);
        if (q.length() == 0) return results;
        String[] terms = TextNormalizer.tokenize(q);
        String selected = method == null || method.length() == 0 ? "Automatic" : method;

        for (int i = 0; i < store.size(); i++) {
            Document d = store.get(i);
            SearchResult r = new SearchResult(d);
            for (int t = 0; t < terms.length; t++) {
                Postings p = index.get(terms[t]);
                if (p != null) for (int x = 0; x < p.size(); x++) if (p.get(x) == i) { r.keywordHits++; break; }
            }

            r.kmpHits = KMP.count(d.getNormalized(), q);
            r.rabinHits = RabinKarp.count(d.getNormalized(), q);
            String local = shorten(d.getNormalized(), q, 160);
            r.editDistance = EditDistance.levenshtein(q, local);
            r.suffixPrefix = suffixArrays[i].longestPrefix(q);

            double termCoverage = terms.length == 0 ? 0 : (double) r.keywordHits / terms.length;
            double exact = r.kmpHits > 0 ? 1.0 : 0.0;
            double fuzzy = fuzzyTermSimilarity(terms, d.getNormalized());
            double suffix = q.length() == 0 ? 0 : (double) r.suffixPrefix / q.length();

            if ("Automatic".equalsIgnoreCase(selected)) {
                r.score = 45 * termCoverage + 25 * exact + 15 * suffix + 15 * fuzzy;
                if (r.kmpHits > 0) r.score += 10;
                if (r.keywordHits > 0 || r.kmpHits > 0 || fuzzy >= 0.55 || suffix >= 0.40) {
                    r.reason = buildReason(r, terms.length, "Automatic ranking");
                    results.add(r);
                }
            } else {
                double methodScore = methodScore(selected, d.getNormalized(), q, terms, termCoverage, fuzzy, suffix, r);
                r.score = methodScore;
                boolean match = methodMatches(selected, d.getNormalized(), q, terms, r, fuzzy, suffix);
                if (match) {
                    r.reason = buildReason(r, terms.length, selected + " score");
                    results.add(r);
                }
            }
        }
        results.sortDescending();
        return results;
    }

    private double methodScore(String method, String text, String q, String[] terms, double coverage, double fuzzy, double suffix, SearchResult r) {
        if ("Edit Distance".equalsIgnoreCase(method)) return fuzzy * 100.0;
        if ("Suffix Array + LCP".equalsIgnoreCase(method)) return suffix * 100.0;
        if ("Aho-Corasick".equalsIgnoreCase(method)) {
            int hits = AhoCorasick.count(text, terms);
            return terms.length == 0 ? 0 : Math.min(100.0, (double) hits / terms.length * 50.0 + coverage * 50.0);
        }
        int hits;
        if ("Naive".equalsIgnoreCase(method)) hits = NaiveSearch.count(text, q);
        else if ("KMP".equalsIgnoreCase(method)) hits = r.kmpHits;
        else if ("Rabin-Karp".equalsIgnoreCase(method)) hits = r.rabinHits;
        else if ("Z-Function".equalsIgnoreCase(method)) hits = ZSearch.count(text, q);
        else hits = r.kmpHits;
        if (hits > 0) return Math.min(100.0, 70.0 + coverage * 30.0 + Math.min(hits, 5) * 1.0);
        return fuzzy * 25.0 + coverage * 25.0;
    }

    private boolean methodMatches(String method, String text, String q, String[] terms, SearchResult r, double fuzzy, double suffix) {
        if ("Edit Distance".equalsIgnoreCase(method)) return fuzzy >= 0.55;
        if ("Suffix Array + LCP".equalsIgnoreCase(method)) return suffix >= 0.20;
        if ("Aho-Corasick".equalsIgnoreCase(method)) return AhoCorasick.count(text, terms) > 0;
        if ("Naive".equalsIgnoreCase(method)) return NaiveSearch.count(text, q) > 0;
        if ("KMP".equalsIgnoreCase(method)) return r.kmpHits > 0;
        if ("Rabin-Karp".equalsIgnoreCase(method)) return r.rabinHits > 0;
        if ("Z-Function".equalsIgnoreCase(method)) return ZSearch.count(text, q) > 0;
        return true;
    }

    private double fuzzyTermSimilarity(String[] queryTerms, String documentText) {
        if (queryTerms.length == 0) return 0;
        String[] docTerms = TextNormalizer.tokenize(documentText);
        if (docTerms.length == 0) return 0;
        double total = 0;
        for (int i = 0; i < queryTerms.length; i++) {
            double best = 0;
            for (int j = 0; j < docTerms.length; j++) {
                double sim = EditDistance.similarity(queryTerms[i], docTerms[j]);
                if (sim > best) best = sim;
                if (best == 1.0) break;
            }
            total += best;
        }
        return total / queryTerms.length;
    }

    private String shorten(String text, String query, int max) {
        int p = KMP.first(text, query);
        if (p >= 0) {
            int start = Math.max(0, p - 60);
            int end = Math.min(text.length(), p + query.length() + 60);
            return text.substring(start, end);
        }
        return text.length() <= max ? text : text.substring(0, max);
    }

    private String buildReason(SearchResult r, int termCount, String label) {
        StringBuilder b = new StringBuilder();
        b.append(label).append(" • Term coverage ").append(r.keywordHits).append("/").append(termCount);
        if (r.kmpHits > 0) b.append(" • KMP ×").append(r.kmpHits);
        if (r.rabinHits > 0) b.append(" • Rabin-Karp ×").append(r.rabinHits);
        b.append(" • Edit distance ").append(r.editDistance);
        b.append(" • Suffix LCP ").append(r.suffixPrefix);
        return b.toString();
    }

    public String getPreview(SearchResult r, String query) {
        String text = r.document.getText();
        String nq = TextNormalizer.normalize(query);
        String nt = r.document.getNormalized();
        int p = KMP.first(nt, nq);
        if (p < 0) p = 0;
        int start = Math.max(0, p - 90);
        int end = Math.min(nt.length(), p + nq.length() + 130);
        if (end <= start) return text.length() > 300 ? text.substring(0, 300) : text;
        return nt.substring(start, end);
    }
}
