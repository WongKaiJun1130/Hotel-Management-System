package System_Control;

import System_Entity.Guest;
import System_adt.DoublyLinkedList;
import System_adt.ListInterface;
import dao.GuestDatabase;
import java.util.Iterator;

public class VIPAllocationControl {

    private DoublyLinkedList.ArrayQueue<Guest> vipQueue;
    private int nextGuestID;
    

    //==========================================================
    // Constructor
    //==========================================================
    public VIPAllocationControl() {
        vipQueue = new DoublyLinkedList.ArrayQueue<>();
        nextGuestID = 1;
        loadGuestDatabase();
    }

    //==========================================================
    // Generate Guest ID
    //==========================================================
    public String generateGuestID() {
        return String.format("R%04d", nextGuestID);
    }

    //==========================================================
    // Add Guest To Allocation Queue
    //==========================================================
    public boolean addGuestToQueue(Guest guest) {

        if (guest == null) {
            return false;
        }

        if (searchGuestByID(guest.getGuestID()) != null) {
            return false;
        }

        insertPriority(guest);
        nextGuestID++;

        return true;
    }

    //==========================================================
    // Insert Guest Based On Priority
    //==========================================================
    private void insertPriority(Guest guest) {

        DoublyLinkedList.ArrayQueue<Guest> temporaryQueue = new DoublyLinkedList.ArrayQueue<>();
        boolean inserted = false;
        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {

            Guest currentGuest = iterator.next();

            if (!inserted && guest.getPriority() > currentGuest.getPriority()) {
                temporaryQueue.enqueue(guest);
                inserted = true;
            }

            temporaryQueue.enqueue(currentGuest);
        }

        if (!inserted) {
            temporaryQueue.enqueue(guest);
        }

        vipQueue = temporaryQueue;
    }

    //==========================================================
    // Allocate Room
    //==========================================================
    public Guest allocateRoom() {

        if (vipQueue.isEmpty()) {
            return null;
        }

        Guest guest = vipQueue.dequeue();
        guest.setRoomStatus("Allocated");

        return guest;
    }

    //==========================================================
    // Get Next Priority Guest
    //==========================================================
    public Guest getNextPriorityGuest() {

        if (vipQueue.isEmpty()) {
            return null;
        }

        return vipQueue.getFront();
    }

    //==========================================================
    // Get All Guest Allocations
    //==========================================================
    public ListInterface<Guest> getAllGuestAllocations() {

        ListInterface<Guest> guestList = new DoublyLinkedList<>();
        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {
            guestList.add(iterator.next());
        }

        return guestList;
    }

    //==========================================================
    // Search Guest Allocation
    //==========================================================
    public ListInterface<Guest> searchGuest(String keyword) {

        ListInterface<Guest> matchingGuests = new DoublyLinkedList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingGuests;
        }

        String searchKeyword = keyword.trim().toLowerCase();
        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {

            Guest guest = iterator.next();

            String searchData = guest.getGuestID() + " "
                    + guest.getGuestName() + " "
                    + guest.getPhoneNumber() + " "
                    + guest.getLoyaltyTier() + " "
                    + guest.getPriority() + " "
                    + guest.getRoomType() + " "
                    + guest.getRoomStatus() + " "
                    + guest.getCheckInDate();

            if (searchData.toLowerCase().contains(searchKeyword)) {
                matchingGuests.add(guest);
            }
        }

        return matchingGuests;
    }

    //==========================================================
    // Search Guest By ID
    //==========================================================
    public Guest searchGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {

            Guest guest = iterator.next();

            if (guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return guest;
            }
        }

        return null;
    }

    //==========================================================
    // Get Available Rooms
    //==========================================================
    public String[][] getAvailableRooms() {

        return new String[][]{
            {"Small Room", "Available"},
            {"Medium Room", "Available"},
            {"Big Room", "Available"}
        };
    }

    //==========================================================
    // Load Guest Database
    //==========================================================
    public int loadGuestDatabase() {

        GuestDatabase guestDatabase = new GuestDatabase();
        DoublyLinkedList.ArrayList<Guest> guests = guestDatabase.retrieveFromFile();

        vipQueue = new DoublyLinkedList.ArrayQueue<>();

        for (int i = 1; i <= guests.getNumberOfEntries(); i++) {
            Guest guest = guests.getEntry(i);
            insertPriority(guest);
        }

        updateNextGuestID(guests);

        return guests.getNumberOfEntries();
    }

    //==========================================================
    // Update Next Guest ID
    //==========================================================
    private void updateNextGuestID(DoublyLinkedList.ArrayList<Guest> guests) {

        int highestID = 0;

        for (int i = 1; i <= guests.getNumberOfEntries(); i++) {

            Guest guest = guests.getEntry(i);
            String guestID = guest.getGuestID();

            if (guestID == null || !guestID.matches("R\\d{4}")) {
                continue;
            }

            int numericID = Integer.parseInt(guestID.substring(1));

            if (numericID > highestID) {
                highestID = numericID;
            }
        }

        nextGuestID = highestID + 1;
    }

    //==========================================================
    // Check Whether Queue Is Empty
    //==========================================================
    public boolean isQueueEmpty() {
        return vipQueue.isEmpty();
    }

    //==========================================================
    // Get Total Guests
    //==========================================================
    public int getTotalGuests() {

        int total = 0;
        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {
            iterator.next();
            total++;
        }

        return total;
    }
}