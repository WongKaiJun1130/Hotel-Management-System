package control;

/**
 *
 * @author Chia Kah Shun
 */

import adt.DoublyLinkedList;
import adt.ListInterface;
import dao.RoomDao;
import entity.Booking;
import entity.Room;
import entity.StatusEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import utility.RoomStatusUtil;
import utility.RoomTypeUtil;

public class HousekeepingControl {

    // ADT declaration
    public static ListInterface<Room> roomList = new DoublyLinkedList<>();
    private static boolean dummyRoomsLoaded = false;
    private static RoomDao roomDao = new RoomDao();

    private static final long AUTO_ADVANCE_INTERVAL_MILLIS = 60 * 1000; // 60 seconds
    private static final long CHECK_FREQUENCY_MILLIS = 30 * 1000;       // 30 seconds
    private static Timer autoAdvanceTimer;

    public static void loadDummyRooms() {
        if (dummyRoomsLoaded) {
            return;
        }
        ListInterface<Room> dummyRooms = roomDao.retrieveFromFile();
        if (dummyRooms != null) {
            Iterator<Room> iterator = dummyRooms.getIterator();
            while (iterator.hasNext()) {
                roomList.add(iterator.next());
            }
        }
        dummyRoomsLoaded = true;
    }

    private static void syncToDao() {
        synchronized (roomList) {
            roomDao.saveToFile(roomList);
        }
    }

