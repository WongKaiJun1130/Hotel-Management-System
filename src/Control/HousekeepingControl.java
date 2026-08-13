/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Control;

import Adt.ListInterface;
import Adt.DoublyLinkedList;
import Entity.StatusEntry;
import Utility.RoomStatusUtil;
import Entity.Room;
import dao.RoomDao;
import java.util.Iterator;
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
    private static final long AUTO_ADVANCE_INTERVAL_MILLIS = 15 * 1000; // TESTING: 15 seconds (set to 15 * 60 * 1000 for the real 15 min)

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
}