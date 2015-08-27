import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JFrame;
import java.awt.Dimension;
import javax.swing.Box;

public class DownloadWindow extends JFrame {

    private JPanel p = new JPanel();
    private JButton start = new JButton("start");
    private JPanel starter = new JPanel(new BorderLayout());
    private JTextField http = new JTextField();
    private DownloadDirectory directory = new DownloadDirectory();
    private JPanel fields = new JPanel(new BorderLayout());
    private JScrollPane table = new JScrollPane(new DownloadTable());
    private TextPrompt prompt = new TextPrompt("input the http string here...", http);

    public static void main(String[] args) {
        new DownloadWindow();
    }

    public DownloadWindow() {
        super("Download Manager");

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        fields.add(http, BorderLayout.NORTH);
        fields.add(directory, BorderLayout.CENTER);

        starter.add(fields, BorderLayout.NORTH);
        starter.add(Box.createRigidArea((new Dimension(0, 3))), BorderLayout.CENTER);
        starter.add(start, BorderLayout.SOUTH);

        p.add(starter);
        p.add(Box.createRigidArea((new Dimension(0, 15))));
        p.add(table);

        add(p);

        setVisible(true);
    }
}