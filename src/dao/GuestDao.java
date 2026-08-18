package dao;

import Entity.Guest;
import Adt.DoublyLinkedList;

public class GuestDao {

    private static DoublyLinkedList<Guest> guests =
            new DoublyLinkedList<>();

    //====================================================
    // Create Initial Guest Data In Memory
    //====================================================
    public static void createGuestData() {

        guests = new DoublyLinkedList<>();

        guests.add(new Guest(
                "R0001",
                "John Tan",
                "0123456789",
                "Elite",
                "Big Room",
                "Waiting",
                "20/07/2026",
                "20/07/2026 10:30"
        ));

        guests.add(new Guest(
                "R0002",
                "Wong Lee",
                "0134567890",
                "Diamond",
                "Medium Room",
                "Waiting",
                "21/07/2026",
                "21/07/2026 08:45"
        ));

        guests.add(new Guest(
                "R0003",
                "Alice Lim",
                "0145678901",
                "Platinum",
                "Small Room",
                "Waiting",
                "22/07/2026",
                "22/07/2026 09:15"
        ));

        guests.add(new Guest(
                "R0004",
                "David Wong",
                "0166789012",
                "Elite",
                "Big Room",
                "Waiting",
                "23/07/2026",
                "23/07/2026 09:00"
        ));

        guests.add(new Guest(
                "R0005",
                "Jason Lee",
                "0177890123",
                "Standard",
                "Small Room",
                "Waiting",
                "24/07/2026",
                "24/07/2026 07:30"
        ));

        guests.add(new Guest(
                "R0006",
                "Sarah Tan",
                "0134567890",
                "Diamond",
                "Medium Room",
                "Waiting",
                "25/07/2026",
                "25/07/2026 10:00"
        ));

        guests.add(new Guest(
                "R0007",
                "Michael Chen",
                "0199012345",
                "Platinum",
                "Medium Room",
                "Waiting",
                "26/07/2026",
                "26/07/2026 08:30"
        ));

        guests.add(new Guest(
                "R0008",
                "Emily Wong",
                "0121122334",
                "Elite",
                "Big Room",
                "Waiting",
                "27/07/2026",
                "27/07/2026 11:00"
        ));

        guests.add(new Guest(
                "R0009",
                "Kevin Lim",
                "0132233445",
                "Standard",
                "Small Room",
                "Waiting",
                "28/07/2026",
                "28/07/2026 09:30"
        ));

        guests.add(new Guest(
                "R0010",
                "Jessica Ng",
                "0143344556",
                "Diamond",
                "Medium Room",
                "Waiting",
                "29/07/2026",
                "29/07/2026 08:15"
        ));

        GuestDao guestDao = new GuestDao();
        guestDao.saveToFile(guests);

        System.out.println(
                guests.getSize() + " Guests Created In Memory!"
        );
    }

    //====================================================
    // Store Guest Data In Memory
    //====================================================
    public void saveToFile(
            DoublyLinkedList<Guest> guestList) {

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

        System.out.println(
                "Guest Database Updated In Memory!"
        );
    }

    //====================================================
    // Retrieve Guest Data From Memory
    //====================================================
    public DoublyLinkedList.ArrayList<Guest>
            retrieveFromFile() {

        if (guests == null || guests.isEmpty()) {
            createGuestData();
        }

        DoublyLinkedList.ArrayList<Guest> result =
                new DoublyLinkedList.ArrayList<>();

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null) {
                result.add(guest);
            }
        }

        return result;
    }

    //====================================================
    // Add Guest
    //====================================================
    public boolean addGuest(Guest guest) {

        if (guest == null) {
            return false;
        }

        if (guest.getGuestID() == null
                || guest.getGuestID().trim().isEmpty()) {
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

        if (guestID == null
                || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null
                    && guest.getGuestID() != null
                    && guest.getGuestID()
                            .equalsIgnoreCase(guestID.trim())) {

                return guest;
            }
        }

        return null;
    }

    //====================================================
    // Remove Guest By ID
    //====================================================
    public Guest removeGuestByID(String guestID) {

        if (guestID == null
                || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null
                    && guest.getGuestID() != null
                    && guest.getGuestID()
                            .equalsIgnoreCase(guestID.trim())) {

                return guests.remove(i);
            }
        }

        return null;
    }

    //====================================================
    // Generate Guest ID
    //====================================================
    public String generateGuestID() {

        int highestID = 0;

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null
                    || guest.getGuestID() == null) {
                continue;
            }

            String guestID = guest.getGuestID();

            if (guestID.matches("R\\d{4}")) {

                int id = Integer.parseInt(
                        guestID.substring(1)
                );

                if (id > highestID) {
                    highestID = id;
                }
            }
        }

        return String.format(
                "R%04d",
                highestID + 1
        );
    }

    //====================================================
    // Search Guest By Name And Phone
    //====================================================
    public Guest searchGuestByNameAndPhone(
            String guestName,
            String phoneNumber) {

        if (guestName == null
                || phoneNumber == null) {
            return null;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest != null
                    && guest.getGuestName()
                            .equalsIgnoreCase(
                                    guestName.trim())
                    && guest.getPhoneNumber()
                            .equals(phoneNumber.trim())) {

                return guest;
            }
        }

        return null;
    }
}