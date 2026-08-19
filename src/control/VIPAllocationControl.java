package control;

import adt.DoublyLinkedList;
import adt.ListInterface;
import dao.GuestDatabase;
import dao.RoomDao;
import entity.Guest;
import entity.LoyaltyRecord;
import entity.Room;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Iterator;

public class VIPAllocationControl {

    private ListInterface<Guest> vipQueue;

    private int nextGuestID;

    private GuestDatabase guestDatabase;
    private LoyaltyControl loyaltyControl;
    private RoomDao roomDao;

    private static final DateTimeFormatter ARRIVAL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter CHECK_IN_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    //==========================================================
    // Constructor
    //==========================================================
    public VIPAllocationControl() {

        vipQueue = new DoublyLinkedList<>();

        guestDatabase = new GuestDatabase();

        loyaltyControl = new LoyaltyControl();

        roomDao = new RoomDao();

        nextGuestID = 1;

        loadGuestDatabase();
    }

    //==========================================================
    // Generate Next Guest ID
    //==========================================================
    public String generateGuestID() {
        return String.format("R%04d", nextGuestID);
    }

    //==========================================================
    // Enqueue
    //==========================================================
    private void enqueue(ListInterface<Guest> queue, Guest guest) {

        if (queue != null && guest != null) {
            queue.add(guest);
        }
    }

    //==========================================================
    // Dequeue
    //==========================================================
    private Guest dequeue() {

        if (vipQueue == null || vipQueue.isEmpty()) {
            return null;
        }

        return vipQueue.remove(1);
    }

    //==========================================================
    // Get Front
    //==========================================================
    private Guest getFront() {

        if (vipQueue == null || vipQueue.isEmpty()) {
            return null;
        }

        return vipQueue.getEntry(1);
    }

    //==========================================================
    // 1. Add Guest To Allocation Queue
    //==========================================================
    public boolean addGuestToQueue(Guest guest, int lifetimePoints) {

        if (guest == null) {
            return false;
        }

        if (guest.getGuestID() == null || guest.getGuestID().trim().isEmpty()) {
            return false;
        }

        if (searchGuestByIDWithoutRefresh(guest.getGuestID()) != null) {
            return false;
        }

        //======================================================
        // Add Guest Into Guest Database
        //======================================================
        if (guestDatabase.searchGuestByID(guest.getGuestID()) == null) {
            guestDatabase.addGuest(guest);
        }

        //======================================================
        // OTHER MODULE FUNCTION
        // Module: Loyalty & Rewards Module
        //
        // Function Used:
        // LoyaltyControl.createLoyaltyMember()
        //
        // Purpose:
        // Create LoyaltyRecord using selected Lifetime Points
        //
        // Elite      = 6000
        // Diamond    = 4000
        // Platinum   = 2000
        // Standard   = 0
        //======================================================
        boolean loyaltyCreated = loyaltyControl.createLoyaltyMember(guest, lifetimePoints);

        if (!loyaltyCreated) {
            return false;
        }

        //======================================================
        // Read Correct Tier Back From Loyalty Module
        //======================================================
        syncGuestTierFromLoyaltyModule(guest);

        //======================================================
        // Insert Guest According To Priority
        //======================================================
        insertPriority(guest);

        updateNextGuestID(guestDatabase.retrieveFromFile());

        return true;
    }

    //==========================================================
    // Synchronize One Guest With Loyalty Module
    //==========================================================
    private boolean syncGuestTierFromLoyaltyModule(Guest guest) {

        if (guest == null || guest.getGuestID() == null) {
            return false;
        }

        if (loyaltyControl == null) {
            loyaltyControl = new LoyaltyControl();
        }

        //======================================================
        // OTHER MODULE FUNCTION
        // Module: Loyalty & Rewards Module
        // Function Used: LoyaltyControl.searchGuest()
        //======================================================
        LoyaltyRecord loyaltyRecord = loyaltyControl.searchGuest(guest.getGuestID());

        if (loyaltyRecord == null || loyaltyRecord.getLoyaltyTier() == null) {
            return false;
        }

        String oldTier = guest.getLoyaltyTier();

        String newTier = loyaltyRecord.getLoyaltyTier();

        if (oldTier == null || !oldTier.equalsIgnoreCase(newTier)) {

            guest.setLoyaltyTier(newTier);

            return true;
        }

        return false;
    }

