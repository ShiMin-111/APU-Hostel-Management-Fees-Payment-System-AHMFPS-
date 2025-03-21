package ahmfps;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class AuditLog {
    JFrame logFrame;
    JTable logTable;

    public AuditLog() {
        logFrame = new JFrame("Audit Log");
        logFrame.setSize(800, 400);
        logFrame.setLocationRelativeTo(null); 

        // Load the log data
        String[] columnNames = {"Name", "User Type", "Login Time", "Logout Time", "Session Duration"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0){
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        loadAuditLog(tableModel);   // Call the table with data

        // Initialize the table
        logTable = new JTable(tableModel);
        logTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        logTable.setFillsViewportHeight(true);

        // Adjust column widths
        TableColumn nameColumn = logTable.getColumnModel().getColumn(0); // "Name"
        nameColumn.setPreferredWidth(100); 
        nameColumn.setMinWidth(80);
        nameColumn.setMaxWidth(150);

        TableColumn userTypeColumn = logTable.getColumnModel().getColumn(1); // "User Type"
        userTypeColumn.setPreferredWidth(80); 
        userTypeColumn.setMinWidth(70);
        userTypeColumn.setMaxWidth(100);

        // Center-align the table data
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);

        // Apply the renderer to all columns
        for (int i = 0; i < logTable.getColumnCount(); i++) {
            logTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(logTable);
        scrollPane.setPreferredSize(new Dimension(780, 360));

        logFrame.setLayout(new BorderLayout());
        logFrame.add(scrollPane, BorderLayout.CENTER);

        logFrame.setVisible(true); // Show the frame

        logFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.third.x.setVisible(true); // Navigate back to the previous screen
            }
        });
    }

    private void loadAuditLog(DefaultTableModel tableModel) {
        try {
            File logFile = new File("auditLog.txt");
            if (!logFile.exists()) {
                JOptionPane.showMessageDialog(null, "No audit log available.");
                return;
            }

            // Read the file content
            Scanner s = new Scanner(logFile);
            ArrayList<String[]> logEntries = new ArrayList<>();
            while (s.hasNextLine()) {
                String name = s.nextLine();
                String userType = s.nextLine();
                String loginTime = s.nextLine();
                String logoutTime = s.nextLine();
                String sessionDuration = s.nextLine();

                s.nextLine(); 
                logEntries.add(new String[]{name, userType, loginTime, logoutTime, sessionDuration});
            }
            s.close();

            for (String[] logEntry : logEntries) {
                tableModel.addRow(logEntry);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading audit log!");
        }
    }
}
