package dao;

import Adt.DoublyLinkedList;
import Adt.ListInterface;
import Entity.Booking;

public class BookingDao {

    //====================================================
    // In-Memory Booking Data
    //====================================================
    private static ListInterface<Booking> waitingBookingData = new DoublyLinkedList<>();
    private static ListInterface<Booking> servedBookingData = new DoublyLinkedList<>();

    //====================================================
    // Create Initial Booking Data
    //====================================================
    public static void createBookingData() {

        ListInterface<Booking> waitingBooking = new DoublyLinkedList<>();

        ListInterface<Booking> servedBooking = new DoublyLinkedList<>();

        //================================================
        // Served Booking
        //================================================
        servedBooking.add(new Booking("B0001", "John Tan", "0123456789", "R0001", "Large", "L01","20-08-2026", "22-08-2026", "Served"));
        servedBooking.add(new Booking("B0002", "Wong Lee", "0134567890", "R0002", "Medium", "M01","21-08-2026", "24-08-2026", "Served"));
        servedBooking.add(new Booking("B0003", "Alice Lim", "0145678901", "R0003", "Single", "S01","25-08-2026", "27-08-2026", "Served"));
        servedBooking.add(new Booking("B0004", "David Wong", "0166789012", "R0004", "Large", "L02", "28-08-2026", "30-08-2026", "Served"));
        servedBooking.add(new Booking("B0005", "Jason Lee", "0177890123", "R0005", "Medium", "M02", "01-09-2026", "03-09-2026", "Served"));

        //================================================
        // Waiting Booking
        //================================================
        waitingBooking.add(new Booking("B0006", "Sarah Tan", "0188901234", "R0006",  "Single", "S02", "10-07-2026", "12-07-2026", "Waiting"));
        waitingBooking.add(new Booking("B0007", "Michael Chen", "0199012345", "R0007", "Medium", "M03", "13-07-2026", "15-07-2026", "Waiting"));
        waitingBooking.add(new Booking("B0008", "Emily Wong", "0121122334", "R0008", "Large", "L03", "16-07-2026", "18-07-2026", "Waiting"));
        waitingBooking.add(new Booking("B0009", "Kevin Lim", "0132233445", "R0009", "Single", "S03", "19-07-2026", "21-07-2026", "Waiting"));
        waitingBooking.add(new Booking("B0010", "Jessica Ng", "0143344556", "R0010", "Medium", "M04", "22-07-2026", "24-07-2026", "Waiting"));
        
        BookingDao bookingDao = new BookingDao();

        bookingDao.saveToFile(waitingBooking,servedBooking);   

        System.out.println(waitingBookingData.getSize() + servedBookingData.getSize() + " Booking Created In Memory!");
    }

    //====================================================
    // Save Booking Data In Memory
    //====================================================
    public void saveToFile(ListInterface<Booking> waitingBookingList, ListInterface<Booking> servedBookingList) {
        waitingBookingData = copyBookingList(waitingBookingList);
        servedBookingData = copyBookingList(servedBookingList);
        System.out.println("Booking Database Updated In Memory!");
    }
        

    //====================================================
    // Get Waiting Booking
    //====================================================
    public ListInterface<Booking> getWaitingBooking() {

        return copyBookingList(waitingBookingData);
    }

    //====================================================
    // Get Served Booking
    //====================================================
    public ListInterface<Booking> getServedBooking() {
        return copyBookingList(servedBookingData);
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
    // Add Served Booking
    //====================================================
    public boolean addServedBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        servedBookingData.add(booking);
        return true;
    }

    //====================================================
    // Move Booking To Served List
    //====================================================
    public boolean servedBooking(String bookingID) {
        if (bookingID == null || bookingID.trim().isEmpty()) {
            return false;
        }
        for (int i = 1; i <= waitingBookingData.getSize(); i++) {
            Booking booking = waitingBookingData.getEntry(i);

            if (booking != null && booking.getBookingID().equalsIgnoreCase(bookingID.trim())) {
                waitingBookingData.remove(i);
                servedBookingData.add(booking);
                return true;
            }
        }
        return false;
    }

