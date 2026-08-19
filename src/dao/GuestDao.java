package dao;

import entity.Guest;
import adt.DoublyLinkedList;

public class GuestDao {

    private static DoublyLinkedList<Guest> guests = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Guest Data In Memory
    //====================================================
    public static void createGuestData() {
        
        guests = new DoublyLinkedList<>();

        //================================================
        // Guest Records Matching Booking Database
        // Booking ID and Guest ID are DIFFERENT:
        // B0001 = Booking ID
        // R0001 = Guest ID
        //================================================

        //================================================
        // Allocated Guests
        //================================================
        guests.add(new Guest("R0001", "John Tan", "0123456789", "Elite", "Big Room", "Allocated", "20/08/2026", "20/08/2026 12:00"));
        guests.add(new Guest("R0002", "Wong Lee", "0134567890", "Diamond", "Medium Room", "Allocated", "21/08/2026", "21/08/2026 12:00"));
        guests.add(new Guest("R0003", "Alice Lim", "0145678901", "Platinum", "Small Room", "Allocated", "25/08/2026", "25/08/2026 12:00"));
        guests.add(new Guest("R0004", "David Wong", "0166789012", "Elite", "Big Room", "Allocated", "28/08/2026", "28/08/2026 12:00"));
        guests.add(new Guest("R0005", "Jason Lee", "0177890123", "Standard", "Medium Room", "Allocated", "01/09/2026", "01/09/2026 12:00"));
        guests.add(new Guest("R0011", "Daniel Lim", "0112233445", "Diamond", "Big Room", "Allocated", "03/08/2026", "03/08/2026 10:15"));
        guests.add(new Guest("R0012", "Michelle Tan", "0113344556", "Platinum", "Medium Room", "Allocated", "07/08/2026", "07/08/2026 11:30"));
        guests.add(new Guest("R0013", "Andrew Lee", "0114455667", "Elite", "Big Room", "Allocated", "12/08/2026", "12/08/2026 09:45"));
        guests.add(new Guest("R0014", "Samantha Wong", "0115566778", "Standard", "Small Room", "Allocated", "18/08/2026", "18/08/2026 14:00"));
        guests.add(new Guest("R0015", "Brian Ng", "0116677889", "Diamond", "Medium Room", "Allocated", "05/09/2026", "05/09/2026 13:30"));
        //================================================
        // Waiting Guests
        //================================================
        guests.add(new Guest("R0006", "Sarah Tan", "0188901234", "Diamond", "Small Room", "Waiting", "10/07/2026", "10/07/2026 12:00"));
        guests.add(new Guest("R0007", "Michael Chen", "0199012345", "Platinum", "Medium Room", "Waiting", "13/07/2026", "13/07/2026 12:00"));
        guests.add(new Guest("R0008", "Emily Wong", "0121122334", "Elite", "Big Room", "Waiting", "16/07/2026", "16/07/2026 12:00"));
        guests.add(new Guest("R0009", "Kevin Lim", "0132233445", "Standard", "Small Room", "Waiting", "19/07/2026", "19/07/2026 12:00"));
        guests.add(new Guest("R0010", "Jessica Ng", "0143344556", "Diamond", "Medium Room", "Waiting", "22/07/2026", "22/07/2026 12:00"));
        guests.add(new Guest("R0016", "Rachel Lee", "0117788990", "Platinum", "Small Room", "Waiting", "20/08/2026", "20/08/2026 15:15"));
        guests.add(new Guest("R0017", "Steven Wong", "0118899001", "Elite", "Big Room", "Waiting", "22/08/2026", "22/08/2026 08:30"));
        guests.add(new Guest("R0018", "Nicole Tan", "0119900112", "Standard", "Medium Room", "Waiting", "24/08/2026", "24/08/2026 10:45"));
        guests.add(new Guest("R0019", "Aaron Lim", "0121011223", "Diamond", "Big Room", "Waiting", "27/08/2026", "27/08/2026 13:00"));
        guests.add(new Guest("R0020", "Chloe Lee", "0132122334", "Platinum", "Small Room", "Waiting", "08/09/2026", "08/09/2026 09:20"));

        System.out.println(guests.getSize() + " Guests Created In Memory!");
    }

