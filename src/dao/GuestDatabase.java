package dao;

import entity.Guest;
import adt.DoublyLinkedList;

public class GuestDatabase {

    private static DoublyLinkedList<Guest> guests = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Guest Data In Memory
    //====================================================
    public static void createGuestData() {

        guests = new DoublyLinkedList<>();

        guests.add(new Guest("R0001", "John Tan", "0123456789", "Elite", "Large Room", "Waiting", "20/07/2026", "20/07/2026 10:30"));
        guests.add(new Guest("R0002", "Wong Lee", "0134567890", "Diamond", "Medium Room", "Waiting", "21/07/2026", "21/07/2026 08:45"));
        guests.add(new Guest("R0003", "Alice Lim", "0145678901", "Platinum", "Single Room", "Waiting", "22/07/2026", "22/07/2026 09:15"));
        guests.add(new Guest("R0004", "David Wong", "0166789012", "Elite", "Large Room", "Waiting", "23/07/2026", "23/07/2026 09:00"));
        guests.add(new Guest("R0005", "Jason Lee", "0177890123", "Standard", "Single Room", "Waiting", "24/07/2026", "24/07/2026 07:30"));
        guests.add(new Guest("R0006", "Sarah Tan", "0188901234", "Diamond", "Medium Room", "Waiting", "25/07/2026", "25/07/2026 10:00"));
        guests.add(new Guest("R0007", "Michael Chen", "0199012345", "Platinum", "Medium Room", "Waiting", "26/07/2026", "26/07/2026 08:30"));
        guests.add(new Guest("R0008", "Emily Wong", "0121122334", "Elite", "Large Room", "Waiting", "27/07/2026", "27/07/2026 11:00"));
        guests.add(new Guest("R0009", "Kevin Lim", "0132233445", "Standard", "Single Room", "Waiting", "28/07/2026", "28/07/2026 09:30"));
        guests.add(new Guest("R0010", "Jessica Ng", "0143344556", "Diamond", "Medium Room", "Waiting", "29/07/2026", "29/07/2026 08:15"));

        GuestDatabase guestDatabase = new GuestDatabase();
        guestDatabase.saveToFile(guests);
        
        System.out.println(guests.getSize() + " Guests Created In Memory!");
    }

    //====================================================
    // Store Guest Data In Memory
    //====================================================
    public void saveToFile(DoublyLinkedList<Guest> guestList) {

        if (guestList == null) {
            guests = new DoublyLinkedList<>();
            return;
        }

        guests = new DoublyLinkedList<>();

        for (int i = 1; i <= guestList.getSize(); i++) {

            Guest guest = guestList.getEntry(i);

            if (guest != null) {
                guests.add(guest);
            }
        }

        System.out.println("Guest Database Updated In Memory!");
    }

    //====================================================
    // Retrieve Guest Data From Memory
    //====================================================
    public DoublyLinkedList<Guest> retrieveFromFile() {

        if (guests == null || guests.isEmpty()) {
            createGuestData();
        }

        return guests;
    }

    //====================================================
    // Add Guest
    //====================================================
    public boolean addGuest(Guest guest) {

        if (guest == null) {
            return false;
        }

        if (guest.getGuestID() == null || guest.getGuestID().trim().isEmpty()) {
            return false;
        }

        if (searchGuestByID(guest.getGuestID()) != null) {
            return false;
        }

        guests.add(guest);

        return true;
    }

    //====================================================
    // Search Guest By ID
    //====================================================
    public Guest searchGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return guest;
            }
        }

        return null;
    }

    //====================================================
    // Remove Guest By ID
    //====================================================
    public Guest removeGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return guests.remove(i);
            }
        }

        return null;
    }

    //====================================================
    // Get Guest By Position
    //====================================================
    public Guest getGuest(int position) {

        if (position < 1 || position > guests.getSize()) {
            return null;
        }

        return guests.getEntry(position);
    }

    //====================================================
    // Get Total Guests
    //====================================================
    public int getTotalGuests() {
        return guests.getSize();
    }

    //====================================================
    // Check Guest Data Is Empty
    //====================================================
    public boolean isGuestDataEmpty() {
        return guests == null || guests.isEmpty();
    }

    //====================================================
    // Get All Guests
    //====================================================
    public DoublyLinkedList<Guest> getAllGuests() {
        return guests;
    }
    
    //====================================================
    // Generate Next Guest ID
    //====================================================
    public String generateGuestID() {
        int maxID = 0;
        for (int i = 1; i <= guests.getSize(); i++) {
            Guest guest = guests.getEntry(i);
            if (guest != null) {
                String id = guest.getGuestID();
                if (id != null && id.startsWith("R")) {
                    try {
                        int number = Integer.parseInt(id.substring(1));
                        if (number > maxID) {
                            maxID = number;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid Guest ID
                    }
                }
            }
        }
        int nextID = maxID + 1;
        return String.format("R%04d", nextID);
    }
    
    //====================================================
    // Find Existing Guest By Name And Phone Number
    //====================================================
    public Guest searchGuestByNameAndPhone(
            String guestName,
            String phoneNumber) {
        if (guestName == null || phoneNumber == null) {
            return null;
        }
        for (int i = 1; i <= guests.getSize(); i++) {
            Guest guest = guests.getEntry(i);
            if (guest != null
                    && guest.getGuestName() != null
                    && guest.getPhoneNumber() != null
                    && guest.getGuestName().equalsIgnoreCase(guestName.trim())
                    && guest.getPhoneNumber().equals(phoneNumber.trim())) {
                return guest;
            }
        }
        return null;
    }

    //====================================================
    // Get Existing Guest Or Create New Guest
    //====================================================
    public Guest getOrCreateGuest(
            String guestName,
            String phoneNumber,
            String roomType,
            String checkInDate) {

        // Check whether guest already exists
        Guest existingGuest = searchGuestByNameAndPhone(guestName, phoneNumber);
        // Existing guest found
        if (existingGuest != null) {
            return existingGuest;
        }
        // Generate new Guest ID
        String guestID = generateGuestID();
        // Booking module default new guest as Standard
        String loyaltyTier = "Standard";
        // Convert Booking room type to Guest room type
        String guestRoomType;

        if (roomType.equalsIgnoreCase("Single")) {
            guestRoomType = "Small Room";
        }
        else if (roomType.equalsIgnoreCase("Medium")) {
            guestRoomType = "Medium Room";
        }
        else {
            guestRoomType = "Big Room";
        }

        // GuestDatabase uses dd/MM/yyyy
        String guestCheckInDate = checkInDate.replace("-", "/");

        String arrivalDateTime = guestCheckInDate + " 12:00";

        Guest newGuest = new Guest(
                guestID,
                guestName,
                phoneNumber,
                loyaltyTier,
                guestRoomType,
                "Waiting",
                guestCheckInDate,
                arrivalDateTime
        );

        guests.add(newGuest);

        return newGuest;
    }
}