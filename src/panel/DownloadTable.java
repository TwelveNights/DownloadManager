package panel;

import javax.swing.JTable;
import java.awt.event.MouseEvent;

public class DownloadTable extends JTable {

    private DownloadTableModel model;

    public DownloadTable(DownloadTableModel dtm) {
        super(dtm);
        model = dtm;
        showHorizontalLines = false;
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int row = rowAtPoint(e.getPoint());
        int column = columnAtPoint(e.getPoint());

        Object value = getValueAt(row, column);
        return value == null ? null : value.toString();
    }

    public DownloadTableModel getDownloadTableModel() {
        return model;
    }
}