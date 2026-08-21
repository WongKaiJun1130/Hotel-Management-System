package control;

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

//Author: Kai Jun
public class VIPAllocationControl {

    //ADT Declaration
    private StackInterface<Guest> vipStack = new DoublyLinkedList<>();

    //Guest ID
    private int nextGuestID;

    //Database Declaration
    private GuestDao guestDatabase;
    private BookingDao bookingDatabase;
    private LoyaltyControl loyaltyControl;
    private RoomDao roomDao;

    //Date Format
    private static final DateTimeFormatter ARRIVAL_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter CHECK_IN_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    //Constructor
    public VIPAllocationControl() {
        guestDatabase = new GuestDao();
        bookingDatabase = new BookingDao();
        loyaltyControl = new LoyaltyControl();
        roomDao = new RoomDao();
        nextGuestID = 1;
        loadGuestDatabase();
    }

    //Generate Guest ID
    public String generateGuestID() {
        updateNextGuestID(guestDatabase.retrieveFromFile());
        return String.format("R%04d", nextGuestID);
    }

    //Add Guest
    public boolean addGuestToStack(Guest guest) {

        if (guest == null) {
            return false;
        }

        int lifetimePoints = getMinimumPointsForTier(guest.getLoyaltyTier());

        return addGuestToStack(guest, lifetimePoints);
    }

    //Add Guest With Points
    public boolean addGuestToStack(Guest guest, int lifetimePoints) {

        if (guest == null) {
            return false;
        }

        if (guest.getGuestID() == null || guest.getGuestID().trim().isEmpty()) {
            return false;
        }

        //Check Stack Duplicate
        if (searchGuestByIDWithoutRefresh(guest.getGuestID()) != null) {
            return false;
        }

        //Check Guest Duplicate
        Guest existingGuest = guestDatabase.searchGuestByID(guest.getGuestID());

        if (existingGuest != null) {
            return false;
        }

        //Create Loyalty Member
        boolean loyaltyCreated = loyaltyControl.createLoyaltyMember(guest, lifetimePoints);

        if (!loyaltyCreated) {
            return false;
        }

        //Save Guest
        guestDatabase.addGuest(guest);

        //Sync Loyalty Tier
        syncGuestTierFromLoyaltyModule(guest);

        //Insert Guest
        insertPriority(guest);

        //Update Guest ID
        updateNextGuestID(guestDatabase.retrieveFromFile());

        return true;
    }

    //Get Minimum Points
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

    //Sync Loyalty Tier
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

    //Sync Booking Guests
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

            //Get Guest Information
            Guest guest = guestDatabase.searchGuestByID(booking.getGuestID().trim());

            if (guest == null) {
                continue;
            }

            //Check Waiting Guest
            if (guest.getRoomStatus() == null || !guest.getRoomStatus().equalsIgnoreCase("Waiting")) {
                continue;
            }

            //Check Duplicate
            if (searchGuestByIDWithoutRefresh(guest.getGuestID()) != null) {
                continue;
            }

            //Sync Loyalty Tier
            syncGuestTierFromLoyaltyModule(guest);

            //Insert Guest
            insertPriority(guest);

