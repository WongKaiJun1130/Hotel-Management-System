/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

/**
 *
 * @author USER
 */

import Adt.ListInterface;
import Adt.DoublyLinkedList;
import Entity.StatusEntry;
import Utility.RoomStatusUtil;
import Utility.RoomTypeUtil;
import Entity.Room;
import Entity.Booking;
import dao.RoomDao;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author Kah SHun
 */
public class HousekeepingControl {

    public static ListInterface<Room> roomList = new DoublyLinkedList<>();
    
    private static boolean dummyRoomsLoaded = false;

    // Loads the 20 dummy rooms (built by RoomDao, directly in memory - no
    // file storage) into the room registry. Guarded so it only loads once -
    // calling it again after rooms have already been registered/modified
    // won't wipe out that in-memory progress.
    public static void loadDummyRooms() {
        if (dummyRoomsLoaded) {
            return;
        }

        ListInterface<Room> dummyRooms = RoomDao.createRoomData();

        Iterator<Room> iterator = dummyRooms.getIterator();
        while (iterator.hasNext()) {
            roomList.add(iterator.next());
        }

        dummyRoomsLoaded = true;
    }

    // ==============================
    // Auto-advance: rooms move to the next status on their own after
    // sitting unchanged for AUTO_ADVANCE_INTERVAL_MILLIS. Runs on a
    // background daemon thread that starts counting the moment the
    // scheduler is started (call this once, e.g. at program startup).
    // ==============================

    // How long a room can sit at its current status before it's
    // auto-advanced to the next stage. Set small (e.g. 15 * 1000 for
    // 15 seconds) while testing; 15 minutes for the real thing.
    private static final long AUTO_ADVANCE_INTERVAL_MILLIS = 30 * 1000; // TESTING: 15 seconds (set to 15 * 60 * 1000 for the real 15 min)

    // How often the scheduler checks all rooms. Independent of the
    // interval above - just needs to be frequent enough that a room
    // isn't left waiting long after it becomes due.
    private static final long CHECK_FREQUENCY_MILLIS = 30 * 1000; // every 30 seconds

    private static Timer autoAdvanceTimer;

