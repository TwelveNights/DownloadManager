package panel;

import javax.swing.table.DefaultTableModel;

public class DownloadTableModel extends DefaultTableModel {
    public DownloadTableModel() {
        super(0, 3);
        String[] colNames = {"Path", "Link", "Progress"};
        setColumnIdentifiers(colNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

}