            totalAdded++;
        }

        //Update Guest ID
        updateNextGuestID(guestDatabase.retrieveFromFile());

        return totalAdded;
    }

    //Refresh Priority Stack
    public int refreshPriorityStackFromLoyaltyModule() {

        if (vipStack == null || vipStack.isEmpty()) {
            return 0;
        }

        StackInterface<Guest> oldStack = new DoublyLinkedList<>();

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

    //Get Loyalty Record
    public LoyaltyRecord getLoyaltyRecordFromLoyaltyModule(String guestID) {

        if (guestID == null || guestID.trim().isEmpty()) {
            return null;
        }

        if (loyaltyControl == null) {
            loyaltyControl = new LoyaltyControl();
        }

        return loyaltyControl.searchGuest(guestID.trim());
    }

    //Get Lifetime Points
    public int getLifetimePointsFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null) {
            return -1;
        }

        return record.getLifetimePoints();
    }

    //Get Loyalty Tier
    public String getLoyaltyTierFromLoyaltyModule(String guestID) {

        LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guestID);

        if (record == null || record.getLoyaltyTier() == null) {
            return null;
        }

        return record.getLoyaltyTier();
    }

    //Insert Guest By Priority
    private void insertPriority(Guest guest) {

        if (guest == null) {
            return;
        }

        //Temporary Stack
        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        while (!vipStack.isEmpty() && !shouldInsertBefore(guest, vipStack.peek())) {
            temporaryStack.push(vipStack.pop());
        }

        vipStack.push(guest);

        //Restore Stack
        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }
    }

    //Compare Guest Priority
    private boolean shouldInsertBefore(Guest newGuest, Guest currentGuest) {

        if (newGuest == null || currentGuest == null) {
            return false;
        }

        //Compare Loyalty Tier
        if (newGuest.getPriority() > currentGuest.getPriority()) {
            return true;
        }

        if (newGuest.getPriority() < currentGuest.getPriority()) {
            return false;
        }

        //Compare Lifetime Points
        int newPoints = getLifetimePointsFromLoyaltyModule(newGuest.getGuestID());
        int currentPoints = getLifetimePointsFromLoyaltyModule(currentGuest.getGuestID());

        if (newPoints < 0) {
            newPoints = 0;
        }

        if (currentPoints < 0) {
            currentPoints = 0;
        }

        if (newPoints > currentPoints) {
            return true;
        }

        if (newPoints < currentPoints) {
            return false;
        }

        //Compare Arrival Time
        LocalDateTime newArrival = parseArrivalDateTime(newGuest.getArrivalDateTime());
        LocalDateTime currentArrival = parseArrivalDateTime(currentGuest.getArrivalDateTime());

        if (newArrival == null && currentArrival == null) {
            return false;
        }

        if (newArrival != null && currentArrival == null) {
            return true;
        }

        if (newArrival == null) {
            return false;
        }

        return newArrival.isBefore(currentArrival);
    }

    //Parse Arrival DateTime
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

    //Allocate Room
    public Guest allocateRoom() {

        syncWaitingBookingsIntoVipStack();

        refreshPriorityStackFromLoyaltyModule();

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        //Stack Pop
        Guest guest = vipStack.pop();

        if (guest == null) {
            return null;
        }

        guest.setRoomStatus("Allocated");

        return guest;
    }

    //View Next Priority Guest
    public Guest getNextPriorityGuest() {

        syncWaitingBookingsIntoVipStack();

        refreshPriorityStackFromLoyaltyModule();

        if (vipStack == null || vipStack.isEmpty()) {
            return null;
        }

        //Stack Peek
        return vipStack.peek();
    }

    //Display Allocation Stack
    public ListInterface<Guest> getAllGuestAllocations() {

        //Sync Booking Guests
        syncWaitingBookingsIntoVipStack();

        //Refresh Priority
        refreshPriorityStackFromLoyaltyModule();

        ListInterface<Guest> guestList = new DoublyLinkedList<>();

        //Temporary Stack
        StackInterface<Guest> temporaryStack = new DoublyLinkedList<>();

        if (vipStack == null || vipStack.isEmpty()) {
            return guestList;
        }

        //Read Stack
        while (!vipStack.isEmpty()) {

            Guest guest = vipStack.pop();

            if (guest != null) {
                guestList.add(guest);
                temporaryStack.push(guest);
            }
        }

        //Restore Stack
        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return guestList;
    }

    //Search Guest
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
                    + String.valueOf(getLifetimePointsFromLoyaltyModule(guest.getGuestID())) + " "
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

    //Search Waiting Guest
    public Guest searchGuestByID(String guestID) {

        syncWaitingBookingsIntoVipStack();

        refreshPriorityStackFromLoyaltyModule();

        return searchGuestByIDWithoutRefresh(guestID);
    }

    //Search Guest In Stack
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

        //Restore Stack
        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return foundGuest;
    }

    //Search All Guest
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

    //Get Available Rooms
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

    //Get Total Rooms
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

    //Get Room Type
    public String getRoomTypeName(Room room) {

        if (room == null) {
            return "";
        }

        return RoomTypeUtil.roomTypeName(room.getRoomType());
    }

    //Get Room Status
    public String getRoomStatusName(Room room) {

        if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
            return "";
        }

        int statusCode = room.getStatusHistory().getCurrentData().getStatusCode();

        return RoomStatusUtil.statusName(statusCode);
    }

    //Remove Guest
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

        //Restore Stack
        while (!temporaryStack.isEmpty()) {
            vipStack.push(temporaryStack.pop());
        }

        return removedGuest;
    }

    //Check Report Date
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

    //Filter Report
    private boolean matchesReportFilters(Guest guest, int year, int month, String loyaltyFilter, String roomTypeFilter) {

        if (!isGuestInReportPeriod(guest, year, month)) {
            return false;
        }

        //Filter Loyalty Tier
        if (loyaltyFilter != null && !loyaltyFilter.equalsIgnoreCase("All")) {

            LoyaltyRecord record = getLoyaltyRecordFromLoyaltyModule(guest.getGuestID());

            if (record == null || record.getLoyaltyTier() == null || !record.getLoyaltyTier().equalsIgnoreCase(loyaltyFilter)) {
                return false;
            }
        }

        //Filter Room Type
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

    //Normalize Room Type
    private String normalizeRoomType(String roomType) {

        if (roomType == null) {
            return "";
        }

        String value = roomType.trim();

        if (value.equalsIgnoreCase("Single") || value.equalsIgnoreCase("Single Room") || value.equalsIgnoreCase("Small") || value.equalsIgnoreCase("Small Room")) {
            return "Small Room";
        }

        if (value.equalsIgnoreCase("Medium") || value.equalsIgnoreCase("Middle") || value.equalsIgnoreCase("Medium Room") || value.equalsIgnoreCase("Middle Room")) {
            return "Medium Room";
        }

        if (value.equalsIgnoreCase("Large") || value.equalsIgnoreCase("Large Room") || value.equalsIgnoreCase("Big") || value.equalsIgnoreCase("Big Room")) {
            return "Big Room";
        }

        return value;
    }

    //Loyalty Tier Report
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

    //Loyalty Tier Report Default
    public int[] getLoyaltyTierCountsFromLoyaltyModule(int year, int month) {
        return getLoyaltyTierCountsFromLoyaltyModule(year, month, "All", "All");
    }

    //Get Total Loyalty Members
    public int getTotalLoyaltyMembersFromLoyaltyModule(int year, int month, String loyaltyFilter, String roomTypeFilter) {

        int[] counts = getLoyaltyTierCountsFromLoyaltyModule(year, month, loyaltyFilter, roomTypeFilter);

        return counts[0] + counts[1] + counts[2] + counts[3];
    }

    //Get Total Loyalty Members Default
    public int getTotalLoyaltyMembersFromLoyaltyModule(int year, int month) {
        return getTotalLoyaltyMembersFromLoyaltyModule(year, month, "All", "All");
    }

    //Room Type Report
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

    //Room Type Report Default
    public int[] getRoomTypeCounts(int year, int month) {
        return getRoomTypeCounts(year, month, "All", "All");
    }

    //Get Total Guests
    public int getTotalGuests(int year, int month, String loyaltyFilter, String roomTypeFilter) {

        int[] counts = getRoomTypeCounts(year, month, loyaltyFilter, roomTypeFilter);

        return counts[0] + counts[1] + counts[2];
    }

    //Get Total Guests Default
    public int getTotalGuests(int year, int month) {
        return getTotalGuests(year, month, "All", "All");
    }

    //Load Guest Data
    public int loadGuestDatabase() {

        DoublyLinkedList<Guest> guests = guestDatabase.retrieveFromFile();

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

            //Sync Loyalty Tier
            syncGuestTierFromLoyaltyModule(guest);

            //Load Waiting Guest
            if (guest.getRoomStatus() != null && guest.getRoomStatus().equalsIgnoreCase("Waiting")) {

                insertPriority(guest);

                totalLoaded++;
            }
        }

        //Sync Booking Guests
        syncWaitingBookingsIntoVipStack();

        //Update Guest ID
        updateNextGuestID(guests);

        return totalLoaded;
    }

    //Update Guest ID
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

                //Ignore Invalid ID
            }
        }

        nextGuestID = highestID + 1;
    }

    //Check Stack Empty
    public boolean isStackEmpty() {

        syncWaitingBookingsIntoVipStack();

        return vipStack == null || vipStack.isEmpty();
    }

    //Get Total Waiting Guests
    public int getTotalGuests() {

        syncWaitingBookingsIntoVipStack();

        if (vipStack == null) {
            return 0;
        }

        return vipStack.getCurrentSize();
    }
}