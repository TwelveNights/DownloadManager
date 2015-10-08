package panel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import task.Mission;

public class DownloadTable extends JTable {

	MissionManager manager;
	Mission selectedMission;

	public DownloadTable(MissionManager manager) {
		super(new DownloadTableModel(manager));
		showHorizontalLines = true;
		setAutoCreateRowSorter(true);
		getTableHeader().setReorderingAllowed(false);
		// getTableHeader().setResizingAllowed(false);
		setCellSelectionEnabled(true);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Updates selectedMission upon select
		selectionModel.addListSelectionListener(e -> {

			DownloadTable table = DownloadTable.this;
			int row = table.getSelectedRow();
			selectedMission = (row == -1) ? null : manager.get(table.convertRowIndexToModel(row));
		});
	}

	public Mission getSelectedMission() {
		return selectedMission;
	}

	/*
	 * @Override public String getToolTipText(MouseEvent e) { int row =
	 * rowAtPoint(e.getPoint()); int column = columnAtPoint(e.getPoint());
	 * 
	 * Object value = getValueAt(row, column); return value == null ? null :
	 * value.toString(); }
	 * 
	 * public Mission getSelectedMission() { return selectedMission; }
	 */

}