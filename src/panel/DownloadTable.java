package panel;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTable extends JTable {

    private DownloadTableModel model;

    public DownloadTable(DownloadTableModel dtm) {
        super(dtm);
        model = dtm;
        showHorizontalLines = false;
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(false);
        // getTableHeader().setResizingAllowed(false);
        setCellSelectionEnabled(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int row = rowAtPoint(e.getPoint());
        int column = columnAtPoint(e.getPoint());

        Object value = getValueAt(row, column);
        return value == null ? null : value.toString();
    }

    public URL getRowURL(int row) {
        try {
            return new URL(model.getValueAt(row, 1).toString());
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return null;
    }
}