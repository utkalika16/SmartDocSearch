
public class DocumentStore {
    private Document[] data = new Document[8];
    private int size = 0;

    public void add(Document d) {
        if (size == data.length) {
            Document[] next = new Document[data.length * 2];
            for (int i = 0; i < size; i++) next[i] = data[i];
            data = next;
        }
        data[size++] = d;
    }

    public int size() { return size; }
    public Document get(int i) { return data[i]; }
}
