package ahmfps;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class DataIO {
    public static ArrayList<Users> allUsers = new ArrayList<>();
    public static ArrayList<Rooms> allRooms = new ArrayList<>();
    public static ArrayList<ManagementFee> allFee = new ArrayList<>();
    public static double managementFee = 0.00;

    // Write all data to files
    public static void write() {
        try {
            writeUsers();
            writeManagementFees();
            writeRooms();
            writeTempRooms();
        } catch (Exception e) {
            System.out.println("Error in write operation: " + e.getMessage());
        }
    }

    private static void writeUsers() throws IOException {
        try (PrintWriter writer = new PrintWriter("user.txt")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            for (Users user : allUsers) {
                writer.println(user.getName());
                writer.println(user.getEmail());
                writer.println(user.getPassword());
                writer.println(user.getStatus());
                writer.println(user.getUserType());
                writer.println(user.getPaymentStatus());

                for (PaymentData payment : user.getPaymentHistory()) {
                    writer.println(sdf.format(payment.getDate()));
                    writer.println(payment.getAmount());
                    writer.println(payment.getDurationInMonths());
                }
                writer.println();
            }
        }
    }

    private static void writeManagementFees() throws IOException {
        try (PrintWriter writer = new PrintWriter("managementFee.txt")) {
            for (ManagementFee fee : allFee) {
                writer.println(fee.getManagementFee());
                writer.println(fee.getChangeDate());
                writer.println();
            }
        }
    }

    public static void writeRooms() throws IOException {
    try (PrintWriter writer = new PrintWriter(new FileWriter("rooms.txt", false))) {  
        for (Rooms room : allRooms) {
            writer.println(room.getName());         // Write room name
            writer.println(room.getStatus());       // Write room status
            
            // Write resident only if it exists
            String resident = room.getResident();
            if (resident!= null) {
                writer.println(resident); 
            } else {
                writer.println(); // Write an empty line if no resident
            }


            writer.println(); // Add a blank line between rooms for separation
        }
    }
}


    public static void writeTempRooms() throws IOException {
        try (PrintWriter writer = new PrintWriter("temp_rooms.txt")) {
            for (Users user : allUsers) {
                if ("Pending".equals(user.getStatus()) && user.getAssignedRoom() != null) {
                    writer.println(user.getName() + "," + user.getAssignedRoom());
                }
            }
        }
    }

    // Read all data from files
    public static void read() {
        try {
            allUsers.clear();
            allRooms.clear();
            allFee.clear();
            
            checkAndCreateFile("user.txt");
            checkAndCreateFile("managementFee.txt");
            checkAndCreateFile("rooms.txt");
            checkAndCreateFile("temp_rooms.txt");

            readUsers();
            readManagementFees();
            readRooms();
            readTempRooms();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in read operation: " + e.getMessage());
        }
    }
    
    private static void checkAndCreateFile(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile(); // Create an empty file
            
            if(fileName.equals("user.txt")){
                try (PrintWriter writer = new PrintWriter("user.txt")) {
                    // Ensure it's only added if the file is empty
                    if (file.length() == 0) {
                        Users manager = new Users();
                        manager.setName("shimin");
                        manager.setEmail("shimin0619@gmail.com");
                        manager.setPassword("shimin");
                        manager.setStatus("Approved");
                        manager.setUserType("Manager");
                        manager.setPaymentStatus("Paid");
                        
                        writer.println(manager.getName());
                        writer.println(manager.getEmail());
                        writer.println(manager.getPassword());
                        writer.println(manager.getStatus());
                        writer.println(manager.getUserType());
                        writer.println(manager.getPaymentStatus());
                        writer.println();
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "Error in adding manager data: " + e.getMessage());
                }
            }
        }
    }

    private static void readUsers() throws IOException {
        try (Scanner scanner = new Scanner(new File("user.txt"))) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            while (scanner.hasNextLine()) {
                String name = scanner.nextLine().trim();
                if (name.isEmpty()) continue;

                Users user = new Users();
                user.setName(name);
                user.setEmail(scanner.nextLine().trim());
                user.setPassword(scanner.nextLine().trim());
                user.setStatus(scanner.nextLine().trim());
                user.setUserType(scanner.nextLine().trim());
                user.setPaymentStatus(scanner.nextLine().trim());

                while (scanner.hasNextLine()) {
                    String dateString = scanner.nextLine().trim();
                    if (dateString.isEmpty()) break;

                    try {
                        Date paymentDate = sdf.parse(dateString);
                        double amount = Double.parseDouble(scanner.nextLine().trim());
                        int durationInMonths = Integer.parseInt(scanner.nextLine().trim()); 

                        user.addPayment(new PaymentData(paymentDate, amount, durationInMonths));
                    } catch (Exception e) {
                        System.out.println("Error parsing payment data: " + e.getMessage());
                    }
                }
                allUsers.add(user);
            }
        }
    }

    private static void readManagementFees() throws IOException {
        try (Scanner scanner = new Scanner(new File("managementFee.txt"))) {
            while (scanner.hasNextLine()) {
                String feeString = scanner.nextLine().trim();
                if (feeString.isEmpty()) continue;

                double feeAmount = Double.parseDouble(feeString);
                String changeDateString = scanner.nextLine().trim();
                LocalDate changeDate = LocalDate.parse(changeDateString);
                allFee.add(new ManagementFee(feeAmount, changeDate));
            }
        }
    }

    private static void readRooms() throws IOException {
        try (Scanner scanner = new Scanner(new File("rooms.txt"))) {
            while (scanner.hasNextLine()) {
                String roomName = scanner.nextLine().trim();
                if (roomName.isEmpty()) continue;

                String roomStatus = scanner.nextLine().trim();
                
                // Check if there is a resident before reading the next line
                String resident = null;
                if (scanner.hasNextLine()) {
                    resident = scanner.nextLine().trim();
                    if (resident.isEmpty()) { 
                        resident = null; // Set resident to null if the line is empty
                    }
                } 
                allRooms.add(new Rooms(roomName, roomStatus, resident));
                if (scanner.hasNextLine()) scanner.nextLine(); // Skip blank line
            }
        }
    }


    private static void readTempRooms() throws IOException {
        try (Scanner scanner = new Scanner(new File("temp_rooms.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split(",");
                if (parts.length == 2) {
                    Users user = checkUserName(parts[0]);
                    if (user != null) {
                        user.setAssignedRoom(parts[1]);
                    }
                }
            }
        }
    }

    public static void writelog() {
        try (FileWriter fw = new FileWriter("auditLog.txt", true)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String loginTimeFormatted = AHMFPS.loginTime.format(formatter);
            String logoutTimeFormatted = AHMFPS.logoutTime.format(formatter);
            Duration duration = Duration.between(AHMFPS.loginTime, LocalDateTime.now());

            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.toSeconds() % 60;

            fw.write(AHMFPS.loginUser.getName() + "\n");
            fw.write(AHMFPS.loginUser.getUserType() + "\n");
            fw.write(loginTimeFormatted + "\n");
            fw.write(logoutTimeFormatted + "\n");
            fw.write(String.format("%02d hours, %02d minutes, %02d seconds\n", hours, minutes, seconds));
            fw.write("\n");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error in writing audit log: " + e.getMessage());
        }
    }

    public static Users checkUserName(String username) {
        return allUsers.stream().filter(user -> user.getName().equals(username)).findFirst().orElse(null);
    }
}
