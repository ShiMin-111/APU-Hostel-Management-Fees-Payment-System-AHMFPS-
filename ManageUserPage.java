package ahmfps;

import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ManageUserPage{
    JFrame frame;
    JTable residentTable;
    JButton deleteUserButton, updatePasswordButton,  backButton, searchButton;
    JTextField searchField;
    
    public ManageUserPage() {
            frame = new JFrame("Manage User - Manager");
            frame.setSize(800, 550);
            frame.setLocationRelativeTo(null);

            JPanel panel = new JPanel();
            panel.setLayout(null);

            JLabel titleLabel = new JLabel("Manage User", JLabel.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setBounds(0, 10, 800, 30);
            panel.add(titleLabel);

            // Search field and button
            JLabel searchLabel = new JLabel("Search by Username:");
            searchLabel.setBounds(20, 50, 200, 30);
            panel.add(searchLabel);

            searchField = new JTextField();
            searchField.setBounds(220, 50, 160, 30);
            panel.add(searchField);

            searchButton = new JButton("Search");
            searchButton.setBounds(400, 50, 120, 30);
            searchButton.addActionListener(e -> loadResidents(searchField.getText().trim()));
            panel.add(searchButton);

            // Residents table
            residentTable = new JTable(new DefaultTableModel(new Object[]{"Username", "User Type", "Email", "Password", "Room"}, 0));
            JScrollPane tableScroll = new JScrollPane(residentTable);
            tableScroll.setBounds(20, 100, 750, 300);
            panel.add(tableScroll);

            // Load residents into table
            loadResidents(null);

            // deleteUserButton button
            deleteUserButton = new JButton("Delete User");
            deleteUserButton.setBounds(20, 410, 200, 30);
            deleteUserButton.addActionListener(e -> deleteUser());
            panel.add(deleteUserButton);
            
            // updatePasswordButton
            updatePasswordButton = new JButton("Update User");
            updatePasswordButton.setBounds(240, 410, 200, 30);
            updatePasswordButton.addActionListener(e -> updateUser());
            panel.add(updatePasswordButton);


            // Back button
            backButton = new JButton("Back");
            backButton.setBounds(650, 460, 100, 30);
            backButton.addActionListener(e -> {
                frame.dispose();
                AHMFPS.third.x.setVisible(true);
            });
            panel.add(backButton);

            frame.add(panel);
            frame.setVisible(true);

            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    AHMFPS.third.x.setVisible(true);
                }
            });
        }
    
    private void deleteUser() {
        int selectedRow = residentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to delete.");
            return;
        }

        String username = residentTable.getValueAt(selectedRow, 0).toString();
        Users user = DataIO.checkUserName(username);
        
        if (user == null) {
            JOptionPane.showMessageDialog(frame, "User not found.");
            return;
        }

        if (username.equals(AHMFPS.loginUser.getName())){
            JOptionPane.showMessageDialog(frame, "You cannot delete your own account!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
                
        int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete " + username + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Find the room assigned to the resident
            String roomNumber = getRoomNumber(username);

            if (!"No Room".equals(roomNumber)) {
                // Resident has an assigned room, now we update the room status and remove the resident's name
                updateRoomAssignment(roomNumber);
            }
            DataIO.allUsers.remove(user);
            DataIO.write();
            JOptionPane.showMessageDialog(frame, "User deleted successfully.");
            loadResidents(null);
        }
    }
    
    private void updateUser() {
        int selectedRow = residentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to update email.");
            return;
        }

        String username = residentTable.getValueAt(selectedRow, 0).toString();
        Users user = DataIO.checkUserName(username);

        if (user == null) {
            JOptionPane.showMessageDialog(frame, "User not found.");
            return;
        }
        
        if (!username.equals(AHMFPS.loginUser.getName()) && user.getUserType().equals("Manager")) {
            JOptionPane.showMessageDialog(frame, "You cant update other manager's email address!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String newEmail = JOptionPane.showInputDialog(frame, "Enter new email for " + username + ":");
        if (newEmail == null || newEmail.trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Email cannot be empty.");
            return;
        }
        
        if (!isValidEmail(newEmail)) {
            JOptionPane.showMessageDialog(frame, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (isEmailRepeated(newEmail)) {
            JOptionPane.showMessageDialog(frame, "Email already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setEmail(newEmail);
        DataIO.write();
        JOptionPane.showMessageDialog(frame, "Email updated successfully.");
        loadResidents(null);
    }
    
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9-]+\\.com$";
        return email.matches(emailRegex);
    }
    
    private boolean isEmailRepeated(String email) {
        return DataIO.allUsers.stream()
              .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private void loadResidents(String searchQuery) {
        DefaultTableModel model = (DefaultTableModel) residentTable.getModel();
        model.setRowCount(0); // Clear existing rows

        // Load Residents
        for (Users user : DataIO.allUsers) {
            if ("Resident".equals(user.getUserType()) && "Approved".equals(user.getStatus())) {
                if (searchQuery != null && !searchQuery.isEmpty() && !user.getName().contains(searchQuery)) {
                    continue;
                }
                String roomAssigned = hasRoomAssigned(user.getName()) ? getRoomNumber(user.getName()) : "No Room";
                model.addRow(new Object[]{user.getName(), user.getUserType(), user.getEmail(), user.getPassword(), roomAssigned});
            }
        }

        // Load Staff & Manager
        for (Users user : DataIO.allUsers) {
            if ("Staff".equals(user.getUserType()) && "Approved".equals(user.getStatus())) {
                if (searchQuery != null && !searchQuery.isEmpty() && !user.getName().contains(searchQuery)) {
                    continue;
                }
                model.addRow(new Object[]{user.getName(), user.getUserType(), user.getEmail(), user.getPassword(), "N/A"}); // Staff/Manager has no room
            }
        }
                
        for (Users user : DataIO.allUsers) {
            if ("Manager".equals(user.getUserType()) && "Approved".equals(user.getStatus())) {
                if (searchQuery != null && !searchQuery.isEmpty() && !user.getName().contains(searchQuery)) {
                    continue;
                }
                model.addRow(new Object[]{user.getName(), user.getUserType(), user.getEmail(), user.getPassword(), "N/A"}); // Staff/Manager has no room
            }
        }

        // Make the table non-editable
        residentTable.setDefaultEditor(Object.class, null);
    }

        // Method to get room number assigned to a resident
        private String getRoomNumber(String username) {
            return DataIO.allRooms.stream()
                    .filter(room -> room.getResident() != null && room.getResident().equals(username))
                    .map(Rooms::getRoomNumber)
                    .findFirst()
                    .orElse("No Room");
        }

            // check if a user has a room assigned
            private boolean hasRoomAssigned(String username) {
                return DataIO.allRooms.stream()
                      .anyMatch(room -> room.getResident()!= null && room.getResident().equals(username));
            }

        // Method to update the room assignment in rooms.txt
        private void updateRoomAssignment(String roomNumber) {
            for (Rooms room : DataIO.allRooms) {
                if (room.getName().equals(roomNumber)) {
                    // Clear the resident's name and set the status to "Vacant"
                    room.setStatus("Vacant");
                    room.setResident(null);
                    // Save the updated rooms back to the file
                    Rooms.updateRoom(room);
                    break;
                }
            }

    }
}
