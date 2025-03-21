package ahmfps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ManageRoomsPage {
    JFrame frame;
    JTextField roomNumberField;
    JButton addButton, editButton, backButton;
    JTable roomsTable;

    public ManageRoomsPage() {
        frame = new JFrame("Manage Rooms");
        frame.setLayout(new BorderLayout());
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Panel for adding rooms
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 2));

        JLabel roomNumberLabel = new JLabel("Room Number:");
        roomNumberField = new JTextField();
        panel.add(roomNumberLabel);
        panel.add(roomNumberField);

        frame.add(panel, BorderLayout.NORTH);

        // Buttons
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Add Room");
        editButton = new JButton("Edit Room");
        backButton = new JButton("Back");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(backButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Table for displaying rooms
        roomsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Button Actions
        addButton.addActionListener(e -> addRoom());
        editButton.addActionListener(e -> editSelectedRoom());
        backButton.addActionListener(e -> {
            frame.setVisible(false);
            AHMFPS.third.x.setVisible(true);
        });

        loadRoomData();    
        frame.setVisible(true);
        
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                AHMFPS.third.x.setVisible(true);
            }
        });
    }

    private void loadRoomData() {
        String[] columnNames = {"Room Number", "Room Status", "Resident"};

        ArrayList<Rooms> rooms = Rooms.getAllRooms();
        Object[][] data = new Object[rooms.size()][3];

        for (int i = 0; i < rooms.size(); i++) {
            Rooms room = rooms.get(i);
            data[i][0] = room.getName();
            data[i][1] = room.getStatus();
            String resident = room.getResident();
            data[i][2] = (resident == null || resident.trim().isEmpty()) ? "No Resident" : resident;
        }

        roomsTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames) {
        public boolean isCellEditable(int row, int column) {
            return false; // Make the table non-editable
        }
    });
}

    private void addRoom() {
        try{
            String roomNumber = roomNumberField.getText().trim();
            String roomStatus = "Vacant";
            
            // Validate that the room name is an integer
            int roomName = Integer.parseInt(roomNumber);
            
            if(roomName <=0){
                JOptionPane.showMessageDialog(frame, "Room Number cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (roomNumber.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Room Number cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (isRoomNumberExists(roomNumber)) {
                JOptionPane.showMessageDialog(frame, "Room Number already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Rooms newRoom = new Rooms(roomNumber, roomStatus, null);
            if (!Rooms.addRoom(newRoom)) {
                JOptionPane.showMessageDialog(frame, "Failed to add room!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            loadRoomData();
            clearFields();
            JOptionPane.showMessageDialog(frame, "Room added successfully!");
        }catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid room name! Please enter a numeric value.");
        }
        
    }

    private boolean isRoomNumberExists(String roomNumber) {
        return Rooms.getAllRooms().stream().anyMatch(room -> room.getName().equals(roomNumber));
    }

    private void editSelectedRoom() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a room to edit!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String roomNumber = roomsTable.getValueAt(selectedRow, 0).toString();
        Rooms roomToEdit = getRoomByNumber(roomNumber);
        if (roomToEdit != null) {
            openEditRoomDialog(roomToEdit);
        }
    }

    private void openEditRoomDialog(Rooms roomToEdit) {
        JDialog editDialog = new JDialog(frame, "Edit Room", true);
        editDialog.setSize(400, 300);
        editDialog.setLayout(new BorderLayout());

        JPanel editPanel = new JPanel(new GridLayout(2, 2));
        JLabel roomNumberLabel = new JLabel("Room Number:");
        JTextField roomNumberField = new JTextField(roomToEdit.getName());
        roomNumberField.setEditable(false);

        JLabel residentLabel = new JLabel("Resident:");
        JComboBox<String> residentCombo = new JComboBox<>();
        loadResidentCombo(residentCombo);
        residentCombo.setSelectedItem(roomToEdit.getResident());

        editPanel.add(roomNumberLabel);
        editPanel.add(roomNumberField);
        editPanel.add(residentLabel);
        editPanel.add(residentCombo);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            String selectedResident = (String) residentCombo.getSelectedItem();

            // Validate resident assignment
            if (!"Vacant".equals(selectedResident) && isResidentAssigned(selectedResident, roomToEdit)) {
                JOptionPane.showMessageDialog(editDialog, "This resident is already assigned to another room!", "Error", JOptionPane.ERROR_MESSAGE);
                return;}

            if ("Vacant".equals(selectedResident)) {
                roomToEdit.setResident(null);
                roomToEdit.setStatus("Vacant");
            } else {
                roomToEdit.setResident(selectedResident);
                roomToEdit.setStatus("Occupied");
            }

            Rooms.updateRoom(roomToEdit);
            loadRoomData();
            JOptionPane.showMessageDialog(editDialog, "Room updated successfully!");
            editDialog.dispose();
        });

        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(editDialog, "Are you sure you want to delete this room?",
                    "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                Rooms.removeRoom(roomToEdit);
                loadRoomData();
                JOptionPane.showMessageDialog(editDialog, "Room deleted successfully!");
                editDialog.dispose();
            }
        });
        cancelButton.addActionListener(e -> editDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        editDialog.add(editPanel, BorderLayout.CENTER);
        editDialog.add(buttonPanel, BorderLayout.SOUTH);
        editDialog.setLocationRelativeTo(frame);
        editDialog.setVisible(true);
    }

    private void loadResidentCombo(JComboBox<String> residentCombo) {
        residentCombo.removeAllItems();
        residentCombo.addItem("Vacant");
        DataIO.allUsers.stream()
                .filter(user -> "Approved".equals(user.getStatus()) && "resident".equalsIgnoreCase(user.getUserType()))
                .filter(user -> !isResidentAssigned(user.getName(), null))
                .forEach(user -> residentCombo.addItem(user.getName()));
    }

    private boolean isResidentAssigned(String resident, Rooms currentRoom) {
        return Rooms.getAllRooms().stream()
                .anyMatch(room -> (currentRoom == null || !room.equals(currentRoom)) && resident.equals(room.getResident()));
    }

    private Rooms getRoomByNumber(String roomNumber) {
        return Rooms.getAllRooms().stream()
                .filter(room -> room.getName().equals(roomNumber))
                .findFirst()
                .orElse(null);
    }

    private void clearFields() {
        roomNumberField.setText("");
    }
}

