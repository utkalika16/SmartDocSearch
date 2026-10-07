
public class Postings {
    private int[] ids = new int[4];
    private int size = 0;

    public void add(int id) {
        if (size > 0 && ids[size - 1] == id) return;
        if (size == ids.length) {
            int[] next = new int[ids.length * 2];
            for (int i = 0; i < size; i++) next[i] = ids[i];
            ids = next;
        }
        ids[size++] = id;
    }

    public int size() { return size; }
    public int get(int i) { return ids[i]; }
}
