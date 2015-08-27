import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class DownloadWindow extends JFrame {

    private JPanel p = new JPanel();

    private JTextField http = new JTextField();
    private TextPrompt prompt = new TextPrompt("input the http string here...", http);
    private DownloadDirectory directory = new DownloadDirectory();

    private JPanel starter = new JPanel(new BorderLayout());
    private JPanel fields = new JPanel(new BorderLayout());
    private JButton start = new JButton("start");

    private JScrollPane table = new JScrollPane(new DownloadTable());

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

        starter.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Download Setup"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        p.add(starter);
        p.add(table);

        add(p);

        setVisible(true);
    }
}