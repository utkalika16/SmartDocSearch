
public class Document {
    private String name;
    private String path;
    private String text;
    private String normalized;

    public Document(String name, String path, String text) {
        this.name = name;
        this.path = path;
        this.text = text;
        this.normalized = TextNormalizer.normalize(text);
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public String getText() { return text; }
    public String getNormalized() { return normalized; }
}
