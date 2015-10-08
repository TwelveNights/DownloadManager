package panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;

public class DownloadDirectory extends JButton implements SwingConstants, ActionListener {

    private JFileChooser chooser;
    private String directory = System.getProperty("user.dir");

    public DownloadDirectory() {
        super(System.getProperty("user.dir"));
        setHorizontalAlignment(SwingConstants.CENTER);
        addActionListener(this);
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        // setBackground(Color.LIGHT_GRAY);

        chooser = new JFileChooser();
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