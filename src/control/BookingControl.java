package control;

import dao.BookingDao;
import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Booking;
import entity.Guest;
import java.util.Iterator;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class BookingControl {

    private ListInterface<Booking> bookingList = new DoublyLinkedList<>();
    private BookingDao bookingDatabase = new BookingDao();
    private static final int ROOMS_PER_TYPE = 10;
    private static final int ROOM_TYPE_COUNT = 3;
    private static final int TOTAL_ROOMS = ROOMS_PER_TYPE * ROOM_TYPE_COUNT;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String STATUS_WAITING = "Waiting";
    private static final String STATUS_SERVED = "Served";
  
    public BookingControl() {
        bookingList = bookingDatabase.getAllBookings();
    }
 
    public String generateBookingID() {
        return bookingDatabase.generateBookingID();
    }
    
    public boolean addBooking(Booking booking) {
        if (getBookingByID(booking.getBookingID()) != null) {
            return false;
        }
        booking.setRoomStatus(STATUS_WAITING);

        boolean added = bookingList.add(booking);
        if (added) {
            bookingDatabase.saveToFile(bookingList);
        }
        return added;
    }

    public boolean cancelBooking(String bookingID) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking.getBookingID().equalsIgnoreCase(bookingID)) {
                boolean removed = bookingList.remove(booking);
                if (removed) {
                    bookingDatabase.saveToFile(bookingList);
                }
                return removed;
            }
        }
        return false;
    }

    public Booking getBookingByID(String bookingID) {
     for (int i = 1; i <= bookingList.getSize(); i++) {
         Booking booking = bookingList.getEntry(i);

         if (booking != null && booking.getBookingID().equalsIgnoreCase(bookingID)) {
             return booking;
         }
     }
     return null;
 }

    public ListInterface<Booking> getAllBooking() {
        ListInterface<Booking> all = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking != null) {
                all.add(booking);
            }
        }
        return all;
    }

    public Booking getNextWaitingBooking() {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking =  bookingList.getEntry(i);
            if (booking != null && booking.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
                return booking;
            }
        }
        return null;
    }

   public Booking processNextReservation() {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking != null && booking.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
                booking.setRoomStatus(STATUS_SERVED);
                bookingDatabase.saveToFile(bookingList);
                return booking;
            }
        }
        return null;
    }
  
    public boolean hasConflict(Guest guest, String checkInDate, String checkOutDate) {
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking == null || !booking.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
                continue;
            }

            if (booking.getGuestName().equalsIgnoreCase(guest.getGuestName())) {
                if (checkInDate.compareTo(booking.getCheckOutDate()) < 0 && checkOutDate.compareTo(booking.getCheckInDate()) > 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public ListInterface<Booking> getWaitingBookings() {

        ListInterface<Booking> result = new DoublyLinkedList<>();
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking != null && booking.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
                result.add(booking);
            }
        }
        return result;
    }

    public ListInterface<Booking> getServedBookings() {
        ListInterface<Booking> result = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking != null && booking.getRoomStatus().equalsIgnoreCase(STATUS_SERVED)) {
                result.add(booking);
            }
        }
        return result;
    }

    public boolean updateBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking existing = bookingList.getEntry(i);

            if (existing != null && existing.getBookingID().equalsIgnoreCase(booking.getBookingID())) {
                bookingDatabase.saveToFile(bookingList);
                return true;
            }
        }
        return false;
    }

    public String assignRoomID(String roomType) {
        String prefix;
        if (roomType.equalsIgnoreCase("Single")) {
            prefix = "S";

        } else if (roomType.equalsIgnoreCase("Medium")) {
            prefix = "M";

        } else if (roomType.equalsIgnoreCase("Large")) {
            prefix = "L";

        } else {
            return null;
        }


        for (int i = 1; i <= ROOMS_PER_TYPE; i++) {
            String roomID = prefix + String.format("%02d", i);
            boolean used = false;

            for (int j = 1; j <= bookingList.getSize(); j++) {
                Booking booking = bookingList.getEntry(j);

                if (booking == null) {
                    continue;
                }

                if (booking.getRoomID() != null && booking.getRoomID().equalsIgnoreCase(roomID) && !booking.getRoomStatus().equalsIgnoreCase("Checked Out")) {
                    used = true;
                    break;
                }
            }
            if (!used) {
                return roomID;
            }
        }
        return null;
    }
    
   public ListInterface<Booking> getBookingSchedule() {
        return getAllBooking();
    }

   public ListInterface<Booking> getOccupiedRooms() {
        ListInterface<Booking> occupied = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (booking != null && booking.getRoomStatus().equalsIgnoreCase(STATUS_SERVED)) {
                occupied.add(booking);
            }
        }
        return occupied;
    }

    public ListInterface<Booking> getBookingHistory() {

        ListInterface<Booking> history = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);

            if (booking != null && !booking.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
                history.add(booking);
            }
        }
        return history;
    }
    
    public ListInterface<Booking> searchBooking(String keyword) {

        ListInterface<Booking> result = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {

            Booking booking = bookingList.getEntry(i);

            if (booking != null&& (booking.getBookingID().equalsIgnoreCase(keyword) || booking.getGuestName().toLowerCase().contains(keyword.toLowerCase()) || booking.getPhoneNumber().equals(keyword))) {
                result.add(booking);
            }
        }
        return result;
    }

    public ListInterface<Booking> getBookingsByGuestName(String guestName) {
        ListInterface<Booking> result = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {

            Booking booking = bookingList.getEntry(i);
            if (booking != null && booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }
        return result;
    }
    
   public ListInterface<Booking> getBookingsByPhoneNumber(String phoneNumber) {
        ListInterface<Booking> result = new DoublyLinkedList<>();

        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);

            if (booking != null && booking.getPhoneNumber().equals(phoneNumber)) {
                result.add(booking);
            }
        }
        return result;
    }
    
    public ListInterface<Booking> getBookingsByDate(LocalDate date) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        ListInterface<Booking> bookingList = getAllBooking();
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
            LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
            if (!date.isBefore(checkIn) && date.isBefore(checkOut)) {
                result.add(booking);
            }
        }
        return result;
    }
    
    public ListInterface<Booking> getBookingsByMonth(YearMonth yearMonth) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth();
        ListInterface<Booking> bookingList = getAllBooking();
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
            LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
            if (checkOut.isAfter(monthStart) && !checkIn.isAfter(monthEnd)) {
                result.add(booking);
            }
        }
        return result;
    }
  
    public long getBookingStayDays(Booking booking) {
        LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
        LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }
 
    public long getBookingStayDaysInMonth(Booking booking, YearMonth yearMonth) {
        LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
        LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth().plusDays(1);

        LocalDate start;
        if (checkIn.isBefore(monthStart)) {
            start = monthStart;
        } else {
            start = checkIn;
        }

        LocalDate end;
        if (checkOut.isAfter(monthEnd)) {
            end = monthEnd;
        } else {
            end = checkOut;
        }
        
        if (start.isBefore(end)) {
            return ChronoUnit.DAYS.between(start, end);
        }
        return 0;
    }
    
    public boolean hasBookingOnDate(LocalDate date) {
        return !getBookingsByDate(date).isEmpty();
    }

    public int getWaitingBookingCount() {
        return getWaitingBookings().getSize();
    }

    public int getServedBookingCount() {
        return getServedBookings().getSize();
    }
    
    
    //Total Available Days = 10 × 31 = 310 room-days
    public double getRoomOccupancyRate(YearMonth yearMonth, String roomType) {
        long bookedDays = getBookedRoomDays(yearMonth, roomType);
        int totalAvailableDays = ROOMS_PER_TYPE * yearMonth.lengthOfMonth();
        return ((double) bookedDays / totalAvailableDays) * 100;
    }
    
    public long getBookedRoomDays(YearMonth yearMonth, String roomType) {
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth().plusDays(1);

        long bookedDays = 0;
        for (int i = 1; i <= bookingList.getSize(); i++) {
            Booking booking = bookingList.getEntry(i);
            if (!booking.getRoomType().equalsIgnoreCase(roomType)) {
                continue;
            }
            LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
            LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
            
            LocalDate start;
            if (checkIn.isBefore(monthStart)) {
                start = monthStart;
            } else {
                start = checkIn;
            }

            LocalDate end;
            if (checkOut.isAfter(monthEnd)) {
                end = monthEnd;
            } else {
                end = checkOut;
            }
            if (start.isBefore(end)) {
                bookedDays += ChronoUnit.DAYS.between(start, end);
            }
        }
        return bookedDays;
    }
    
    public int getTotalRoomCount() {
        return TOTAL_ROOMS;
    }
    
    public int getRoomTypeCount() {
        return ROOM_TYPE_COUNT;
    }
    
    public void sortBookingByID(ListInterface<Booking> bookingList) {

        for (int i = 1; i <= bookingList.getSize(); i++) {
            for (int j = i + 1; j <= bookingList.getSize(); j++) {
                Booking booking1 = bookingList.getEntry(i);
                Booking booking2 = bookingList.getEntry(j);
                int id1 = Integer.parseInt(booking1.getBookingID().substring(1));
                int id2 = Integer.parseInt(booking2.getBookingID().substring(1));
                if (id1 > id2) {
                    bookingList.replace(i, booking2);
                    bookingList.replace(j, booking1);
                }
            }
        }
    }
    
    
    public Booking checkOutBookingByRoomID(String roomID) {
        Booking booking = getActiveServedBookingByRoomID(roomID);
        if (booking == null) {
            return null;
        }
        booking.setRoomStatus("Checked Out");
        bookingDatabase.saveToFile(bookingList);
        return booking;
    }

    public Booking getActiveServedBookingByRoomID(String roomID) {
        if (roomID == null) {
            return null;
        }
            for (int i = 1; i <= bookingList.getSize(); i++) {
                Booking booking = bookingList.getEntry(i);
                if (booking == null) {
                    continue;
                }

                if (booking.getRoomID() != null && booking.getRoomID().equalsIgnoreCase(roomID) && booking.getRoomStatus().equalsIgnoreCase(STATUS_SERVED)) {
                    return booking;
            }
        }
        return null;
    }
    

   public boolean checkInBooking(Booking booking, String roomID) {
        if (booking == null || roomID == null) {
            return false;
        }

        Booking existing = getBookingByID(booking.getBookingID());
        if (existing == null) {
            return false;
        }

        if (!existing.getRoomStatus().equalsIgnoreCase(STATUS_WAITING)) {
            return false;
        }
        existing.setRoomID(roomID);
        existing.setRoomStatus(STATUS_SERVED);
        bookingDatabase.saveToFile(bookingList);
        return true;
    }
}