    //==========================================================
    // Refresh Priority Queue Using Loyalty Module
    //==========================================================
    public int refreshPriorityQueueFromLoyaltyModule() {

        if (vipQueue == null || vipQueue.isEmpty()) {
            return 0;
        }

        ListInterface<Guest> currentGuests = new DoublyLinkedList<>();

        for (int i = 1; i <= vipQueue.getSize(); i++) {

            Guest guest = vipQueue.getEntry(i);

            if (guest != null) {
                currentGuests.add(guest);
            }
        }

        vipQueue = new DoublyLinkedList<>();

        int totalTierChanged = 0;

        for (int i = 1; i <= currentGuests.getSize(); i++) {

            Guest guest = currentGuests.getEntry(i);

            if (guest == null) {
                continue;
            }

            boolean changed = syncGuestTierFromLoyaltyModule(guest);

            if (changed) {
                totalTierChanged++;
            }

            insertPriority(guest);
        }

        return totalTierChanged;
    }

    //==========================================================
    // Get Loyalty Record From Loyalty Module
    //==========================================================
    public LoyaltyRecord getLoyaltyRecordFromLoyaltyModule(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        if (loyaltyControl == null) {
            loyaltyControl = new LoyaltyControl();
        }

        //======================================================
        // OTHER MODULE FUNCTION
        // LoyaltyControl.searchGuest()
        //======================================================
        return loyaltyControl.searchGuest(guestID.trim());
    }

