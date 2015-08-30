package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import downloadexception.ExtensionException;
import task.SimpleMission;
import java.net.URLDecoder;

import org.apache.commons.io.FilenameUtils;

public class DownloadWindow extends JFrame {

    private JPanel p = new JPanel();

    private MissionManager manager = MissionManager.getInstance();

    private JTextField http = new JTextField();
    private DownloadDirectory directory = new DownloadDirectory();
    private GridLayout controlLayout = new GridLayout(2, 2);
    private JPanel controlPanel = new JPanel(controlLayout);
    private JButton start = new JButton("start");
    private JButton pause = new JButton("pause");
    private JButton stop = new JButton("stop");
    private JButton clear = new JButton("clear");

    private BorderLayout startLayout = new BorderLayout();
    private BorderLayout fieldsLayout = new BorderLayout();

    private JPanel inputFrame = new JPanel(startLayout);
    private JPanel fields = new JPanel(fieldsLayout);

    private DownloadTable table = new DownloadTable(new DownloadTableModel());
    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel warning = new JLabel(" ");

    public static void main(String[] args) {
        new DownloadWindow();
    }

    public DownloadWindow() {
        super("Download Manager");

        startLayout.setHgap(5);
        fieldsLayout.setVgap(3);
        controlLayout.setHgap(2);
        controlLayout.setVgap(2);

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        setSize(500, 350);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        new TextPrompt("input the http string here...", http);

        fields.add(http, BorderLayout.NORTH);
        fields.add(directory, BorderLayout.CENTER);

        start.addActionListener((ActionEvent a) -> startDownload());
        // pause.addActionListener((ActionEvent e) -> selectedMission.pause());
        // stop.addActionListener((ActionEvent e) -> selectedMission.stop());
        // clear.addActionListener((ActionEvent e) -> {
        //    DownloadWindow window = (DownloadWindow) this.getParent();
        //    window.getManager().removeMission(selectedMission);
        // });

        controlPanel.add(start);
        controlPanel.add(pause);
        controlPanel.add(stop);
        controlPanel.add(clear);

        inputFrame.add(fields, BorderLayout.CENTER);
        inputFrame.add(controlPanel, BorderLayout.EAST);

        inputFrame.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Download Setup"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        p.add(inputFrame);
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
            Path path;
            String link = http.getText();
            URL url = new URL(link);
            if (FilenameUtils.getExtension(url.getFile()) == "")
                throw new ExtensionException();
            String fileName = "\\" + FilenameUtils.getBaseName(url.getFile()) + "." + FilenameUtils.getExtension(url.getFile());
            path = Paths.get(directory.getDirectory() + URLDecoder.decode(fileName, "UTF-8"));
            SimpleMission mission = new SimpleMission(url, path);
            manager.addMission(mission);
            mission.start();
            setWarningText(" ");
            Object[] row = {path.toAbsolutePath(), link, "Todo"};
            table.insertRow(0, row);;

        } catch (MalformedURLException e) {
            setWarningText("Please enter a properly formatted URL for the file.");
        } catch (UnsupportedEncodingException a) {
            a.printStackTrace();
        } catch (ExtensionException x) {
            setWarningText(x.getMessage());
        }
    }

    public MissionManager getManager() {
        return manager;
    }
}