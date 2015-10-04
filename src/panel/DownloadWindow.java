package panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import task.SimpleMission;
import java.net.URLDecoder;

import org.apache.commons.io.FilenameUtils;

import task.Status;
import task.Mission;

public class DownloadWindow extends JFrame {

	private JPanel panel = new JPanel();

	private MissionManager manager;

	private JTextField http = new JTextField();
	private DownloadDirectory directory = new DownloadDirectory();
	private GridLayout controlLayout = new GridLayout(2, 2);

	private JPanel controlPanel = new JPanel(controlLayout);

	private JButton start = new JButton("Start");
	private JButton action = new JButton("Resume"); // or pause
	private JButton abort = new JButton("Abort");
	private JButton remove = new JButton("Remove");

	private BorderLayout inputLayout = new BorderLayout();
	private BorderLayout fieldsLayout = new BorderLayout();

	private JPanel inputFrame = new JPanel(inputLayout);
	private JPanel fields = new JPanel(fieldsLayout);

	private DownloadTable table;
	private JScrollPane scrollPane;

	private JLabel warning = new JLabel(" ");

	public static void main(String[] args) {
		new DownloadWindow();
	}

	public DownloadWindow() {
		super("Download Manager");

		try (FileInputStream fileIn = new FileInputStream("res/downloads.ser");
				ObjectInputStream in = new ObjectInputStream(fileIn)) {
			manager = (MissionManager) in.readObject();
		} catch (IOException i) {
			manager = new MissionManager();
		} catch (ClassNotFoundException c) {
			c.printStackTrace();
		}
		table = new DownloadTable(manager);
		scrollPane = new JScrollPane(table);

		manager.getMissionStream().filter((Mission m) -> m.getStatus() == Status.PAUSED)
				.forEach((Mission m) -> m.start());

		inputLayout.setHgap(5);
		fieldsLayout.setVgap(3);
		controlLayout.setHgap(2);
		controlLayout.setVgap(2);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		setSize(500, 350);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		new TextPrompt("http input", http, TextPrompt.Show.FOCUS_LOST);

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
		fields.add(warning, BorderLayout.SOUTH);

		start.addActionListener((ActionEvent a) -> {
			NewMissionDialog dialog = new NewMissionDialog(DownloadWindow.this);
			Mission m = dialog.get();
			if (m != null) {
				manager.addMission(m);
				m.start();
			}
		});

		action.addActionListener((ActionEvent a) -> {
			// TODO
			Mission m = table.getSelectedMission();
			if (m != null) {
				Status s = m.getStatus();
				if (s == Status.PAUSED || s == Status.FAILED)
					m.start();
				if (s == Status.IN_PROGRESS)
					m.pause();
			} else
				setWarningText("Please select a download.");
		});

		/*
		 * TODO pause.addActionListener((ActionEvent e) -> { Mission m =
		 * table.getSelectedMission(); if (m != null) { if (m.getStatus() ==
		 * Status.IN_PROGRESS) m.pause(); else { setWarningText(
		 * "Selected download is not in progress"); } } else setWarningText(
		 * "Please select a download."); });
		 */

		abort.addActionListener((ActionEvent e) -> {
			Mission m = table.getSelectedMission();
			if (m != null) {
				try {
					manager.safelyAbortMission(m);
				} catch (InterruptedException e0) {
					e0.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} else
				setWarningText("Please select a download.");
		});

		remove.addActionListener((ActionEvent e) -> {
			Mission m = table.getSelectedMission();
			if (m != null) {
				try {
					manager.safelyRemoveMission(m);
				} catch (InterruptedException e0) {
					e0.printStackTrace();
				}
			} else
				setWarningText("Please select a download.");
		});

		controlPanel.add(start);
		controlPanel.add(action);
		controlPanel.add(abort);
		controlPanel.add(remove);

		inputFrame.add(fields, BorderLayout.CENTER);
		inputFrame.add(controlPanel, BorderLayout.EAST);

		inputFrame.setBorder(javax.swing.BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Download Setup"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		panel.add(inputFrame);
		panel.add(scrollPane);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				manager.stopMissions();

				File file = new File("res/downloads.ser");
				try {
					file.createNewFile();
				} catch (IOException ex) {
					ex.printStackTrace();
				}

				manager.joinMissions();

				try (FileOutputStream fileOut = new FileOutputStream(file);
						ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
					out.writeObject(manager);
				} catch (IOException i) {
					i.printStackTrace();
				}

				e.getWindow().dispose();
			}
		});

		add(panel);
		setVisible(true);
	}

	public void setWarningText(String text) {
		warning.setText(text);
	}

	private void startDownload() {
		try {
			File file;
			URL url;
			String link = http.getText();
			url = new URL(link);
			// TODO add an arbitrary extension
			String fileName = FilenameUtils.getName(url.getFile());

			file = new File(Paths.get(directory.getDirectory()).toFile() + URLDecoder.decode(fileName, "UTF-8"));
			SimpleMission mission = new SimpleMission(url, file);
			if (manager.contains(mission)) {
				setWarningText("Mission is already in-progress/complete.");
				return;
			}
			manager.addMission(mission);
			mission.start();
		} catch (MalformedURLException e) {
			setWarningText("Please enter a properly formatted URL for the file.");
		} catch (UnsupportedEncodingException a) {
			a.printStackTrace();
		}
	}
}