    public static synchronized void startAutoAdvanceScheduler() {
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

    public static synchronized void stopAutoAdvanceScheduler() {
        if (autoAdvanceTimer != null) {
            autoAdvanceTimer.cancel();
            autoAdvanceTimer = null;
        }
    }

    private static void autoAdvanceDueRooms() {
        long now = System.currentTimeMillis();
        List<Room> roomsToAdvance = new ArrayList<>();

        // Phase 1: Read and collect target rooms under lock safely
        synchronized (roomList) {
            Iterator<Room> iterator = roomList.getIterator();
            while (iterator.hasNext()) {
                Room room = iterator.next();
                if (room == null || room.getStatusHistory() == null) continue;

                StatusEntry current = room.getStatusHistory().getCurrentData();
                if (current == null || current.getStatusCode() == RoomStatusUtil.Late_CheckOut_Hold) {
                    continue;
                }

                long elapsed = now - current.getTimestamp();
                if (elapsed >= AUTO_ADVANCE_INTERVAL_MILLIS) {
                    int nextStatus = RoomStatusUtil.nextStatusAfter(current.getStatusCode());
                    if (nextStatus != -1) {
                        roomsToAdvance.add(room);
                    }
                }
            }
        }

        // Phase 2: Perform status advancement outside the iteration loop
        for (Room room : roomsToAdvance) {
            advanceStatus(room, "Auto-advanced after " + formatInterval(AUTO_ADVANCE_INTERVAL_MILLIS));
        }
    }

    private static String formatInterval(long millis) {
        if (millis < 60000) {
            return (millis / 1000) + " sec";
        }
        return (millis / 60000) + " min";
    }

    public static Room registerRoom(String roomNum, int roomType) {
        Room room = new Room(roomNum, roomType);
        room.getStatusHistory().addAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Room Registered"));
        synchronized (roomList) {
            roomList.add(room);
        }
        syncToDao();
        return room;
    }

    public static Room findRoom(String roomNum) {
        if (roomNum == null) return null;
        synchronized (roomList) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room != null && room.getRoomNum().equalsIgnoreCase(roomNum)) {
                    return room;
                }
            }
        }
        return null;
    }

    public static boolean roomIsEmpty() {
        synchronized (roomList) {
            return roomList.isEmpty();
        }
    }

    public static int getRoomCount() {
        synchronized (roomList) {
            return roomList.getSize();
        }
    }

    public static Room getRoomAt(int index) {
        synchronized (roomList) {
            return roomList.getEntry(index);
        }
    }

    public static int advanceStatus(Room room, String note) {
        if (room == null || room.getStatusHistory() == null) return -1;
        int nextStatus;
        synchronized (roomList) {
            StatusEntry current = room.getStatusHistory().getCurrentData();
            if (current == null) return -1;

            nextStatus = RoomStatusUtil.nextStatusAfter(current.getStatusCode());
            if (nextStatus == -1) {
                return -1;
            }

            room.getStatusHistory().addAndAdvance(new StatusEntry(nextStatus, note));
        }
        syncToDao();
        return nextStatus;
    }

    public static int rollbackStatus(Room room) {
        if (room == null || room.getStatusHistory() == null) return -1;
        StatusEntry restored;
        synchronized (roomList) {
            restored = room.getStatusHistory().rollback();
        }
        syncToDao();
        return (restored == null) ? -1 : restored.getStatusCode();
    }

    public static void interruptForLateCheckout(Room room, String note) {
        if (room == null || room.getStatusHistory() == null) return;
        synchronized (roomList) {
            room.getStatusHistory().insertAfterCurrent(new StatusEntry(RoomStatusUtil.Late_CheckOut_Hold, note));
        }
        syncToDao();
    }

    public static int resumeStatus(Room room) {
        if (room == null || room.getStatusHistory() == null) return -1;
        StatusEntry resumed;
        synchronized (roomList) {
            resumed = room.getStatusHistory().redo();
        }
        syncToDao();
        return (resumed == null) ? -1 : resumed.getStatusCode();
    }

    public static boolean guestCheckOut(Room room) {
        if (room == null || room.getStatusHistory() == null) return false;
        synchronized (roomList) {
            StatusEntry current = room.getStatusHistory().getCurrentData();
            if (current != null && current.getStatusCode() == RoomStatusUtil.Dirty) {
                return false;
            }

            room.getStatusHistory().addAndAdvance(new StatusEntry(RoomStatusUtil.Dirty, "Guest checked out - needs cleaning"));
        }
        syncToDao();
        return true;
    }

    public static int[] getStatusCounts() {
        int[] counts = new int[5];
        synchronized (roomList) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room != null && room.getStatusHistory() != null && room.getStatusHistory().getCurrentData() != null) {
                    int status = room.getStatusHistory().getCurrentData().getStatusCode();
                    if (status >= 0 && status < counts.length) {
                        counts[status]++;
                    }
                }
            }
        }
        return counts;
    }

    public static int[] getTypeCounts() {
        int[] counts = new int[3];
        synchronized (roomList) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room != null) {
                    int type = room.getRoomType();
                    if (type >= 0 && type < counts.length) {
                        counts[type]++;
                    }
                }
            }
        }
        return counts;
    }

    public static ListInterface<Room> searchRooms(String keyword) {
        ListInterface<Room> result = new DoublyLinkedList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            return result;
        }

        String searchKeyword = keyword.trim().toLowerCase();

        synchronized (roomList) {
            Iterator<Room> iterator = roomList.getIterator();
            while (iterator.hasNext()) {
                Room room = iterator.next();
                if (room == null) continue;

                StatusEntry current = (room.getStatusHistory() != null) ? room.getStatusHistory().getCurrentData() : null;
                String statusLabel = (current != null) ? RoomStatusUtil.statusName(current.getStatusCode()) : "";
                
                String searchData = room.getRoomNum() + " "
                        + RoomTypeUtil.roomTypeName(room.getRoomType()) + " "
                        + statusLabel;

                if (searchData.toLowerCase().contains(searchKeyword)) {
                    result.add(room);
                }
            }
        }
        return result;
    }

    public static ListInterface<Room> filterRooms(Integer roomType, Integer status) {
        ListInterface<Room> result = new DoublyLinkedList<>();

        synchronized (roomList) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
                    continue;
                }

                int currentStatus = room.getStatusHistory().getCurrentData().getStatusCode();
                boolean typeMatches = (roomType == null) || (room.getRoomType() == roomType);
                boolean statusMatches = (status == null) || (currentStatus == status);

                if (typeMatches && statusMatches) {
                    result.add(room);
                }
            }
        }

        return result;
    }

    public static ListInterface<Room> sortRooms(ListInterface<Room> roomsToSort, int sortOption) {
        if (roomsToSort == null || roomsToSort.isEmpty()) {
            return new DoublyLinkedList<>();
        }

        Room[] rooms = toArray(roomsToSort);

        switch (sortOption) {
            case 2:
                insertionSort(rooms, (a, b) -> {
                    int statusA = (a.getStatusHistory() != null && a.getStatusHistory().getCurrentData() != null) 
                            ? a.getStatusHistory().getCurrentData().getStatusCode() : -1;
                    int statusB = (b.getStatusHistory() != null && b.getStatusHistory().getCurrentData() != null) 
                            ? b.getStatusHistory().getCurrentData().getStatusCode() : -1;
                    return Integer.compare(statusA, statusB);
                });
                break;
            case 3:
                insertionSort(rooms, Comparator.comparingInt(Room::getRoomType));
                break;
            default:
                insertionSort(rooms, (a, b) -> a.getRoomNum().compareToIgnoreCase(b.getRoomNum()));
                break;
        }

        return fromArray(rooms);
    }

    public static ListInterface<Room> getRoomsSortedByRoomNumber() {
        return sortRooms(roomList, 1);
    }

    public static ListInterface<Room> getRoomsSortedByStatus() {
        return sortRooms(roomList, 2);
    }

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

    public static ListInterface<Room> generateBusinessCycleReport(Integer roomTypeFilter, Integer statusFilter, int sortOption) {
        ListInterface<Room> filtered = filterRooms(roomTypeFilter, statusFilter);
        return sortRooms(filtered, sortOption);
    }

    // =========================================================
    // Cross-Module Booking Matching Methods & Static Inner Class
    // =========================================================
    public static ListInterface<RoomBookingMatch> getReadyRoomsForWaitingGuests(BookingControl bookingControl) {
        ListInterface<RoomBookingMatch> matches = new DoublyLinkedList<>();
        if (bookingControl == null) return matches;

        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();

        synchronized (roomList) {
            for (int i = 1; i <= roomList.getSize(); i++) {
                Room room = roomList.getEntry(i);
                if (room == null || room.getStatusHistory() == null || room.getStatusHistory().getCurrentData() == null) {
                    continue;
                }

                int status = room.getStatusHistory().getCurrentData().getStatusCode();
                if (status != RoomStatusUtil.Ready_For_CheckIN) {
                    continue;
                }

                for (int j = 1; j <= waitingBookings.getSize(); j++) {
                    Booking booking = waitingBookings.getEntry(j);
                    if (booking != null && matchesRoomType(room.getRoomType(), booking.getRoomType())) {
                        matches.add(new RoomBookingMatch(room, booking));
                    }
                }
            }
        }

        return matches;
    }

    public static ListInterface<Booking> getWaitingBookingsForRoom(Room room, BookingControl bookingControl) {
        ListInterface<Booking> matches = new DoublyLinkedList<>();
        if (room == null || bookingControl == null) return matches;

        ListInterface<Booking> waitingBookings = bookingControl.getWaitingBookings();

        for (int j = 1; j <= waitingBookings.getSize(); j++) {
            Booking booking = waitingBookings.getEntry(j);
            if (booking != null && matchesRoomType(room.getRoomType(), booking.getRoomType())) {
                matches.add(booking);
            }
        }

        return matches;
    }

    private static boolean matchesRoomType(int housekeepingRoomType, String bookingRoomType) {
        return RoomTypeUtil.matchesLabel(housekeepingRoomType, bookingRoomType);
    }

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

    public static boolean processGuestCheckOut(String roomNum, BookingControl bookingControl, StringBuilder resultMessage) {
        Room room = findRoom(roomNum);

        if (room == null) {
            resultMessage.append("Room ").append(roomNum).append(" not found.");
            return false;
        }

        boolean statusUpdated = guestCheckOut(room);
        if (!statusUpdated) {
            resultMessage.append("Room ").append(room.getRoomNum()).append(" is already marked Dirty (needs cleaning).");
            return false;
        }

        resultMessage.append("Room ").append(room.getRoomNum())
                .append(" checked out. Housekeeping Status updated to: Dirty (needs cleaning).\n");

        if (bookingControl != null) {
            Booking closedBooking = bookingControl.checkOutBookingByRoomID(room.getRoomNum());
            if (closedBooking != null) {
                resultMessage.append("Booking ").append(closedBooking.getBookingID())
                        .append(" (").append(closedBooking.getGuestName())
                        .append(") updated to: Checked Out.");
            } else {
                resultMessage.append("No active booking record found for Room ").append(room.getRoomNum()).append(".");
            }
        }

        return true;
    }
}