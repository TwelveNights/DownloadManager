import javax.swing.JTable;

public class DownloadTable extends JTable {

    public DownloadTable() {
        super(new DownloadTableModel());
        showHorizontalLines = false;
        setAutoCreateRowSorter(true);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setResizingAllowed(false);
    }
}