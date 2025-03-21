package ahmfps;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Rooms {
    private static ArrayList<RoomUpdateListener> listeners = new ArrayList<>();

    private String name;
    private String status; // Status: "Vacant" or "Occupied"
    private String resident; 
    
    // Constructor to initialize the room with a resident
    public Rooms(String name, String status, String resident) {
        this.name = name;
        this.status = status;
        this.resident = resident != null &&!resident.isEmpty()? resident: null; // Set to null if resident is empty
    }

    // Add a room update listener
    public static void addRoomUpdateListener(RoomUpdateListener listener) {
        listeners.add(listener);
    }

    // Notify all listeners of a room update
    private static void notifyRoomUpdate() {
        for (RoomUpdateListener listener : listeners) {
            listener.onRoomUpdate();
        }
    }

    // Save all rooms to a file
    public static void saveRooms() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("rooms.txt", false))) {
            for (Rooms room : DataIO.allRooms) {
                writer.println(room.getName());         // Write room name
                writer.println(room.getStatus());       // Write room status

                writer.println(room.getResident() != null ? room.getResident() : ""); 

                writer.println(); 
            }
        } catch (IOException e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }


    // Get all rooms
    public static ArrayList<Rooms> getAllRooms() {
        return DataIO.allRooms;
    }

    // Add a new room
    public static boolean addRoom(Rooms room) {
        boolean added = DataIO.allRooms.add(room);
        if (added) {
            saveRooms();
            notifyRoomUpdate();
        }
        return added;
    }

    // Update a room's status and resident
    public static void updateRoom(Rooms room) {
        int index = DataIO.allRooms.indexOf(room);
        if (index >= 0) {
            DataIO.allRooms.set(index, room);
            saveRooms(); // Save changes
            notifyRoomUpdate(); // Notify listeners
        }
    }

    public static boolean removeRoom(Rooms room) {
        boolean removed = DataIO.allRooms.remove(room);
        if (removed) {
            saveRooms(); // Save changes
            notifyRoomUpdate(); // Notify listeners
        }
        return removed;
    }


    // Getters and setters for room properties
    public String getName() {
        return name;
    }

    public String getResident() {
        return this.resident; // Return the name of the current resident
    }

    public String getStatus() {
        return status;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResident(String resident) {
        this.resident = resident != null && !resident.equals("N/A") ? resident : null; // Only set resident if it's valid
        updateRoomStatus(); // Update status based on the resident
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Update room status based on whether the room has a resident
    private void updateRoomStatus() {
        // Set status to "Vacant" if no resident is in the room
        this.status = (this.resident == null) ? "Vacant" : "Occupied";
    }

    // Static method to get all vacant rooms
    public static List<Rooms> getVacantRooms() {
    List<Rooms> vacantRooms = new ArrayList<>();
    for (Rooms room : getAllRooms()) { // This calls the existing getAllRooms method
        if ("Vacant".equals(room.getStatus())) {  // Check if the room is vacant
            vacantRooms.add(room);
        }
    }
    return vacantRooms;
}

    public String getRoomNumber() {
        return this.name; // or whatever property represents the room number
    }

    // Interface for room update listeners
    public interface RoomUpdateListener {
        void onRoomUpdate();
    }
}