    // Starts the background timer as a daemon thread (so it never
    // blocks the program from exiting normally). Safe to call more
    // than once - only the first call actually starts anything.
    public static void startAutoAdvanceScheduler() {
        if (autoAdvanceTimer != null) {
            return;
        }

        autoAdvanceTimer = new Timer(true);
        autoAdvanceTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                autoAdvanceDueRooms();
            }
        }, CHECK_FREQUENCY_MILLIS, CHECK_FREQUENCY_MILLIS);
    }

    // Checks every room's current status entry; if it has been sitting
    // for at least AUTO_ADVANCE_INTERVAL_MILLIS and there is a valid
    // next status, advances it automatically.
    private static void autoAdvanceDueRooms() {
        long now = System.currentTimeMillis();

        for (int i = 1; i <= roomList.getSize(); i++) {
            Room room = roomList.getEntry(i);
            StatusEntry current = room.getStatusHistory().getCurrentData();

            if (current == null) {
                continue;
            }

            // Rooms on hold for a guest's late check-out must stay put
            // until a supervisor manually resumes them - never auto-advance
            // out of this status, regardless of how long it's been sitting.
            if (current.getStatusCode() == RoomStatusUtil.Late_CheckOut_Hold) {
                continue;
            }

            long elapsed = now - current.getTimestamp();
            if (elapsed < AUTO_ADVANCE_INTERVAL_MILLIS) {
                continue;
            }

            int nextStatus = RoomStatusUtil.nextStatusAfter(current.getStatusCode());
            if (nextStatus == -1) {
                continue;
            }

            advanceStatus(room, "Auto-advanced after " + formatInterval(AUTO_ADVANCE_INTERVAL_MILLIS));
        }
    }

    // Formats the interval as seconds if it's under a minute, otherwise
    // as minutes - so the note text stays accurate whether you're
    // testing with a short interval or running the real 15-minute one.
    private static String formatInterval(long millis) {
        if (millis < 60000) {
            return (millis / 1000) + " sec";
        }
        return (millis / 60000) + " min";
    }

    // Room register

    public static Room registerRoom(String roomNum, int roomType) {
        Room room = new Room(roomNum, roomType);
        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Room Registered"));
        roomList.add(room);
        return room;
    }

    public static Room findRoom(String roomNum) {
        for (int i = 1; i <= roomList.getSize(); i++) {
            Room room = roomList.getEntry(i);
            if (room.getRoomNum().equalsIgnoreCase(roomNum)) {
                return room;
            }
        }
        return null;
    }

    public static boolean roomIsEmpty() {
        return roomList.isEmpty();
    }

    public static int getRoomCount() {
        return roomList.getSize();
    }

    public static Room getRoomAt(int index) {
        return roomList.getEntry(index);
    }


    // Status transitions
    public static int advanceStatus(Room room, String note) {
        StatusEntry current = room.getStatusHistory().getCurrentData();
        int nextStatus = RoomStatusUtil.nextStatusAfter(current.getStatusCode());

        if (nextStatus == -1) {
            return -1;
        }

        room.getStatusHistory().insertAndAdvance(new StatusEntry(nextStatus, note));
        return nextStatus;
    }

    // Supervisor logged the wrong status. Returns the restored status
    // code, or -1 if already at the earliest entry

    public static int rollbackStatus(Room room) {
        StatusEntry restored = room.getStatusHistory().rollback();
        return (restored == null) ? -1 : restored.getStatusCode();
    }

    // Guest requests late check-out mid-cleaning: splice a hold entry in
    // without discarding whatever cleaning step was queued next
    public static void interruptForLateCheckout(Room room, String note) {
        room.getStatusHistory().spliceAfterCurrent(new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold, note));
    }

    // Continue the cleaning flow after the interruption is resolved
    // Returns the resumed status code, or -1 if nothing was queued
    public static int resumeStatus(Room room) {
        StatusEntry resumed = room.getStatusHistory().redo();
        return (resumed == null) ? -1 : resumed.getStatusCode();
    }

    // Guest checks out: room goes back to Dirty, ready to be re-cleaned.
    // Returns false if the room was already Dirty
    public static boolean guestCheckOut(Room room) {
        int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();
        if (currentStatus == RoomStatusUtil.Dirty) {
            return false;
        }

        room.getStatusHistory().insertAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Guest checked out - needs cleaning"));
        return true;
    }


    // Queries for reports

    // Index matches RoomStatusUtil status codes: Dirty, Clean_In_Progress,
    // Inspected, Ready_For_CheckIN, Late_CheckOut_Hold

    public static int[] getStatusCounts() {
        int[] counts = new int[5];
        for (int i = 1; i <= roomList.getSize(); i++) {
            int status = roomList.getEntry(i).getStatusHistory().getCurrentData().getStatusCode();
            if (status >= 0 && status < counts.length) {
                counts[status]++;
            }
        }
        return counts;
    }

    // Index matches RoomTypeUtil type codes: Normal_Room, Deluxe_Room, VIP_Room

    public static int[] getTypeCounts() {
        int[] counts = new int[3];
        for (int i = 1; i <= roomList.getSize(); i++) {
            int type = roomList.getEntry(i).getRoomType();
            if (type >= 0 && type < counts.length) {
                counts[type]++;
            }
        }
        return counts;
    }


    // ==============================
    // Search
    // ==============================

    // Flexible keyword search across room number, room type name, and
    // status name. Case-insensitive partial match - e.g. "vip" finds
    // every VIP room, "dirty" finds every dirty room, "101" finds room
    // 101. Mirrors VIPAllocationControl.searchGuest()'s pattern.
    public static ListInterface<Room> searchRooms(String keyword) {
        ListInterface<Room> result = new DoublyLinkedList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        for (int i = 1; i <= roomList.getSize(); i++) {
            Room room = roomList.getEntry(i);
            StatusEntry current = room.getStatusHistory().getCurrentData();

            String searchData = room.getRoomNum() + " "
                    + RoomTypeUtil.roomTypeName(room.getRoomType()) + " "
                    + RoomStatusUtil.statusName(current.getStatusCode());

            if (searchData.toLowerCase().contains(searchKeyword)) {
                result.add(room);
            }
        }

        return result;
    }


    // ==============================
    // Filter (multi-criteria)
    // ==============================

    // Filters by any combination of room type and status at once. Pass
    // null for either parameter to mean "any" for that field - e.g.
    // filterRooms(RoomTypeUtil.VIP_Room, null) returns every VIP room
    // regardless of status; filterRooms(null, RoomStatusUtil.Dirty)
    // returns every Dirty room regardless of type; supplying both
    // narrows to rooms matching BOTH criteria simultaneously. This is
    // distinct from searchRooms(): search is a single free-text keyword
    // match, filter is structured, multi-field, and exact.
    public static ListInterface<Room> filterRooms(Integer roomType, Integer status) {
        ListInterface<Room> result = new DoublyLinkedList<>();

        for (int i = 1; i <= roomList.getSize(); i++) {
            Room room = roomList.getEntry(i);
            int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();

            boolean typeMatches = (roomType == null) || (room.getRoomType() == roomType);
            boolean statusMatches = (status == null) || (currentStatus == status);

            if (typeMatches && statusMatches) {
                result.add(room);
            }
        }

        return result;
    }


    // ==============================
    // Sort
    // ==============================
    // sortRooms() works on ANY list you hand it (not just roomList), so
    // the same sorting logic can be reused on a filtered subset - that's
    // what makes the business-cycle report below possible without
    // duplicating the insertion sort. Returns a NEW list in sorted order;
    // the list passed in is left untouched. sortOption: 1 = room number,
    // 2 = status, 3 = room type.
    public static ListInterface<Room> sortRooms(ListInterface<Room> roomsToSort, int sortOption) {
        Room[] rooms = toArray(roomsToSort);

        switch (sortOption) {
            case 2:
                insertionSort(rooms, (a, b) -> {
                    int statusA = a.getStatusHistory().getCurrentData().getStatusCode();
                    int statusB = b.getStatusHistory().getCurrentData().getStatusCode();
                    return Integer.compare(statusA, statusB);
                });
                break;
            case 3:
                insertionSort(rooms, (a, b) -> Integer.compare(a.getRoomType(), b.getRoomType()));
                break;
            default:
                insertionSort(rooms, (a, b) -> a.getRoomNum().compareToIgnoreCase(b.getRoomNum()));
                break;
        }

        return fromArray(rooms);
    }

    // Sorts by room number, alphanumeric ascending.
    public static ListInterface<Room> getRoomsSortedByRoomNumber() {
        return sortRooms(roomList, 1);
    }

    // Sorts by status, following the natural cleaning-flow order:
    // Dirty -> Cleaning In Progress -> Inspected -> Ready -> Late Check-Out Hold
    public static ListInterface<Room> getRoomsSortedByStatus() {
        return sortRooms(roomList, 2);
    }

    // Sorts by room type: Normal -> Deluxe -> VIP
    public static ListInterface<Room> getRoomsSortedByType() {
        return sortRooms(roomList, 3);
    }

    private static Room[] toArray(ListInterface<Room> list) {
        Room[] rooms = new Room[list.getSize()];
        for (int i = 1; i <= list.getSize(); i++) {
            rooms[i - 1] = list.getEntry(i);
        }
        return rooms;
    }

    private static ListInterface<Room> fromArray(Room[] rooms) {
        ListInterface<Room> sorted = new DoublyLinkedList<>();
        for (Room room : rooms) {
            sorted.add(room);
        }
        return sorted;
    }

    // Classic insertion sort: O(n^2) worst case, O(n) best case on
    // already-sorted input, stable. Chosen over a library sort since
    // Room is stored in our own custom ADT, not a java.util.List, and
    // insertion sort is simple to trace/explain for a small list like
    // a hotel's room registry.
    private static void insertionSort(Room[] rooms, Comparator<Room> comparator) {
        for (int i = 1; i < rooms.length; i++) {
            Room key = rooms[i];
            int j = i - 1;

            while (j >= 0 && comparator.compare(rooms[j], key) > 0) {
                rooms[j + 1] = rooms[j];
                j--;
            }

            rooms[j + 1] = key;
        }
    }


    // ==============================
    // Business-Cycle Report: filter + sort combined
    // ==============================
    // End-of-cycle operational report: rooms are first FILTERED by any
    // combination of type/status (null = no filter on that field), then
    // the filtered subset is SORTED by the chosen field, in a single
    // pipeline - exactly the "filter on multiple criteria + sort ->
    // structured report" algorithm management asks for at the close of
    // each business cycle. Both filterRooms() and sortRooms() are reused
    // as-is; this method's job is purely to chain them.
    public static ListInterface<Room> generateBusinessCycleReport(Integer roomTypeFilter, Integer statusFilter, int sortOption) {
        ListInterface<Room> filtered = filterRooms(roomTypeFilter, statusFilter);
        return sortRooms(filtered, sortOption);
    }


    // ==============================
    // Cross-module report: Housekeeping + Booking
    // ==============================

    // Matches rooms currently Ready for Check-In with bookings still
    // waiting for a room of the same type. This is a genuine
    // cross-module report - it correlates Room (this module) with
    // Booking (a different module, via BookingControl), not just
    // Housekeeping's own data.
    public static ListInterface<RoomBookingMatch> getReadyRoomsForWaitingGuests(BookingControl bookingControl) {
        ListInterface<RoomBookingMatch> matches = new DoublyLinkedList<>();

        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();

        for (int i = 1; i <= roomList.getSize(); i++) {
            Room room = roomList.getEntry(i);
            int status = room.getStatusHistory().getCurrentData().getStatusCode();

            if (status != RoomStatusUtil.Ready_For_CheckIN) {
                continue;
            }

            for (int j = 1; j <= waitingBookings.getSize(); j++) {
                Booking booking = waitingBookings.getEntry(j);
                if (matchesRoomType(room.getRoomType(), booking.getRoomType())) {
                    matches.add(new RoomBookingMatch(room, booking));
                }
            }
        }

        return matches;
    }

    // Same correlation as above, but scoped to a single room - used
    // by the Room Status History report so it can show which waiting
    // bookings (if any) this specific room could be assigned to.
    public static ListInterface<Booking> getWaitingBookingsForRoom(Room room, BookingControl bookingControl) {
        ListInterface<Booking> matches = new DoublyLinkedList<>();

        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();

        for (int j = 1; j <= waitingBookings.getSize(); j++) {
            Booking booking = waitingBookings.getEntry(j);
            if (matchesRoomType(room.getRoomType(), booking.getRoomType())) {
                matches.add(booking);
            }
        }

        return matches;
    }

    // Booking and Housekeeping use different room-type vocabularies
    // (see RoomTypeUtil.matchesLabel for details), so the comparison is
    // delegated there instead of a direct string match.
    private static boolean matchesRoomType(int housekeepingRoomType, String bookingRoomType) {
        if (bookingRoomType == null) {
            return false;
        }
        int bookingTypeCode;
        String typeLower = bookingRoomType.trim().toLowerCase();

        if (typeLower.contains("deluxe")) {
            bookingTypeCode = RoomTypeUtil.Single;
        } else if (typeLower.contains("vip") || typeLower.contains("suite")) {
            bookingTypeCode = RoomTypeUtil.Medium;
        } else {
            bookingTypeCode = RoomTypeUtil.Large;
        }

        return housekeepingRoomType == bookingTypeCode;
    }

    // Pairs a Room with a Booking for display - not a persisted
    // entity, purely a result holder for the cross-module report.
    public static class RoomBookingMatch {
        private final Room room;
        private final Booking booking;

        public RoomBookingMatch(Room room, Booking booking) {
            this.room = room;
            this.booking = booking;
        }

        public Room getRoom() {
            return room;
        }

        public Booking getBooking() {
            return booking;
        }
    }
}