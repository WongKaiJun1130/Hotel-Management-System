package control;

/**
 *
 * @author Wong Kai Jun
 */

import adt.DoublyLinkedList;
import adt.ListInterface;
import adt.ListInterface.StackInterface;
import dao.BookingDao;
import dao.GuestDao;
import dao.RoomDao;
import entity.Booking;
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

/**
 * Module: VIP & Loyalty Tier Priority Room Allocation
 *
 * This control class manages VIP and loyalty-tier guest allocation
 * using Stack ADT. The highest-priority guest is stored at the
 * top of the stack according to loyalty tier and arrival time.
 *
 * The module also integrates with Guest, Booking, Loyalty and
 * Housekeeping / Room modules.
 *
 * @author Wong Kai Jun
 */
public class VIPAllocationControl {

    //==========================================================
    // Stack ADT
    // Highest Priority Guest Is At The Top
    //==========================================================
    private StackInterface<Guest> vipStack = new DoublyLinkedList<>();

    private int nextGuestID;

    //==========================================================
    // Other Classes / Modules
    //==========================================================
    private GuestDao guestDatabase;
    private BookingDao bookingDatabase;
    private LoyaltyControl loyaltyControl;
    private RoomDao roomDao;

    private static final DateTimeFormatter ARRIVAL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter CHECK_IN_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    //==========================================================
    // Constructor
    //==========================================================
    public VIPAllocationControl() {
        guestDatabase = new GuestDao();
        bookingDatabase = new BookingDao();
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
    // 1. Add Guest To VIP Stack
    //==========================================================
    public boolean addGuestToStack(Guest guest) {

        if (guest == null) {
            return false;
        }

        int lifetimePoints = getMinimumPointsForTier(guest.getLoyaltyTier());

        return addGuestToStack(guest, lifetimePoints);
    }

    //==========================================================
    // 1. Add Guest To VIP Stack
    // Guest + Loyalty Module
    //==========================================================
    public boolean addGuestToStack(Guest guest, int lifetimePoints) {

        if (guest == null) {
            return false;
        }

        if (guest.getGuestID() == null || guest.getGuestID().trim().isEmpty()) {
            return false;
        }

        //======================================================
        // Check VIP Stack Duplicate
        //======================================================
        if (searchGuestByIDWithoutRefresh(guest.getGuestID()) != null) {
            return false;
        }

        //======================================================
        // Check Guest Database Duplicate
        //======================================================
        Guest existingGuest = guestDatabase.searchGuestByID(guest.getGuestID());

        if (existingGuest != null) {
            return false;
        }

        //======================================================
        // Create Loyalty Member
        //======================================================
        boolean loyaltyCreated = loyaltyControl.createLoyaltyMember(guest, lifetimePoints);

        if (!loyaltyCreated) {
            return false;
        }

        //======================================================
        // Save Guest
        //======================================================
        guestDatabase.addGuest(guest);

        //======================================================
        // Get Latest Tier
        //======================================================
        syncGuestTierFromLoyaltyModule(guest);

        //======================================================
        // Add Into Stack
        //======================================================
        insertPriority(guest);

        updateNextGuestID(guestDatabase.retrieveFromFile());

        return true;
    }

    //==========================================================
    // Get Minimum Lifetime Points
    //==========================================================
    private int getMinimumPointsForTier(String loyaltyTier) {

        if (loyaltyTier == null) {
            return 0;
        }

        if (loyaltyTier.equalsIgnoreCase("Elite")) {
            return 6000;
        }

        if (loyaltyTier.equalsIgnoreCase("Diamond")) {
            return 4000;
        }

        if (loyaltyTier.equalsIgnoreCase("Platinum")) {
            return 2000;
        }

        return 0;
    }

    //==========================================================
    // Synchronize Guest Tier With Loyalty Module
    //==========================================================
    private boolean syncGuestTierFromLoyaltyModule(Guest guest) {

        if (guest == null || guest.getGuestID() == null) {
            return false;
        }

        if (loyaltyControl == null) {
            loyaltyControl = new LoyaltyControl();
        }

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
    // Booking Module Integration
    //
    // Get Waiting Booking
    // -> Get Guest ID
    // -> Get Full Guest Information
    // -> Add Waiting Guest Into VIP Stack
    //==========================================================
    public int syncWaitingBookingsIntoVipStack() {

        if (bookingDatabase == null) {
            bookingDatabase = new BookingDao();
        }

        ListInterface<Booking> waitingBookings = bookingDatabase.getWaitingBooking();

        if (waitingBookings == null || waitingBookings.isEmpty()) {
            return 0;
        }

        int totalAdded = 0;

        for (int i = 1; i <= waitingBookings.getSize(); i++) {

            Booking booking = waitingBookings.getEntry(i);

            if (booking == null || booking.getGuestID() == null || booking.getGuestID().trim().isEmpty()) {
                continue;
            }

            //==================================================
            // Get Full Guest Information From Guest Module
            //==================================================
            Guest guest = guestDatabase.searchGuestByID(booking.getGuestID().trim());

            if (guest == null) {
                continue;
            }

            //==================================================
            // Only Waiting Guest
            //==================================================
            if (guest.getRoomStatus() == null || !guest.getRoomStatus().equalsIgnoreCase("Waiting")) {
                continue;
            }

            //==================================================
            // Avoid Duplicate
            //==================================================
            if (searchGuestByIDWithoutRefresh(guest.getGuestID()) != null) {
                continue;
            }

            syncGuestTierFromLoyaltyModule(guest);

            insertPriority(guest);

            totalAdded++;
        }

        return totalAdded;
    }

    //==========================================================
    // Refresh Stack Using Latest Loyalty Tier
    //==========================================================
    public int refreshPriorityStackFromLoyaltyModule() {

        if (vipStack == null || vipStack.isEmpty()) {
            return 0;
        }

        StackInterface<Guest> oldStack = vipStack;

        vipStack = new DoublyLinkedList<>();

        int totalTierChanged = 0;

        while (!oldStack.isEmpty()) {

            Guest guest = oldStack.pop();

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
    // Loyalty Record
    //==========================================================
    public LoyaltyRecord getLoyaltyRecordFromLoyaltyModule(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        if (loyaltyControl == null) {
            loyaltyControl = new LoyaltyControl();
        }

        return loyaltyControl.searchGuest(guestID.trim());
    }

    //==========================================================
    // Lifetime Points
    //==========================================================
    public int getLifetimePointsFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null) {
            return -1;
        }

        return record.getLifetimePoints();
    }

    //==========================================================
    // Loyalty Tier
    //==========================================================
    public String getLoyaltyTierFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null || record.getLoyaltyTier() == null) {
            return null;
        }

        return record.getLoyaltyTier();
    }

