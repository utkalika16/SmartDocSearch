
public class SearchResult {
    public Document document;
    public double score;
    public int keywordHits;
    public int kmpHits;
    public int rabinHits;
    public int editDistance;
    public int suffixPrefix;
    public String reason;

    public SearchResult(Document document) {
        this.document = document;
    }
}