    //====================================================
    // Store Guest Data In Memory
    //====================================================
    public void saveToFile(DoublyLinkedList<Guest> guestList) {

        guests = new DoublyLinkedList<>();

        if (guestList == null) {
            return;
        }

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
    // Search Guest By Guest ID
    // Example: R0001
    //====================================================
    public Guest searchGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null || guest.getGuestID() == null) {
                continue;
            }

            if (guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return guest;
            }
        }

        return null;
    }

    //====================================================
    // Remove Guest By Guest ID
    //====================================================
    public Guest removeGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null || guest.getGuestID() == null) {
                continue;
            }

            if (guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
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

        if (guests == null) {
            return 0;
        }

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
    // Example:
    // R0001 ... R0010 -> Next = R0011
    //====================================================
    public String generateGuestID() {

        int maxID = 0;

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null) {
                continue;
            }

            String guestID = guest.getGuestID();

            if (guestID == null || !guestID.matches("R\\d{4}")) {
                continue;
            }

            try {

                int number = Integer.parseInt(guestID.substring(1));

                if (number > maxID) {
                    maxID = number;
                }

            } catch (NumberFormatException exception) {

                // Ignore invalid Guest ID
            }
        }

        return String.format("R%04d", maxID + 1);
    }

    //====================================================
    // Find Existing Guest By Name And Phone Number
    //====================================================
    public Guest searchGuestByNameAndPhone(String guestName, String phoneNumber) {

        if (guestName == null || guestName.trim().isEmpty()) {
            return null;
        }

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null) {
                continue;
            }

            if (guest.getGuestName() == null || guest.getPhoneNumber() == null) {
                continue;
            }

            boolean sameName = guest.getGuestName().equalsIgnoreCase(guestName.trim());
            boolean samePhone = guest.getPhoneNumber().equals(phoneNumber.trim());

            if (sameName && samePhone) {
                return guest;
            }
        }

        return null;
    }

    //====================================================
    // Get Existing Guest Or Create New Guest
    // Used By Booking Module
    //====================================================
    public Guest getOrCreateGuest(String guestName, String phoneNumber, String roomType, String checkInDate) {

        //================================================
        // Check Existing Guest
        //================================================
        Guest existingGuest = searchGuestByNameAndPhone(guestName, phoneNumber);

        if (existingGuest != null) {
            return existingGuest;
        }

        //================================================
        // Generate New Guest ID
        // Booking ID remains BXXXX
        // Guest ID remains RXXXX
        //================================================
        String guestID = generateGuestID();

        // New guest starts as Standard loyalty tier
        String loyaltyTier = "Standard";

        //================================================
        // Convert Booking Room Type To Guest Room Type
        //
        // Booking     Guest
        // Single   -> Small Room
        // Medium   -> Medium Room
        // Large    -> Big Room
        //================================================
        String guestRoomType;

        if (roomType == null || roomType.trim().isEmpty()) {

            guestRoomType = "Small Room";

        } else if (roomType.equalsIgnoreCase("Single")) {

            guestRoomType = "Small Room";

        } else if (roomType.equalsIgnoreCase("Medium")) {

            guestRoomType = "Medium Room";

        } else if (roomType.equalsIgnoreCase("Large")) {

            guestRoomType = "Big Room";

        } else {

            guestRoomType = "Small Room";
        }

        //================================================
        // Convert Booking Date:
        // 20-08-2026 -> 20/08/2026
        //================================================
        String guestCheckInDate = convertBookingDateToGuestDate(checkInDate);

        // Default arrival time
        String arrivalDateTime = guestCheckInDate + " 12:00";

        //================================================
        // Create New Guest
        //================================================
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

    //====================================================
    // Convert Booking Date To Guest Date
    //
    // BookingDatabase:
    // DD-MM-YYYY
    //
    // GuestDao:
    // DD/MM/YYYY
    //====================================================
    private String convertBookingDateToGuestDate(String bookingDate) {

        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            return "";
        }

        return bookingDate.trim().replace("-", "/");
    }
}