package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import downloadexception.ExtensionException;
import task.SimpleMission;
import java.net.URLDecoder;

import org.apache.commons.io.FilenameUtils;
import task.Status;

public class DownloadWindow extends JFrame {

    private JPanel p = new JPanel();

    private MissionManager manager;
    private SimpleMission selectedMission = null;

    private JTextField http = new JTextField();
    private DownloadDirectory directory = new DownloadDirectory();
    private GridLayout controlLayout = new GridLayout(2, 2);

    private JPanel controlPanel = new JPanel(controlLayout);

    private JButton start = new JButton("start");
    private JButton resume = new JButton("resume");
    private JButton pause = new JButton("pause");
    private JButton stop = new JButton("stop");
    private JButton clear = new JButton("clear");

    private BorderLayout inputLayout = new BorderLayout();
    private BorderLayout fieldsLayout = new BorderLayout();
    private BorderLayout startLayout = new BorderLayout();

    private JPanel inputFrame = new JPanel(inputLayout);
    private JPanel fields = new JPanel(fieldsLayout);
    private JPanel startButton = new JPanel(startLayout);


    private DownloadTableModel model = new DownloadTableModel();
    private DownloadTable table = new DownloadTable(model);
    private JScrollPane scrollPane = new JScrollPane(table);

    private JLabel warning = new JLabel(" ");

    public static void main(String[] args) {
        new DownloadWindow();
    }

    public DownloadWindow() {
        super("Download Manager");

        try (FileInputStream fileIn = new FileInputStream("res/downloads.ser");
             ObjectInputStream in = new ObjectInputStream(fileIn)
        ) { manager = (MissionManager) in.readObject(); }
        catch (IOException i) { manager = new MissionManager(); }
        catch (ClassNotFoundException c) { c.printStackTrace(); }

        manager.setModel(model);
        manager.populateTable();

        for (int i = 0; i < manager.getSize(); i++) {
            SimpleMission mission = manager.get(i);
            if (mission.getFile().exists())
                mission.start();
        }

        inputLayout.setHgap(5);
        fieldsLayout.setVgap(3);
        startLayout.setHgap((3));
        controlLayout.setHgap(2);
        controlLayout.setVgap(2);

        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        setSize(500, 350);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        new TextPrompt("input the http string here...", http);

        http.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                table.clearSelection();
            }

            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        fields.add(http, BorderLayout.NORTH);
        fields.add(directory, BorderLayout.CENTER);

        start.addActionListener((ActionEvent a) -> startDownload());

        resume.addActionListener((ActionEvent a) -> {
            selectMission();
            if (selectedMission != null) {
                if (selectedMission.getFile().exists())
                    selectedMission.start();
                else {
                    int index = table.getSelectedRow();
                    manager.removeMission(table.getSelectedRow(), selectedMission);
                    manager.addMission(index, new SimpleMission(selectedMission.getUrl(), selectedMission.getPath()
                    ));
                    selectedMission.start();
                }
                selectedMission = null;
            }
            else setWarningText("Please select a download.");
        });

        pause.addActionListener((ActionEvent e) -> {
            selectMission();
            if (selectedMission != null) {
                if (selectedMission.getFile().exists())
                    selectedMission.pause();
                else {
                    manager.removeMission(table.getSelectedRow(), selectedMission);
                    setWarningText("The selected file does not exist.");
                }
                selectedMission = null;
            }
            else setWarningText("Please select a download.");
        });

        stop.addActionListener((ActionEvent e) -> {
            selectMission();
            if (selectedMission != null) try {
                manager.removeMission(table.getSelectedRow(), selectedMission);
                if (selectedMission.getFile().exists())
                    Files.delete(selectedMission.getPath());
                else setWarningText("The selected file does not exist.");
                selectedMission = null;
            } catch (IOException io) {
                io.printStackTrace();
            }
            else setWarningText("Please select a download.");
        });
        clear.addActionListener((ActionEvent e) -> {
            selectMission();
            if (selectedMission.getStatus().equals(Status.FINISHED)) {
                if (selectedMission != null) {
                    manager.removeMission(table.getSelectedRow(), selectedMission);
                    selectedMission = null;
                } else setWarningText("Please select a download.");
            }

            else setWarningText("Cannot clear log entry until download is complete.");
        });

        controlPanel.add(resume);
        controlPanel.add(pause);
        controlPanel.add(stop);
        controlPanel.add(clear);

        startButton.add(start, BorderLayout.CENTER);
        startButton.add(controlPanel, BorderLayout.EAST);

        inputFrame.add(fields, BorderLayout.CENTER);
        inputFrame.add(startButton, BorderLayout.EAST);

        inputFrame.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Download Setup"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        p.add(inputFrame);
        p.add(scrollPane);
        p.add(warning);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                manager.stopMissions();

                File file = new File("res/downloads.ser");
                if (!file.exists()) {
                    file.getParentFile().mkdir();
                    try {
                        file.createNewFile();
                    } catch (IOException r) {
                        r.printStackTrace();
                    }
                }

                try (FileOutputStream fileOut = new FileOutputStream("res/downloads.ser");
                     ObjectOutputStream out = new ObjectOutputStream(fileOut)) {

                    out.writeObject(manager);
                } catch (IOException i) {
                    i.printStackTrace();
                }
                e.getWindow().

                        dispose();
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
            if (directory.getText().equals("..."))
                setWarningText("Please choose a directory.");
            else {
                path = Paths.get(directory.getDirectory() + URLDecoder.decode(fileName, "UTF-8"));
                SimpleMission mission = new SimpleMission(url, path);
                try (FileOutputStream out = new FileOutputStream(path.toFile(), mission.getCurrentSize() != 0)) {
                }
                if (manager.contains(mission)) {
                    setWarningText("Mission is already in-progress/complete.");
                    return;
                }
                manager.addMission(mission);
                mission.start();
                setWarningText(" ");
            }
        } catch (MalformedURLException e) {
            setWarningText("Please enter a properly formatted URL for the file.");
        } catch (UnsupportedEncodingException a) {
            a.printStackTrace();
        } catch (ExtensionException x) {
            setWarningText(x.getMessage());
        } catch (FileNotFoundException f) {
            setWarningText("Folder access is denied.");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void selectMission() {
        if (table.getSelectedRow() != -1)
            selectedMission = manager.findMissionByURL(table.getRowURL(table.getSelectedRow()));
    }
}