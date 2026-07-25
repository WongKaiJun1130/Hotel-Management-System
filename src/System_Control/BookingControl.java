package System_Control;

import System_adt.ListInterface;
import System_adt.ArrayList;
import System_Entity.Booking;
import System_Entity.Guest;

public class BookingControl {

    private ListInterface<Booking> bookingList;

    public BookingControl() {
        bookingList = new ArrayList<>();
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
    
    public ListInterface<Booking> getBookingByGuest(String guestID) {
        ListInterface<Booking> result = new ArrayList<>();
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getGuestName().getGuestID().equals(guestID)) {
                result.add(book);
            }
        }
        return result;
    }

    public Booking getLatestBooking() {
        return bookingList.getLast();
    }

    public void setBookingList(ListInterface<Booking> list) {
        this.bookingList = list;
    }

    public boolean hasConflict(Guest guest, String date, String time) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking book = bookingList.getEntry(i);
            if (book.getGuest().equals(guest) &&
                book.getDate().equals(date) &&
                book.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }
}
