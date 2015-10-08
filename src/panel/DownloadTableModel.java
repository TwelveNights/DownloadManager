package panel;

import java.awt.event.ActionEvent;

import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import task.Mission;

public class DownloadTableModel extends AbstractTableModel {

	MissionManager manager;
	Timer timer;

	public DownloadTableModel(MissionManager manager) {
		this.manager = manager;
		timer = new Timer(1000, (ActionEvent e) -> fireTableDataChanged());
		timer.start();
	}

	@Override
	public int getColumnCount() {
		// Path, URL, Status, Progress
		return 4;
	}

	@Override
	public int getRowCount() {
		return manager.getSize();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Mission m = manager.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return m.getPath();
		case 1:
			return m.getUrl();
		case 2:
			return m.getStatus();
		case 3:
			return m.getCurrentSize();
		default:
			throw new AssertionError("Column unsupported");
		}
	}
	
	@Override
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return "Path";
		case 1:
			return "URL";
		case 2:
			return "Status";
		case 3:
			return "Progress";
		default:
			throw new AssertionError("Column unsupported");
		}
	}

}