    //====================================================
    // Search Waiting Booking By ID
    //====================================================
    public Booking searchWaitingBookingByID(String bookingID) {
        if (bookingID == null || bookingID.trim().isEmpty()) {
            return null;
        }

        for (int i = 1; i <= waitingBookingData.getSize(); i++) {
            Booking booking = waitingBookingData.getEntry(i);
            if (booking != null && booking.getBookingID().equalsIgnoreCase(bookingID.trim())) {
                return booking;
            }
        }
        return null;
    }

    //====================================================
    // Search Served Booking By ID
    //====================================================
    public Booking searchServedBookingByID(String bookingID) {
        if (bookingID == null || bookingID.trim().isEmpty()) {
            return null;
        }
        for (int i = 1; i <= servedBookingData.getSize(); i++) {
            Booking booking = servedBookingData.getEntry(i);
            if (booking != null && booking.getBookingID().equalsIgnoreCase(bookingID.trim())) {
                return booking;
            }
        }
        return null;
    }

    //====================================================
    // Remove Waiting Booking
    //====================================================
    public Booking removeWaitingBooking(int position) {
        if (position < 1 || position > waitingBookingData.getSize()) {
            return null;
        }
        return waitingBookingData.remove(position);
    }

    //====================================================
    // Remove Served Booking
    //====================================================
    public Booking removeServedBooking(int position) {
        if (position < 1 || position > servedBookingData.getSize()) {
            return null;
        }
        return servedBookingData.remove(position);
    }

    //====================================================
    // Get Total Waiting Booking
    //====================================================
    public int getTotalWaitingBooking() {
        return waitingBookingData.getSize();
    }

    //====================================================
    // Get Total Served Booking
    //====================================================
    public int getTotalServedBooking() {
        return servedBookingData.getSize();
    }

    //====================================================
    // Check Waiting Booking Is Empty
    //====================================================
    public boolean isWaitingBookingEmpty() {
        return waitingBookingData.isEmpty();
    }

    //====================================================
    // Check Served Booking Is Empty
    //====================================================
    public boolean isServedBookingEmpty() {
        return servedBookingData.isEmpty();
    }

    //====================================================
    // Copy Booking List
    //====================================================
    private static ListInterface<Booking> copyBookingList(ListInterface<Booking> sourceList) {
        ListInterface<Booking> copiedList = new DoublyLinkedList<>();
        if (sourceList == null) {
            return copiedList;
        }
        for (int i = 1; i <= sourceList.getSize(); i++) {
            Booking booking = sourceList.getEntry(i);
            if (booking != null) {
                copiedList.add(booking);
            }
        }
        return copiedList;
    }
    
    //====================================================
    // Generate Next Booking ID
    //====================================================
    public String generateBookingID() {
        int maxID = 0;
        // Check waiting bookings
        for (int i = 1; i <= waitingBookingData.getSize(); i++) {
            Booking booking = waitingBookingData.getEntry(i);

            if (booking != null) {
                String id = booking.getBookingID();

                if (id != null && id.startsWith("B")) {
                    try {
                        int number = Integer.parseInt(id.substring(1));

                        if (number > maxID) {
                            maxID = number;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid Booking ID
                    }
                }
            }
        }

        // Check served bookings
        for (int i = 1; i <= servedBookingData.getSize(); i++) {
            Booking booking = servedBookingData.getEntry(i);

            if (booking != null) {
                String id = booking.getBookingID();

                if (id != null && id.startsWith("B")) {
                    try {
                        int number = Integer.parseInt(id.substring(1));

                        if (number > maxID) {
                            maxID = number;
                        }
                    } catch (NumberFormatException e) {
                        // Ignore invalid Booking ID
                    }
                }
            }
        }
        return String.format("B%04d", maxID + 1);
    }
}