
public class TextNormalizer {
    public static String normalize(String s) {
        if (s == null) return "";
        StringBuilder b = new StringBuilder();
        boolean space = false;
        for (int i = 0; i < s.length(); i++) {
            char c = Character.toLowerCase(s.charAt(i));
            if (Character.isLetterOrDigit(c)) {
                b.append(c);
                space = false;
            } else if (!space) {
                b.append(' ');
                space = true;
            }
        }
        return b.toString().trim();
    }

    public static String[] tokenize(String s) {
        String n = normalize(s);
        if (n.length() == 0) return new String[0];
        int count = 1;
        for (int i = 0; i < n.length(); i++) if (n.charAt(i) == ' ') count++;
        String[] out = new String[count];
        int start = 0, k = 0;
        for (int i = 0; i <= n.length(); i++) {
            if (i == n.length() || n.charAt(i) == ' ') {
                if (i > start) out[k++] = n.substring(start, i);
                start = i + 1;
            }
        }
        if (k == out.length) return out;
        String[] trimmed = new String[k];
        for (int i = 0; i < k; i++) trimmed[i] = out[i];
        return trimmed;
    }
}
