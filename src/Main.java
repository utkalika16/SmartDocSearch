
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SearchEngine engine = new SearchEngine();
                engine.loadFolder("docs");
                new SearchUI(engine);
            }
        });
    }
}