    //==========================================================
    // Get Lifetime Points From Loyalty Module
    //==========================================================
    public int getLifetimePointsFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null) {
            return -1;
        }

        return record.getLifetimePoints();
    }

    //==========================================================
    // Get Loyalty Tier From Loyalty Module
    //==========================================================
    public String getLoyaltyTierFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null || record.getLoyaltyTier() == null) {
            return null;
        }

        return record.getLoyaltyTier();
    }

    //==========================================================
    // Insert Guest According To Priority
    //==========================================================
    private void insertPriority(Guest guest) {

        if (guest == null) {
            return;
        }

        ListInterface<Guest> temporaryQueue = new DoublyLinkedList<>();

        boolean inserted = false;

        Iterator<Guest> iterator = vipQueue.getIterator();

        while (iterator.hasNext()) {

            Guest currentGuest = iterator.next();

            if (!inserted && shouldInsertBefore(guest, currentGuest)) {

                enqueue(temporaryQueue, guest);

                inserted = true;
            }

            enqueue(temporaryQueue, currentGuest);
        }

        if (!inserted) {
            enqueue(temporaryQueue, guest);
        }

        vipQueue = temporaryQueue;
    }

    //==========================================================
    // Compare Priority
    // Same Priority -> Earlier Arrival First
    //==========================================================
    private boolean shouldInsertBefore(Guest newGuest, Guest currentGuest) {

        if (newGuest == null || currentGuest == null) {
            return false;
        }

        if (newGuest.getPriority() > currentGuest.getPriority()) {
            return true;
        }

        if (newGuest.getPriority() < currentGuest.getPriority()) {
            return false;
        }

        LocalDateTime newArrival = parseArrivalDateTime(newGuest.getArrivalDateTime());

        LocalDateTime currentArrival = parseArrivalDateTime(currentGuest.getArrivalDateTime());

        if (newArrival == null || currentArrival == null) {
            return false;
        }

        return newArrival.isBefore(currentArrival);
    }

    //==========================================================
    // Parse Arrival Date Time
    //==========================================================
    private LocalDateTime parseArrivalDateTime(String arrivalDateTime) {

        if (arrivalDateTime == null || arrivalDateTime.trim().isEmpty()) {
            return null;
        }

        try {

            return LocalDateTime.parse(arrivalDateTime.trim(), ARRIVAL_FORMATTER);

        } catch (DateTimeParseException exception) {

            return null;
        }
    }

    //==========================================================
    // 2. Allocate Room
    //==========================================================
    public Guest allocateRoom() {

        refreshPriorityQueueFromLoyaltyModule();

        Guest guest = dequeue();

        if (guest == null) {
            return null;
        }

        guest.setRoomStatus("Allocated");

        return guest;
    }

    //==========================================================
    // 3. View Next Priority Guest
    //==========================================================
    public Guest getNextPriorityGuest() {

        refreshPriorityQueueFromLoyaltyModule();

        return getFront();
    }

    //==========================================================
    // 4. Display Allocation Queue
    //==========================================================
    public ListInterface<Guest> getAllGuestAllocations() {

        refreshPriorityQueueFromLoyaltyModule();

        ListInterface<Guest> guestList = new DoublyLinkedList<>();

        if (vipQueue == null) {
            return guestList;
        }

        for (int i = 1; i <= vipQueue.getSize(); i++) {

            Guest guest = vipQueue.getEntry(i);

            if (guest != null) {
                guestList.add(guest);
            }
        }

        return guestList;
    }

    //==========================================================
    // 5. Search Guest Allocation
    //==========================================================
    public ListInterface<Guest> searchGuest(String keyword) {

        refreshPriorityQueueFromLoyaltyModule();

        ListInterface<Guest> matchingGuests = new DoublyLinkedList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingGuests;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        for (int i = 1; i <= vipQueue.getSize(); i++) {

            Guest guest = vipQueue.getEntry(i);

            if (guest == null) {
                continue;
            }

            int points = getLifetimePointsFromLoyaltyModule(guest.getGuestID());

            String searchData = String.valueOf(guest.getGuestID()) + " "
                    + String.valueOf(guest.getGuestName()) + " "
                    + String.valueOf(guest.getPhoneNumber()) + " "
                    + String.valueOf(guest.getLoyaltyTier()) + " "
                    + guest.getPriority() + " "
                    + points + " "
                    + String.valueOf(guest.getRoomType()) + " "
                    + String.valueOf(guest.getRoomStatus()) + " "
                    + String.valueOf(guest.getCheckInDate()) + " "
                    + String.valueOf(guest.getArrivalDateTime());

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

        refreshPriorityQueueFromLoyaltyModule();

        return searchGuestByIDWithoutRefresh(guestID);
    }

    //==========================================================
    // Search Guest By ID Without Refresh
    //==========================================================
    private Guest searchGuestByIDWithoutRefresh(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= vipQueue.getSize(); i++) {

            Guest guest = vipQueue.getEntry(i);

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return guest;
            }
        }

        return null;
    }

    //==========================================================
    // 6. Available Rooms
    //==========================================================
    public DoublyLinkedList<Room> getAvailableRoomsFromRoomModule() {

        DoublyLinkedList<Room> availableRooms = new DoublyLinkedList<>();

        if (roomDao == null) {
            roomDao = new RoomDao();
        }

        //======================================================
        // OTHER MODULE FUNCTION
        // Housekeeping / Room Module
        // RoomDao.retrieveFromFile()
        //======================================================
        DoublyLinkedList<Room> allRooms = roomDao.retrieveFromFile();

        if (allRooms == null || allRooms.isEmpty()) {
            return availableRooms;
        }

        for (int i = 1; i <= allRooms.getSize(); i++) {

            Room room = allRooms.getEntry(i);

            if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
                continue;
            }

            int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();

            if (currentStatus == RoomStatusUtil.Ready_For_CheckIN) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    //==========================================================
    // Get Total Hotel Rooms
    //==========================================================
    public int getTotalRoomsFromRoomModule() {

        if (roomDao == null) {
            roomDao = new RoomDao();
        }

        DoublyLinkedList<Room> allRooms = roomDao.retrieveFromFile();

        if (allRooms == null) {
            return 0;
        }

        return allRooms.getSize();
    }

    //==========================================================
    // Get Room Type Name
    //==========================================================
    public String getRoomTypeName(Room room) {

        if (room == null) {
            return "";
        }

        return RoomTypeUtil.roomTypeName(room.getRoomType());
    }

    //==========================================================
    // Get Room Status Name
    //==========================================================
    public String getRoomStatusName(Room room) {

        if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
            return "";
        }

        int statusCode = room.getStatusHistory().getCurrentData().getStatusCode();

        return RoomStatusUtil.statusName(statusCode);
    }

    //==========================================================
    // 7. Remove Guest Allocation
    //==========================================================
    public Guest removeGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= vipQueue.getSize(); i++) {

            Guest guest = vipQueue.getEntry(i);

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                return vipQueue.remove(i);
            }
        }

        return null;
    }

    //==========================================================
    // Check Guest Report Period
    //==========================================================
    private boolean isGuestInReportPeriod(Guest guest, int year, int month) {

        if (guest == null || guest.getCheckInDate() == null || guest.getCheckInDate().trim().isEmpty()) {
            return false;
        }

        try {

            LocalDate checkInDate = LocalDate.parse(guest.getCheckInDate().trim(), CHECK_IN_FORMATTER);

            return checkInDate.getYear() == year && checkInDate.getMonthValue() == month;

        } catch (DateTimeParseException exception) {

            return false;
        }
    }

    //==========================================================
    // 8. Loyalty Tier Summary Report
    //==========================================================
    public int[] getLoyaltyTierCountsFromLoyaltyModule(int year, int month) {

        int[] counts = new int[4];

        DoublyLinkedList<Guest> allGuests = guestDatabase.retrieveFromFile();

        if (allGuests == null || allGuests.isEmpty()) {
            return counts;
        }

        for (int i = 1; i <= allGuests.getSize(); i++) {

            Guest guest = allGuests.getEntry(i);

            if (guest == null || !isGuestInReportPeriod(guest, year, month)) {
                continue;
            }

            LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guest.getGuestID());

            if (record == null || record.getLoyaltyTier() == null) {
                continue;
            }

            String tier = record.getLoyaltyTier();

            if (tier.equalsIgnoreCase("Elite")) {

                counts[0]++;

            } else if (tier.equalsIgnoreCase("Diamond")) {

                counts[1]++;

            } else if (tier.equalsIgnoreCase("Platinum")) {

                counts[2]++;

            } else if (tier.equalsIgnoreCase("Standard")) {

                counts[3]++;
            }
        }

        return counts;
    }

    //==========================================================
    // Total Loyalty Members
    //==========================================================
    public int getTotalLoyaltyMembersFromLoyaltyModule(int year, int month) {

        int[] counts = getLoyaltyTierCountsFromLoyaltyModule(year, month);

        return counts[0] + counts[1] + counts[2] + counts[3];
    }

    //==========================================================
    // 9. Room Type Report
    //==========================================================
    public int[] getRoomTypeCounts(int year, int month) {

        int[] counts = new int[3];

        DoublyLinkedList<Guest> allGuests = guestDatabase.retrieveFromFile();

        if (allGuests == null || allGuests.isEmpty()) {
            return counts;
        }

        for (int i = 1; i <= allGuests.getSize(); i++) {

            Guest guest = allGuests.getEntry(i);

            if (guest == null || guest.getRoomType() == null) {
                continue;
            }

            if (!isGuestInReportPeriod(guest, year, month)) {
                continue;
            }

            String roomType = guest.getRoomType();

            if (roomType.equalsIgnoreCase("Small Room")) {

                counts[0]++;

            } else if (roomType.equalsIgnoreCase("Medium Room") || roomType.equalsIgnoreCase("Middle Room")) {

                counts[1]++;

            } else if (roomType.equalsIgnoreCase("Big Room")) {

                counts[2]++;
            }
        }

        return counts;
    }

    //==========================================================
    // Total Guests
    //==========================================================
    public int getTotalGuests(int year, int month) {

        int[] counts = getRoomTypeCounts(year, month);

        return counts[0] + counts[1] + counts[2];
    }

    //==========================================================
    // Load Guest Database
    //==========================================================
    public int loadGuestDatabase() {

        DoublyLinkedList<Guest> guests = guestDatabase.retrieveFromFile();

        vipQueue = new DoublyLinkedList<>();

        if (guests == null || guests.isEmpty()) {

            nextGuestID = 1;

            return 0;
        }

        int totalLoaded = 0;

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null) {
                continue;
            }

            // Get latest Loyalty Tier
            syncGuestTierFromLoyaltyModule(guest);

            if (guest.getRoomStatus() != null && guest.getRoomStatus().equalsIgnoreCase("Waiting")) {

                insertPriority(guest);

                totalLoaded++;
            }
        }

        updateNextGuestID(guests);

        return totalLoaded;
    }

    //==========================================================
    // Update Next Guest ID
    //==========================================================
    private void updateNextGuestID(ListInterface<Guest> guests) {

        int highestID = 0;

        if (guests == null) {

            nextGuestID = 1;

            return;
        }

        for (int i = 1; i <= guests.getSize(); i++) {

            Guest guest = guests.getEntry(i);

            if (guest == null || guest.getGuestID() == null) {
                continue;
            }

            String guestID = guest.getGuestID();

            if (!guestID.matches("R\\d{4}")) {
                continue;
            }

            try {

                int number = Integer.parseInt(guestID.substring(1));

                if (number > highestID) {
                    highestID = number;
                }

            } catch (NumberFormatException exception) {

                // Ignore invalid ID
            }
        }

        nextGuestID = highestID + 1;
    }

    //==========================================================
    // Queue Empty
    //==========================================================
    public boolean isQueueEmpty() {
        return vipQueue == null || vipQueue.isEmpty();
    }

    //==========================================================
    // Total Waiting Guests
    //==========================================================
    public int getTotalGuests() {

        if (vipQueue == null) {
            return 0;
        }

        return vipQueue.getSize();
    }
}