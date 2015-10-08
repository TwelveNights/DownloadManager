package panel;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Paths;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.io.FilenameUtils;

import task.AbstractMission;
import task.ExtendedMission;
import task.Mission;
import task.MultithreadMission;
import task.SimpleMission;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class NewMissionDialog extends JDialog {

	static final long serialVersionUID = 7555045454918003350L;

	// Integers greater than 0 means acceptable input.

	// -1 -> unsupported protocol
	// 0 -> invalid
	// 1 -> valid
	int validUrl = 0;

	// 0 -> invalid
	// 1 -> valid
	int validPath = 0;

	boolean available = false;

	// Elements in mainPanel
	JTextField urlField;
	JTextField pathField;
	JCheckBox multithreadBox;
	JCheckBox extensionMaskBox;
	JLabel warning;

	// Elements in lowerPanel
	JButton yesButton;

	public NewMissionDialog(Frame owner) {
		super(owner, "New Mission...", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationRelativeTo(owner);
		setSize(400, 250);

		{
			JPanel mainPanel = new JPanel();

			GroupLayout mainLayout = new GroupLayout(mainPanel);
			mainPanel.setLayout(mainLayout);
			mainLayout.setAutoCreateGaps(true);
			mainLayout.setAutoCreateContainerGaps(true);

			JLabel urlLabel = new JLabel("URL:");
			JLabel pathLabel = new JLabel("Path:");

			urlField = new JTextField();
			urlField.setMaximumSize(new Dimension(Integer.MAX_VALUE, urlField.getPreferredSize().height));
			urlField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					return;
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					validate();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					validate();
				}

				/**
				 * Checks if URL is valid and the protocol is supported.
				 */
				private void validate() {
					try {
						URL url = new URL(urlField.getText());
						String protocol = url.getProtocol();
						if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("https")) {
							validUrl = 1;
						} else {
							validUrl = -1;
						}
					} catch (MalformedURLException e) {
						validUrl = 0;
					} finally {
						updateStatus();
					}
				}
			});

			pathField = new JTextField();
			pathField.setMaximumSize(new Dimension(Integer.MAX_VALUE, pathField.getPreferredSize().height));
			pathField.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void changedUpdate(DocumentEvent e) {
					return;
				}

				@Override
				public void insertUpdate(DocumentEvent e) {
					validate();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					validate();
				}

				/**
				 * Checks if path is valid.
				 */
				private void validate() {
					if (Paths.get(pathField.getText()).toFile().canWrite()) {
						validPath = 1;
					} else {
						validPath = 0;
					}
					
					updateStatus();
				}
			});

			JButton chooserButton = new JButton("...");
			chooserButton.addActionListener((ActionEvent e) -> {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES );
				int returnVal = chooser.showOpenDialog(NewMissionDialog.this);
				if (returnVal == JFileChooser.APPROVE_OPTION)
					pathField.setText(chooser.getSelectedFile().getAbsolutePath());
			});

			multithreadBox = new JCheckBox("Multi-thread Download", true);
			extensionMaskBox = new JCheckBox("Mask Extension", true);

			warning = new JLabel();

			//@formatter:off
			mainLayout.setHorizontalGroup(mainLayout.createSequentialGroup()
					.addGroup(mainLayout.createParallelGroup()
							.addComponent(urlLabel)
							.addComponent(pathLabel))
					.addGroup(mainLayout.createParallelGroup()
							.addComponent(urlField)
							.addGroup(mainLayout.createSequentialGroup()
									.addComponent(pathField)
									.addComponent(chooserButton))
							.addComponent(multithreadBox)
							.addComponent(extensionMaskBox)
							.addComponent(warning)));
			
			mainLayout.setVerticalGroup(mainLayout.createSequentialGroup()
					.addGroup(mainLayout.createParallelGroup()
							.addComponent(urlLabel)
							.addComponent(urlField))
					.addGroup(mainLayout.createParallelGroup()
							.addComponent(pathLabel)
							.addComponent(pathField)
							.addComponent(chooserButton))
					.addComponent(multithreadBox)
					.addComponent(extensionMaskBox)
					.addComponent(warning));
			//@formatter:on

			getContentPane().add(mainPanel, BorderLayout.CENTER);
		}

		{
			JPanel lowerPanel = new JPanel();
			getContentPane().add(lowerPanel, BorderLayout.SOUTH);
			lowerPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));

			yesButton = new JButton("Accept");
			yesButton.setEnabled(false);
			yesButton.addActionListener((ActionEvent e) -> {
				available = true;
				NewMissionDialog.this.dispose();
			});

			JButton noButton = new JButton("Cancel");
			noButton.addActionListener((ActionEvent e) -> {
				NewMissionDialog.this.dispose();
			});

			lowerPanel.add(yesButton);
			lowerPanel.add(noButton);
		}
		
		setVisible(true);

	}

	void updateStatus() {
		if (validUrl > 0 && validPath > 0) {
			yesButton.setEnabled(true);
			warning.setText("");
		} else {
			yesButton.setEnabled(false);

			String urlMsg;
			switch (validUrl) {
			case -1:
				urlMsg = "Unsupported protocol<br>";
				break;
			case 0:
				urlMsg = "Invalid URL<br>";
				break;
			default:
				urlMsg = "";
			}

			String pathMsg;
			switch (validPath) {
			case 0:
				pathMsg = "Invalid file path<br>";
				break;
			default:
				pathMsg = "";
			}

			String msg = "<html>" + urlMsg + pathMsg + "</html>";

			warning.setText(msg);
		}
	}

	/**
	 * @return the new mission. null if the new mission is canceled.
	 */
	public Mission get() {

		if (available) {

			try {
				URL url = new URL(urlField.getText());
				File file = Paths.get(pathField.getText()).toFile();

				// Optionally adds file name
				if (file.isDirectory())
					file = new File(file, FilenameUtils.getName(URLDecoder.decode(url.getFile(), "UTF-8")));

				AbstractMission m;
				if (multithreadBox.isSelected()) {
					m = new MultithreadMission(url, file);
				} else {
					m = new SimpleMission(url, file);
				}

				return extensionMaskBox.isSelected() ? new ExtendedMission(m, ".dmt") : m;

			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return null;
	}
}