    //==========================================================
    // Insert Guest Based On Priority Using Stack
    //
    // TOP
    // Elite
    // Diamond
    // Platinum
    // Standard
    // BOTTOM
    //
    // Same Priority -> Earlier Arrival First
    //==========================================================
    private void insertPriority(Guest guest) {

        if (guest == null) {
            return;
        }

        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        while (!vipStack.isEmpty() && !shouldInsertBefore(guest, vipStack.peek())) {
            temporaryStack.push(vipStack.pop());
        }

        vipStack.push(guest);

        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }
    }

    //==========================================================
    // Compare Guest Priority
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
    // Parse Arrival DateTime
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
    //
    // Stack pop()
    //==========================================================
    public Guest allocateRoom() {

        syncWaitingBookingsIntoVipStack();
        refreshPriorityStackFromLoyaltyModule();

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        Guest guest = vipStack.pop();

        if (guest == null) {
            return null;
        }

        guest.setRoomStatus("Allocated");

        return guest;
    }

    //==========================================================
    // 3. View Next Priority Guest
    //
    // Stack peek()
    //==========================================================
    public Guest getNextPriorityGuest() {

        syncWaitingBookingsIntoVipStack();
        refreshPriorityStackFromLoyaltyModule();

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        return vipStack.peek();
    }

    //==========================================================
    // 4. Display Allocation Stack
    //
    // Includes:
    // VIP Guest Database
    // +
    // Waiting Booking Guests
    //==========================================================
    public ListInterface<Guest> getAllGuestAllocations() {

        //======================================================
        // Get Latest Booking Guests
        //======================================================
        syncWaitingBookingsIntoVipStack();

        refreshPriorityStackFromLoyaltyModule();

        ListInterface<Guest> guestList = new DoublyLinkedList<>();

        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        if (vipStack == null || vipStack.isEmpty()) {
            return guestList;
        }

        //======================================================
        // Read Stack From Top To Bottom
        //======================================================
        while (!vipStack.isEmpty()) {

            Guest guest = vipStack.pop();

            if (guest != null) {
                guestList.add(guest);
                temporaryStack.push(guest);
            }
        }

        //======================================================
        // Restore VIP Stack
        //======================================================
        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return guestList;
    }

    //==========================================================
    // 5. Search Guest
    //
    // Waiting + Allocated
    //==========================================================
    public ListInterface<Guest> searchGuest(String keyword) {

        ListInterface<Guest> matchingGuests = new DoublyLinkedList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return matchingGuests;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        DoublyLinkedList<Guest> allGuests = guestDatabase.retrieveFromFile();

        if (allGuests == null || allGuests.isEmpty()) {
            return matchingGuests;
        }

        for (int i = 1; i <= allGuests.getSize(); i++) {

            Guest guest = allGuests.getEntry(i);

            if (guest == null) {
                continue;
            }

            syncGuestTierFromLoyaltyModule(guest);

            String searchData = String.valueOf(guest.getGuestID()) + " "
                    + String.valueOf(guest.getGuestName()) + " "
                    + String.valueOf(guest.getPhoneNumber()) + " "
                    + String.valueOf(guest.getLoyaltyTier()) + " "
                    + guest.getPriority() + " "
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
    // Search Waiting Guest
    //==========================================================
    public Guest searchGuestByID(String guestID) {

        syncWaitingBookingsIntoVipStack();
        refreshPriorityStackFromLoyaltyModule();

        return searchGuestByIDWithoutRefresh(guestID);
    }

    //==========================================================
    // Search Stack Without Refresh
    //==========================================================
    private Guest searchGuestByIDWithoutRefresh(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        Guest foundGuest = null;

        while (!vipStack.isEmpty()) {

            Guest guest = vipStack.pop();

            temporaryStack.push(guest);

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                foundGuest = guest;
                break;
            }
        }

        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return foundGuest;
    }

    //==========================================================
    // Search All Guest
    //==========================================================
    public Guest searchAllGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        Guest guest = guestDatabase.searchGuestByID(guestID.trim());

        if (guest != null) {
            syncGuestTierFromLoyaltyModule(guest);
        }

        return guest;
    }

    //==========================================================
    // 6. Available Rooms
    //
    // Housekeeping / Room Module
    //==========================================================
    public DoublyLinkedList<Room> getAvailableRoomsFromRoomModule() {

        DoublyLinkedList<Room> availableRooms = new DoublyLinkedList<>();

        if (roomDao == null) {
            roomDao = new RoomDao();
        }

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
    // Total Hotel Rooms
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
    // Room Type Name
    //==========================================================
    public String getRoomTypeName(Room room) {

        if (room == null) {
            return "";
        }

        return RoomTypeUtil.roomTypeName(room.getRoomType());
    }

    //==========================================================
    // Room Status Name
    //==========================================================
    public String getRoomStatusName(Room room) {

        if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
            return "";
        }

        int statusCode = room.getStatusHistory().getCurrentData().getStatusCode();

        return RoomStatusUtil.statusName(statusCode);
    }

    //==========================================================
    // 7. Remove Guest
    //
    // Uses Temporary Stack
    //==========================================================
    public Guest removeGuestByID(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        syncWaitingBookingsIntoVipStack();

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        Guest removedGuest = null;

        while (!vipStack.isEmpty()) {

            Guest guest = vipStack.pop();

            if (guest != null && guest.getGuestID() != null && guest.getGuestID().equalsIgnoreCase(guestID.trim())) {
                removedGuest = guest;
                break;
            }

            temporaryStack.push(guest);
        }

        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return removedGuest;
    }

    //==========================================================
    // Check Report Date
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
    // Report Filtering
    //==========================================================
    private boolean matchesReportFilters(Guest guest, int year, int month, String loyaltyFilter, String roomTypeFilter) {

        if (!isGuestInReportPeriod(guest, year, month)) {
            return false;
        }

        //======================================================
        // Loyalty Tier Filter
        //======================================================
        if (loyaltyFilter != null && !loyaltyFilter.equalsIgnoreCase("All")) {

            LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guest.getGuestID());

            if (record == null || record.getLoyaltyTier() == null || !record.getLoyaltyTier().equalsIgnoreCase(loyaltyFilter)) {
                return false;
            }
        }

        //======================================================
        // Room Type Filter
        //======================================================
        if (roomTypeFilter != null && !roomTypeFilter.equalsIgnoreCase("All")) {

            if (guest.getRoomType() == null) {
                return false;
            }

            String guestRoomType = normalizeRoomType(guest.getRoomType());
            String selectedRoomType = normalizeRoomType(roomTypeFilter);

            if (!guestRoomType.equalsIgnoreCase(selectedRoomType)) {
                return false;
            }
        }

        return true;
    }

    //==========================================================
    // Normalize Room Type
    //==========================================================
    private String normalizeRoomType(String roomType) {

        if (roomType == null) {
            return "";
        }

        String value = roomType.trim();

        if (value.equalsIgnoreCase("Single") || value.equalsIgnoreCase("Small") || value.equalsIgnoreCase("Small Room")) {
            return "Small Room";
        }

        if (value.equalsIgnoreCase("Medium") || value.equalsIgnoreCase("Middle") || value.equalsIgnoreCase("Medium Room") || value.equalsIgnoreCase("Middle Room")) {
            return "Medium Room";
        }

        if (value.equalsIgnoreCase("Large") || value.equalsIgnoreCase("Big") || value.equalsIgnoreCase("Big Room")) {
            return "Big Room";
        }

        return value;
    }

    //==========================================================
    // 8. Loyalty Tier Report With Filtering
    //==========================================================
    public int[] getLoyaltyTierCountsFromLoyaltyModule(int year, int month, String loyaltyFilter, String roomTypeFilter) {

        int[] counts = new int[4];

        DoublyLinkedList<Guest> allGuests = guestDatabase.retrieveFromFile();

        if (allGuests == null || allGuests.isEmpty()) {
            return counts;
        }

        for (int i = 1; i <= allGuests.getSize(); i++) {

            Guest guest = allGuests.getEntry(i);

            if (guest == null || !matchesReportFilters(guest, year, month, loyaltyFilter, roomTypeFilter)) {
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
    // Compatibility Version
    //==========================================================
    public int[] getLoyaltyTierCountsFromLoyaltyModule(int year, int month) {
        return getLoyaltyTierCountsFromLoyaltyModule(year, month, "All", "All");
    }

    //==========================================================
    // Total Loyalty Members With Filter
    //==========================================================
    public int getTotalLoyaltyMembersFromLoyaltyModule(int year, int month, String loyaltyFilter, String roomTypeFilter) {

        int[] counts = getLoyaltyTierCountsFromLoyaltyModule(year, month, loyaltyFilter, roomTypeFilter);

        return counts[0] + counts[1] + counts[2] + counts[3];
    }

    //==========================================================
    // Compatibility Version
    //==========================================================
    public int getTotalLoyaltyMembersFromLoyaltyModule(int year, int month) {
        return getTotalLoyaltyMembersFromLoyaltyModule(year, month, "All", "All");
    }

    //==========================================================
    // 9. Room Type Report With Filtering
    //==========================================================
    public int[] getRoomTypeCounts(int year, int month, String loyaltyFilter, String roomTypeFilter) {

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

            if (!matchesReportFilters(guest, year, month, loyaltyFilter, roomTypeFilter)) {
                continue;
            }

            String roomType = normalizeRoomType(guest.getRoomType());

            if (roomType.equalsIgnoreCase("Small Room")) {
                counts[0]++;
            } else if (roomType.equalsIgnoreCase("Medium Room")) {
                counts[1]++;
            } else if (roomType.equalsIgnoreCase("Big Room")) {
                counts[2]++;
            }
        }

        return counts;
    }

    //==========================================================
    // Compatibility Version
    //==========================================================
    public int[] getRoomTypeCounts(int year, int month) {
        return getRoomTypeCounts(year, month, "All", "All");
    }

    //==========================================================
    // Total Guest With Filter
    //==========================================================
    public int getTotalGuests(int year, int month, String loyaltyFilter, String roomTypeFilter) {

        int[] counts = getRoomTypeCounts(year, month, loyaltyFilter, roomTypeFilter);

        return counts[0] + counts[1] + counts[2];
    }

    //==========================================================
    // Compatibility Version
    //==========================================================
    public int getTotalGuests(int year, int month) {
        return getTotalGuests(year, month, "All", "All");
    }

    //==========================================================
    // Load Guest Database
    //==========================================================
    public int loadGuestDatabase() {

        DoublyLinkedList<Guest> guests = guestDatabase.retrieveFromFile();

        vipStack = new DoublyLinkedList<>();

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

            syncGuestTierFromLoyaltyModule(guest);

            if (guest.getRoomStatus() != null && guest.getRoomStatus().equalsIgnoreCase("Waiting")) {

                insertPriority(guest);

                totalLoaded++;
            }
        }

        //======================================================
        // Also Read Waiting Booking Guests
        //======================================================
        syncWaitingBookingsIntoVipStack();

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
                // Ignore Invalid Guest ID
            }
        }

        nextGuestID = highestID + 1;
    }

    //==========================================================
    // Stack Empty
    //==========================================================
    public boolean isStackEmpty() {

        syncWaitingBookingsIntoVipStack();

        return vipStack == null || vipStack.isEmpty();
    }

    //==========================================================
    // Total Waiting Guests
    //==========================================================
    public int getTotalGuests() {

        syncWaitingBookingsIntoVipStack();

        if (vipStack == null) {
            return 0;
        }

        return vipStack.getCurrentSize();
    }
}