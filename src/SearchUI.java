
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class SearchUI extends JFrame {
    private SearchEngine engine;
    private JTextField query;
    private JTextArea output;
    private JLabel status;

    public SearchUI(SearchEngine engine) {
        this.engine = engine;
        setTitle("Smart Document Search System");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel top = new JPanel(new BorderLayout(8, 8));
        JLabel title = new JLabel("SMART DOCUMENT SEARCH");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        top.add(title, BorderLayout.NORTH);

        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        query = new JTextField();
        query.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JButton search = new JButton("Search");
        JButton add = new JButton("Add Folder");
        searchBar.add(query, BorderLayout.CENTER);
        searchBar.add(search, BorderLayout.EAST);
        searchBar.add(add, BorderLayout.WEST);
        top.add(searchBar, BorderLayout.CENTER);

        root.add(top, BorderLayout.NORTH);

        output = new JTextArea();
        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 14));
        root.add(new JScrollPane(output), BorderLayout.CENTER);

        status = new JLabel("Indexed documents: " + engine.getDocumentCount());
        root.add(status, BorderLayout.SOUTH);

        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { runSearch(); }
        });
        query.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { runSearch(); }
        });
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) { addFolder(); }
        });

        setContentPane(root);
        setVisible(true);
    }

    private void runSearch() {
        String q = query.getText().trim();
        if (q.length() == 0) return;

        ResultList results = engine.search(q);
        StringBuilder b = new StringBuilder();
        b.append("QUERY: ").append(q).append("\n");
        b.append("RESULTS: ").append(results.size()).append("\n");
        b.append("------------------------------------------------------------\n\n");

        for (int i = 0; i < results.size(); i++) {
            SearchResult r = results.get(i);
            b.append("#").append(i + 1).append("  ").append(r.document.getName()).append("\n");
            b.append("Score: ").append(String.format("%.2f", r.score)).append("\n");
            b.append(r.reason).append("\n");
            b.append("Preview: ").append(engine.getPreview(r, q)).append("\n");
            b.append("Path: ").append(r.document.getPath()).append("\n");
            b.append("------------------------------------------------------------\n");
        }

        if (results.size() == 0) {
            b.append("No relevant document found.\n");
            b.append("Try a shorter phrase, a different keyword, or a spelling variant.\n");
        }
        output.setText(b.toString());
        status.setText("Indexed documents: " + engine.getDocumentCount() + " | Search completed");
    }

    private void addFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = chooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            engine.loadFolder(chooser.getSelectedFile().getAbsolutePath());
            status.setText("Indexed documents: " + engine.getDocumentCount() + " | Folder loaded");
        }
    }
}
