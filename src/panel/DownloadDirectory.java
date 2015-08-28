package panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;
import java.awt.Color;

public class DownloadDirectory extends JButton implements SwingConstants, ActionListener {

    private JFileChooser chooser;
    private String directory = "C:\\";

    public DownloadDirectory() {
        super("...");
        chooser = new JFileChooser();
        setHorizontalAlignment(SwingConstants.CENTER);
        addActionListener(this);
        setBackground(Color.LIGHT_GRAY);

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