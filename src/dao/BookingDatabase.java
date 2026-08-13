package dao;

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Booking;

public class BookingDatabase {

    //====================================================
    // In-Memory Booking Data
    //====================================================
    private static ListInterface<Booking> waitingBookingData = new DoublyLinkedList<>();
    private static ListInterface<Booking> completedBookingData = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Booking Data
    //====================================================
    public static void createBookingData() {

        ListInterface<Booking> waitingBooking
                = new DoublyLinkedList<>();

        ListInterface<Booking> completedBooking
                = new DoublyLinkedList<>();

        //================================================
        // Waiting Booking
        //================================================
        waitingBooking.add(new Booking("B0001", "John Tan", "Big Room", "20/08/2026", "22/08/2026", "Waiting"));
        waitingBooking.add(new Booking("B0002", "Wong Lee", "Medium Room", "21/08/2026", "24/08/2026", "Waiting"));
        waitingBooking.add(new Booking("B0003", "Alice Lim", "Small Room", "25/08/2026", "27/08/2026", "Waiting"));
        waitingBooking.add(new Booking("B0004", "David Wong", "Big Room", "28/08/2026", "30/08/2026", "Waiting"));
        waitingBooking.add(new Booking("B0005", "Jason Lee", "Medium Room", "01/09/2026", "03/09/2026", "Waiting"));


        //================================================
        // Completed Booking
        //================================================

        completedBooking.add(new Booking("B0006", "Sarah Tan", "Small Room", "10/07/2026", "12/07/2026", "Completed"));
        completedBooking.add(new Booking("B0007", "Michael Chen", "Medium Room", "13/07/2026", "15/07/2026", "Completed"));
        completedBooking.add(new Booking("B0008", "Emily Wong", "Big Room", "16/07/2026", "18/07/2026", "Completed"));
        completedBooking.add(new Booking("B0009", "Kevin Lim", "Small Room", "19/07/2026", "21/07/2026", "Completed"));
        completedBooking.add(new Booking("B0010", "Jessica Ng", "Medium Room", "22/07/2026", "24/07/2026", "Completed"));

        BookingDatabase database = new BookingDatabase();

        database.saveToFile(
                waitingBooking,
                completedBooking
        );

        System.out.println("10 Booking Records Created In Memory!");
    }

    //====================================================
    // Save Booking Data In Memory
    //====================================================
    public void saveToFile(
            ListInterface<Booking> waitingBookingList,
            ListInterface<Booking> completedBookingList
    ) {

        waitingBookingData
                = copyBookingList(waitingBookingList);

        completedBookingData
                = copyBookingList(completedBookingList);

        System.out.println(
                "Booking Database Updated In Memory!"
        );
    }

    //====================================================
    // Get Waiting Booking
    //====================================================
    public ListInterface<Booking> getWaitingBooking() {

        return copyBookingList(waitingBookingData);
    }

    //====================================================
    // Get Completed Booking
    //====================================================
    public ListInterface<Booking> getCompletedBooking() {

        return copyBookingList(completedBookingData);
    }

    //====================================================
    // Add Waiting Booking
    //====================================================
    public boolean addWaitingBooking(Booking booking) {

        if (booking == null) {
            return false;
        }

        waitingBookingData.add(booking);

        return true;
    }

    //====================================================
    // Add Completed Booking
    //====================================================
    public boolean addCompletedBooking(Booking booking) {

        if (booking == null) {
            return false;
        }

        completedBookingData.add(booking);

        return true;
    }

    //====================================================
    // Move Booking To Completed List
    //====================================================
    public boolean completeBooking(String bookingID) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return false;
        }

        for (int i = 1;
                i <= waitingBookingData.getSize();
                i++) {

            Booking booking
                    = waitingBookingData.getEntry(i);

            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )) {

                waitingBookingData.remove(i);

                completedBookingData.add(booking);

                return true;
            }
        }

        return false;
    }

    //====================================================
    // Search Waiting Booking By ID
    //====================================================
    public Booking searchWaitingBookingByID(
            String bookingID
    ) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return null;
        }

        for (int i = 1;
                i <= waitingBookingData.getSize();
                i++) {

            Booking booking
                    = waitingBookingData.getEntry(i);

            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )) {

                return booking;
            }
        }

        return null;
    }

    //====================================================
    // Search Completed Booking By ID
    //====================================================
    public Booking searchCompletedBookingByID(
            String bookingID
    ) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return null;
        }

        for (int i = 1;
                i <= completedBookingData.getSize();
                i++) {

            Booking booking
                    = completedBookingData.getEntry(i);

            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )) {

                return booking;
            }
        }

        return null;
    }

    //====================================================
    // Remove Waiting Booking
    //====================================================
    public Booking removeWaitingBooking(int position) {

        if (position < 1
                || position > waitingBookingData.getSize()) {

            return null;
        }

        return waitingBookingData.remove(position);
    }

    //====================================================
    // Remove Completed Booking
    //====================================================
    public Booking removeCompletedBooking(int position) {

        if (position < 1
                || position > completedBookingData.getSize()) {

            return null;
        }

        return completedBookingData.remove(position);
    }

    //====================================================
    // Get Total Waiting Booking
    //====================================================
    public int getTotalWaitingBooking() {

        return waitingBookingData.getSize();
    }

    //====================================================
    // Get Total Completed Booking
    //====================================================
    public int getTotalCompletedBooking() {

        return completedBookingData.getSize();
    }

    //====================================================
    // Check Waiting Booking Is Empty
    //====================================================
    public boolean isWaitingBookingEmpty() {

        return waitingBookingData.isEmpty();
    }

    //====================================================
    // Check Completed Booking Is Empty
    //====================================================
    public boolean isCompletedBookingEmpty() {

        return completedBookingData.isEmpty();
    }

    //====================================================
    // Copy Booking List
    //====================================================
    private static ListInterface<Booking> copyBookingList(
            ListInterface<Booking> sourceList
    ) {

        ListInterface<Booking> copiedList
                = new DoublyLinkedList<>();

        if (sourceList == null) {
            return copiedList;
        }

        for (int i = 1;
                i <= sourceList.getSize();
                i++) {

            Booking booking
                    = sourceList.getEntry(i);

            if (booking != null) {
                copiedList.add(booking);
            }
        }

        return copiedList;
    }
}