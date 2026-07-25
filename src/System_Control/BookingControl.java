package System_Control;

import System_adt.ListInterface;
import System_adt.DoublyLinkedList;
import System_Entity.Booking;
import System_Entity.Guest;

public class BookingControl {

    private ListInterface<Booking> bookingList;

    public BookingControl() {
        bookingList = new DoublyLinkedList<>();    
    }

    public boolean addBooking(Booking booking) {
        if (searchBooking(booking.getBookingID()) != null) {
            return false;
        }
        return bookingList.add(booking);
    }

    public boolean cancelBooking(String bookingID) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getBookingID().equals(bookingID)) {
                return bookingList.remove(book);
            }
        }
        return false;
    }

    public Booking searchBooking(String bookingID) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getBookingID().equals(bookingID)) {
                return book;
            }
        }
        return null;
    }

    public ListInterface<Booking> getAllBooking() {
        return bookingList;
    }
    
    public ListInterface<Booking> getBookingByGuest(String guestName) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getGuestName().equals(guestName)) {
                result.add(book);
            }
        }
        return result;
    }

    public Booking getLatestBooking() {
        if (bookingList.isEmpty()) {
            return null;
        }
        return bookingList.getEntry(bookingList.getSize());
    }

    public void setBookingList(ListInterface<Booking> list) {
        this.bookingList = list;
    }

    public boolean hasConflict(Guest guest, String checkInDate, String checkOutDate) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getGuestName().equals(guest.getGuestName()) &&
                book.getCheckInDate().equals(checkInDate) &&
                book.getCheckOutDate().equals(checkOutDate)) {
                return true;
            }
        }
        return false;
    }
}
