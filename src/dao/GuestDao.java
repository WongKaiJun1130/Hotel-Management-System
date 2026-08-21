package dao;
/**
 *
 * @author Wong Kai Jun
 */

import entity.Guest;
import adt.DoublyLinkedList;

public class GuestDao {

    private static DoublyLinkedList<Guest> guests = new DoublyLinkedList<>();

    public static void createGuestData() {
        
        guests = new DoublyLinkedList<>();

        guests.add(new Guest("R0001", "John Tan", "0123456789", "Elite", "Large Room", "Allocated", "20/08/2026", "20/08/2026 12:00"));
        guests.add(new Guest("R0002", "Wong Lee", "0134567890", "Diamond", "Medium Room", "Allocated", "21/08/2026", "21/08/2026 12:00"));
        guests.add(new Guest("R0003", "Alice Lim", "0145678901", "Platinum", "Single Room", "Allocated", "25/08/2026", "25/08/2026 12:00"));
        guests.add(new Guest("R0004", "David Wong", "0166789012", "Elite", "Large Room", "Allocated", "28/08/2026", "28/08/2026 12:00"));
        guests.add(new Guest("R0005", "Jason Lee", "0177890123", "Standard", "Medium Room", "Allocated", "01/09/2026", "01/09/2026 12:00"));
        guests.add(new Guest("R0011", "Daniel Lim", "0112233445", "Diamond", "Large Room", "Allocated", "03/08/2026", "03/08/2026 10:15"));
        guests.add(new Guest("R0012", "Michelle Tan", "0113344556", "Platinum", "Medium Room", "Allocated", "07/08/2026", "07/08/2026 11:30"));
        guests.add(new Guest("R0013", "Andrew Lee", "0114455667", "Elite", "Large Room", "Allocated", "12/08/2026", "12/08/2026 09:45"));
        guests.add(new Guest("R0014", "Samantha Wong", "0115566778", "Standard", "Single Room", "Allocated", "18/08/2026", "18/08/2026 14:00"));
        guests.add(new Guest("R0015", "Brian Ng", "0116677889", "Diamond", "Medium Room", "Allocated", "05/09/2026", "05/09/2026 13:30"));
        
        
        guests.add(new Guest("R0006", "Sarah Tan", "0188901234", "Diamond", "Single Room", "Waiting", "10/07/2026", "10/07/2026 12:00"));
        guests.add(new Guest("R0007", "Michael Chen", "0199012345", "Platinum", "Medium Room", "Waiting", "13/07/2026", "13/07/2026 12:00"));
        guests.add(new Guest("R0008", "Emily Wong", "0121122334", "Elite", "Large Room", "Waiting", "16/07/2026", "16/07/2026 12:00"));
        guests.add(new Guest("R0009", "Kevin Lim", "0132233445", "Standard", "Single Room", "Waiting", "19/07/2026", "19/07/2026 12:00"));
        guests.add(new Guest("R0010", "Jessica Ng", "0143344556", "Diamond", "Medium Room", "Waiting", "22/07/2026", "22/07/2026 12:00"));
        guests.add(new Guest("R0016", "Rachel Lee", "0117788990", "Platinum", "Single Room", "Waiting", "20/08/2026", "20/08/2026 15:15"));
        guests.add(new Guest("R0017", "Steven Wong", "0118899001", "Elite", "Large Room", "Waiting", "22/08/2026", "22/08/2026 08:30"));
        guests.add(new Guest("R0018", "Nicole Tan", "0119900112", "Standard", "Medium Room", "Waiting", "24/08/2026", "24/08/2026 10:45"));
        guests.add(new Guest("R0019", "Aaron Lim", "0121011223", "Diamond", "Large Room", "Waiting", "27/08/2026", "27/08/2026 13:00"));
        guests.add(new Guest("R0020", "Chloe Lee", "0132122334", "Platinum", "Single Room", "Waiting", "08/09/2026", "08/09/2026 09:20"));

        guests.add(new Guest(
                "R0011",
                "Daniel Lim",
                "0112233445",
                "Diamond",
                "Large Room",
                "Waiting",
                "03/08/2026",
                "03/08/2026 10:15"
        ));

        guests.add(new Guest(
                "R0012",
                "Michelle Tan",
                "0113344556",
                "Platinum",
                "Medium Room",
                "Waiting",
                "07/08/2026",
                "07/08/2026 11:30"
        ));

        guests.add(new Guest(
                "R0013",
                "Andrew Lee",
                "0114455667",
                "Elite",
                "Large Room",
                "Waiting",
                "12/08/2026",
                "12/08/2026 09:45"
        ));

        guests.add(new Guest(
                "R0014",
                "Samantha Wong",
                "0115566778",
                "Standard",
                "Single Room",
                "Waiting",
                "18/08/2026",
                "18/08/2026 14:00"
        ));

        guests.add(new Guest(
                "R0015",
                "Brian Ng",
                "0116677889",
                "Diamond",
                "Medium Room",
                "Waiting",
                "05/09/2026",
                "05/09/2026 13:30"
        ));

        guests.add(new Guest(
                "R0016",
                "Rachel Lee",
                "0117788990",
                "Platinum",
                "Single Room",
                "Waiting",
                "20/08/2026",
                "20/08/2026 15:15"
        ));

        guests.add(new Guest(
                "R0017",
                "Steven Wong",
                "0118899001",
                "Elite",
                "Large Room",
                "Waiting",
                "22/08/2026",
                "22/08/2026 08:30"
        ));

        guests.add(new Guest(
                "R0018",
                "Nicole Tan",
                "0119900112",
                "Standard",
                "Medium Room",
                "Waiting",
                "24/08/2026",
                "24/08/2026 10:45"
        ));

        guests.add(new Guest(
                "R0019",
                "Aaron Lim",
                "0121011223",
                "Diamond",
                "Large Room",
                "Waiting",
                "27/08/2026",
                "27/08/2026 13:00"
        ));

        guests.add(new Guest(
                "R0020",
                "Chloe Lee",
                "0132122334",
                "Platinum",
                "Single Room",
                "Waiting",
                "08/09/2026",
                "08/09/2026 09:20"
        ));


        System.out.println(
                guests.getSize()
                + " Guests Created In Memory!"
        );
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

    
    public DoublyLinkedList<Guest> retrieveFromFile() {

        if (guests == null || guests.isEmpty()) {
            createGuestData();
        }

        return guests;
    }

    
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

    
    public Guest getGuest(int position) {

        if (position < 1 || position > guests.getSize()) {
            return null;
        }

        return guests.getEntry(position);
    }

    
    public int getTotalGuests() {

        if (guests == null) {
            return 0;
        }

        return guests.getSize();
    }

    
    public boolean isGuestDataEmpty() {
        return guests == null || guests.isEmpty();
    }

    
    public DoublyLinkedList<Guest> getAllGuests() {
        return guests;
    }

    
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

    
    public Guest getOrCreateGuest(String guestName, String phoneNumber, String roomType, String checkInDate) {

        
        Guest existingGuest = searchGuestByNameAndPhone(guestName, phoneNumber);

        if (existingGuest != null) {
            return existingGuest;
        }

        
        String guestID = generateGuestID();

        // New guest starts as Standard loyalty tier
        String loyaltyTier = "Standard";

        String guestRoomType;
        if (roomType == null || roomType.trim().isEmpty()) {
            guestRoomType = "Single Room";

        } else if (roomType.equalsIgnoreCase("Single")) {
            guestRoomType = "Single Room";

        } else if (roomType.equalsIgnoreCase("Medium")) {
            guestRoomType = "Medium Room";

        } else if (roomType.equalsIgnoreCase("Large")) {
            guestRoomType = "Large Room";

        } else {
            guestRoomType = "Single Room";
        }

        
        String guestCheckInDate = convertBookingDateToGuestDate(checkInDate);

        // Default arrival time
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

    private String convertBookingDateToGuestDate(String bookingDate) {

        if (bookingDate == null || bookingDate.trim().isEmpty()) {
            return "";
        }

        return bookingDate.trim().replace("-", "/");
    }
}
