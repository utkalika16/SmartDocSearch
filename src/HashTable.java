
public class HashTable {
    private static class Entry {
        String key;
        Postings postings;
        Entry next;
        Entry(String key, int docId) {
            this.key = key;
            this.postings = new Postings();
            this.postings.add(docId);
        }
    }

    private Entry[] table = new Entry[257];

    private int index(String key) {
        long h = 0;
        for (int i = 0; i < key.length(); i++) h = (h * 31 + key.charAt(i)) & 0x7fffffffL;
        return (int)(h % table.length);
    }

    public void add(String key, int docId) {
        if (key == null || key.length() == 0) return;
        int i = index(key);
        Entry e = table[i];
        while (e != null) {
            if (e.key.equals(key)) {
                e.postings.add(docId);
                return;
            }
            e = e.next;
        }
        Entry n = new Entry(key, docId);
        n.next = table[i];
        table[i] = n;
    }

    public Postings get(String key) {
        if (key == null) return null;
        Entry e = table[index(key)];
        while (e != null) {
            if (e.key.equals(key)) return e.postings;
            e = e.next;
        }
        return null;
    }
}
