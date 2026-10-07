
public class ResultList {
    private SearchResult[] data = new SearchResult[8];
    private int size = 0;

    public void add(SearchResult r) {
        if (size == data.length) {
            SearchResult[] n = new SearchResult[data.length * 2];
            for (int i = 0; i < size; i++) n[i] = data[i];
            data = n;
        }
        data[size++] = r;
    }

    public int size() { return size; }
    public SearchResult get(int i) { return data[i]; }

    public void sortDescending() {
        for (int i = 1; i < size; i++) {
            SearchResult key = data[i];
            int j = i - 1;
            while (j >= 0 && data[j].score < key.score) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = key;
        }
    }
}
