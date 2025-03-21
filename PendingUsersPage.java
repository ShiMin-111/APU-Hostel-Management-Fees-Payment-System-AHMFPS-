package ahmfps;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PendingUsersPage implements ActionListener {

    JFrame frame;
    JTable userTable;
    JButton approveButton, denyButton, backButton;
    DefaultTableModel tableModel;
    Map<String, String> tempRoomAssignments;

    public PendingUsersPage() {
        frame = new JFrame("Pending User Approvals");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Username", "User Type", "Room"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        loadTempRoomAssignments();
        loadPendingUsers();

        JPanel buttonPanel = new JPanel();

        approveButton = new JButton("Approve");
        approveButton.addActionListener(this);
        buttonPanel.add(approveButton);

        denyButton = new JButton("Deny");
        denyButton.addActionListener(this);
        buttonPanel.add(denyButton);

        backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            frame.setVisible(false);
            AHMFPS.third.x.setVisible(true);
        });
        buttonPanel.add(backButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        frame.add(panel);
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.third.x.setVisible(true);
            }
        });
    }

    //Load temporary room assignments from temp_rooms.txt into a map.
    private void loadTempRoomAssignments() {
        tempRoomAssignments = new HashMap<>();
        File tempFile = new File("temp_rooms.txt");

        if (!tempFile.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    tempRoomAssignments.put(parts[0], parts[1]); // username -> room
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error reading temp_rooms.txt: " + e.getMessage());
        }
    }

    private void loadPendingUsers() {
        List<Users> pendingUsers = DataIO.allUsers;
        tableModel.setRowCount(0);

        for (Users user : pendingUsers) {
            if ("Pending".equals(user.getStatus())) {
                String room = tempRoomAssignments.getOrDefault(user.getName(), "N/A");
                tableModel.addRow(new Object[] {
                        user.getName(),
                        user.getUserType(),
                        room
                });
            }
        }
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedRow = userTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a user to proceed.");
            return;
        }

        String username = (String) tableModel.getValueAt(selectedRow, 0);
        Users selectedUser = DataIO.checkUserName(username);

        if (selectedUser == null) {
            JOptionPane.showMessageDialog(frame, "User not found!");
            return;
        }

        if (e.getSource() == approveButton) {
            approveUser(selectedUser);
        } else if (e.getSource() == denyButton) {
            denyUser(selectedUser);
        }
    }

    //Approve the selected user and update room assignments.
    private void approveUser(Users selectedUser) {
    // If the user is a staff member, no room assignment is required
    if (selectedUser.getUserType().equals("Staff")|| selectedUser.getUserType().equals("Manager")) {
        // Directly approve the staff without checking room assignment
        selectedUser.setStatus("Approved");
        DataIO.write();  // Save the updated user data
        loadPendingUsers();  // Reload the table
        JOptionPane.showMessageDialog(frame, selectedUser.getUserType()+" user approved successfully!");
        return;
    }

    // If the user is not staff, proceed with room assignments
    String room = tempRoomAssignments.get(selectedUser.getName());

    if (room == null || room.isEmpty()) {
        JOptionPane.showMessageDialog(frame, "No room assigned to the user!");
        return;
    }

    Rooms selectedRoom = Rooms.getAllRooms().stream()
            .filter(r -> r.getName().equals(room))
            .findFirst()
            .orElse(null);

    if (selectedRoom == null) {
        JOptionPane.showMessageDialog(frame, "Room not found!");
        return;
    }

    // Check if the room already has a resident
    if (selectedRoom.getResident() != null) {
        JOptionPane.showMessageDialog(frame, "Room is already occupied!");
        return;
    }

    // Assign the user to the room
    selectedRoom.setResident(selectedUser.getName());

    selectedUser.setStatus("Approved");

    // Remove the user from temp_rooms.txt
    updateTempRoomFile(selectedUser.getName());

    // Update the room data in the global list
    Rooms.updateRoom(selectedRoom);

    // Update the system data
    DataIO.write();
    loadTempRoomAssignments();
    loadPendingUsers();

    JOptionPane.showMessageDialog(frame, "User approved and room updated successfully!");
    
    // Send email notification
    String toEmail = selectedUser.getEmail();
            
    String emailBody = "Dear " + selectedUser.getName() + ",\n\nYour registration has been approved.";
            
    // Add room information if available
    if (!room.isEmpty()) {
        emailBody += "\n\nYour assigned room is: " + room;
    }
            
    emailBody += "\n\nBest regards,\nAPU Hostel Management Office";
    EmailSender.sendEmail(toEmail, "Registration Approved", emailBody);
    
}



    //Deny the selected user and remove their room assignment.
    private void denyUser(Users selectedUser) {
        DataIO.allUsers.remove(selectedUser);

        // Remove the user from temp_rooms.txt
        updateTempRoomFile(selectedUser.getName());

        DataIO.write();
        loadTempRoomAssignments();
        loadPendingUsers();
        JOptionPane.showMessageDialog(frame, "User denied successfully!");
        
        // Send email notification
        String emailBody = "Dear " + selectedUser.getName() + ",\n\nWe regret to inform you that your registration has been denied." +
                "\n\nBest regards,\nAPU Hostel Management Office";
        EmailSender.sendEmail(selectedUser.getEmail(), "Registration Denied", emailBody);
    }

    //Update the temp_rooms.txt file to remove a user's room assignment.
    private void updateTempRoomFile(String username) {
        File tempFile = new File("temp_rooms.txt");
        if (!tempFile.exists()) return;

        try {
            List<String> lines = Files.readAllLines(tempFile.toPath());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                for (String line : lines) {
                    if (!line.startsWith(username + ",")) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error updating temp_rooms.txt: " + e.getMessage());
        }
    }
}
