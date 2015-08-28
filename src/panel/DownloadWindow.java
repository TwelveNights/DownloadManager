package panel;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import task.SimpleMission;

public class DownloadWindow extends JFrame {

    private JPanel p = new JPanel();

    private JTextField http = new JTextField();
    private DownloadDirectory directory = new DownloadDirectory();

    private JPanel starter = new JPanel(new BorderLayout());
    private JPanel fields = new JPanel(new BorderLayout());
    private JButton start = new JButton("start");

    private DownloadTable table = new DownloadTable(new DownloadTableModel());
    private JScrollPane scrollPane = new JScrollPane(table);
    private JLabel warning = new JLabel(" ");

    public static void main(String[] args) {
        new DownloadWindow();
    }

    public DownloadWindow() {
        super("Download Manager");

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        start.addActionListener((ActionEvent a) ->
                {
                    startDownload();
                });

        setSize(500, 350);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        new TextPrompt("input the http string here...", http);

        fields.add(http, BorderLayout.NORTH);
        fields.add(directory, BorderLayout.CENTER);

        starter.add(fields, BorderLayout.NORTH);
        starter.add(Box.createRigidArea((new Dimension(0, 3))), BorderLayout.CENTER);
        starter.add(start, BorderLayout.SOUTH);

        starter.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Download Setup"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        p.add(starter);
        p.add(scrollPane);
        p.add(warning);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
            }
        });

        add(p);
        setVisible(true);
    }

    public void setWarningText(String txt) {
        warning.setText(txt);
    }

    private void startDownload() {
        try {
            URL url = new URL(http.getText());
            Path path = Paths.get(directory.getDirectory() + url.getFile());
            SimpleMission mission = new SimpleMission(url, path);
            mission.start();
            setWarningText(" ");
            Object[] row = {directory.getDirectory(), http.getText(), "Todo"};
            updateTable(row);

        } catch (MalformedURLException e) {
            setWarningText("Please enter the URL for a file.");
        }
    }

    private void updateTable(Object[] row) {
        table.insertRow(0, row);
    }
}