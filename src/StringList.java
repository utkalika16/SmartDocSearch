
public class StringList {
    private static class Node {
        String value;
        Node next;
        Node(String value) { this.value = value; }
    }

    private Node head;
    private int size;

    public void add(String value) {
        Node n = new Node(value);
        if (head == null) head = n;
        else {
            Node p = head;
            while (p.next != null) p = p.next;
            p.next = n;
        }
        size++;
    }

    public int size() { return size; }

    public String get(int index) {
        Node p = head;
        for (int i = 0; i < index && p != null; i++) p = p.next;
        return p == null ? null : p.value;
    }
}
