import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import java.awt.event.*;

public class DownloadDirectory extends JButton implements SwingConstants, ActionListener {

    private JFileChooser chooser;
    private String directory = "C:\\";

    public DownloadDirectory() {
        super("...");
        chooser = new JFileChooser();
        setHorizontalAlignment(SwingConstants.CENTER);
        addActionListener(this);
        setContentAreaFilled(false);

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    public void actionPerformed(ActionEvent e) {
        int returnVal = chooser.showOpenDialog(DownloadDirectory.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            directory = chooser.getSelectedFile().getAbsolutePath();
            setText(directory);
        }
    }

    public String getDirectory() {
        return directory;
    }
}