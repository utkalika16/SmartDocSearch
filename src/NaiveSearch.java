public class NaiveSearch {
    public static int count(String text, String pattern) {
        if (pattern.length() == 0 || pattern.length() > text.length()) return 0;
        int c = 0;
        for (int i = 0; i <= text.length() - pattern.length(); i++) {
            int j = 0;
            while (j < pattern.length() && text.charAt(i + j) == pattern.charAt(j)) j++;
            if (j == pattern.length()) c++;
        }
        return c;
    }
}
