import javax.swing.table.DefaultTableModel;
import javax.swing.JCheckBox;

public class DownloadTableModel extends DefaultTableModel {
    public DownloadTableModel() {
        super(15, 4);
        String[] colNames = {"Path", "Progress", "Total", "Complete"};
        setColumnIdentifiers(colNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}