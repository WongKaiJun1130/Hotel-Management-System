package System_Control;

import System_adt.ListInterface;
import System_adt.DoublyLinkedList;
import System_Entity.Booking;
import System_Entity.Guest;
import java.util.Iterator;

public class BookingControl {
    private ListInterface<Booking> bookingList;
    //==========================================================
    // Constructor
    //==========================================================
    public BookingControl() {
        bookingList = new DoublyLinkedList<>();
    }

    //==========================================================
    // Add Standard Reservation
    // Linear ADT Queue Enqueue
    //==========================================================
    public boolean addBooking(Booking booking) {
        if (getBookingByID(booking.getBookingID()) != null) {
            return false;
        }
        return bookingList.add(booking);
    }

    //==========================================================
    // Cancel Booking
    //==========================================================
    public boolean cancelBooking(String bookingID) {
        Booking booking = getBookingByID(bookingID);
        if(booking != null) {
            return bookingList.remove(booking);
        }
        return false;
    }

    //==========================================================
    // Get Booking By ID
    //==========================================================
    public Booking getBookingByID(String bookingID) {
        Iterator<Booking> iterator = bookingList.getIterator();
        while(iterator.hasNext()) {
            Booking booking = iterator.next();
            if(booking.getBookingID().equalsIgnoreCase(bookingID)) {
                return booking;
            }
        }
        return null;
    }

    //==========================================================
    // Get All Booking
    //==========================================================
    public ListInterface<Booking> getAllBooking() {
        return bookingList;
    }

    //==========================================================
    // FIFO Queue Peek
    // View Next Waiting Reservation
    //==========================================================
    public Booking getNextWaitingBooking() {
        if(bookingList.isEmpty()) {
            return null;
        }
        return bookingList.getEntry(1);
    }

    //==========================================================
    // FIFO Queue Dequeue
    // Process Next Reservation
    //==========================================================
    public Booking processNextReservation() {
        if(bookingList.isEmpty()) {
            return null;
        }
        Booking booking = bookingList.getEntry(1);
        booking.setRoomStatus("Checked-In");
        bookingList.remove(1);
        return booking;
    }

    //==========================================================
    // Get Latest Booking
    //==========================================================
    public Booking getLatestBooking() {
        if(bookingList.isEmpty()) {
            return null;
        }
        return bookingList.getEntry(bookingList.getSize());
    }

    //==========================================================
    // Search Booking By Guest Name
    //==========================================================
    public ListInterface<Booking> getBookingByGuest(String guestName) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        for(int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if(booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }
        return result;
    }

    //==========================================================
    // Update Booking List
    //==========================================================
    public void setBookingList(ListInterface<Booking> list) {
        this.bookingList = list;
    }

    //==========================================================
    // Check Booking Conflict
    //==========================================================
    public boolean hasConflict(Guest guest, String checkInDate, String checkOutDate) {
        for(int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if(booking.getGuestName().equalsIgnoreCase(guest.getGuestName())) {
                if(checkInDate.compareTo(booking.getCheckOutDate()) < 0
                &&
                checkOutDate.compareTo(booking.getCheckInDate()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    //==========================================================
    // Get All Waiting Booking
    //==========================================================
    public ListInterface<Booking> getWaitingBookings() {
        ListInterface<Booking> waitingList = new DoublyLinkedList<>();
        for(int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if(booking.getRoomStatus().equalsIgnoreCase("Waiting")) {
                waitingList.add(booking);
            }
        }
        return waitingList;
    }

    //==========================================================
    // Get All Completed Booking
    //==========================================================
    public ListInterface<Booking> getCompletedBookings() {
        ListInterface<Booking> completedList = new DoublyLinkedList<>();
        for(int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if(booking.getRoomStatus().equalsIgnoreCase("Completed")) {
                completedList.add(booking);
            }
        }
        return completedList;
    }
}