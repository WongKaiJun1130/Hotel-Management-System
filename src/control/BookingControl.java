package control;

import dao.BookingDatabase;
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

    private ListInterface<Booking> waitingBookingList = new DoublyLinkedList<>();
    private ListInterface<Booking> servedBookingList = new DoublyLinkedList<>();
    private BookingDatabase bookingDatabase = new BookingDatabase();
    private static final int ROOMS_PER_TYPE = 10;
    private static final int ROOM_TYPE_COUNT = 3;
    private static final int TOTAL_ROOMS = ROOMS_PER_TYPE * ROOM_TYPE_COUNT;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final String STATUS_WAITING = "Waiting";
    private static final String STATUS_SERVED = "Served";
  
    public BookingControl() {
        waitingBookingList = bookingDatabase.getWaitingBooking();
        servedBookingList = bookingDatabase.getServedBooking();
    }
 
    public String generateBookingID() {
        return bookingDatabase.generateBookingID();
    }
    
    //==========================================================
    // Add Standard Reservation
    // Linear ADT Queue Enqueue
    //==========================================================
    public boolean addBooking(Booking booking) {
        if(getBookingByID(booking.getBookingID()) != null){
            return false;
        }
        booking.setRoomStatus(STATUS_WAITING);
        return waitingBookingList.add(booking);
    }

    //==========================================================
    // Cancel Booking
    //==========================================================
    public boolean cancelBooking(String bookingID){
        for(int i=1; i<=waitingBookingList.getSize(); i++){
            Booking booking = waitingBookingList.getEntry(i);
            if(booking.getBookingID().equalsIgnoreCase(bookingID)){
               return waitingBookingList.remove(booking);
            }
        }
        return false;
    }

    //==========================================================
    // Get Booking By ID
    //==========================================================
    public Booking getBookingByID(String bookingID){
        Iterator<Booking> waiting = waitingBookingList.getIterator();
            while(waiting.hasNext()){
            Booking booking = waiting.next();
                if(booking.getBookingID().equalsIgnoreCase(bookingID)){
                    return booking;
                }
            }
        Iterator<Booking> served = servedBookingList.getIterator();
            while(served.hasNext()){
            Booking booking = served.next();
                if(booking.getBookingID().equalsIgnoreCase(bookingID)){
                    return booking;
                }
            }
        return null;
    }

    //==========================================================
    // Get All Booking
    //==========================================================
    public ListInterface<Booking> getAllBooking(){
    ListInterface<Booking> all = new DoublyLinkedList<>();
        for(int i=1; i<=waitingBookingList.getSize(); i++){
            all.add(waitingBookingList.getEntry(i));
        }
        for(int i=1; i<=servedBookingList.getSize(); i++){
            all.add(servedBookingList.getEntry(i));
        }
        return all;
    }

    //==========================================================
    // FIFO Queue Peek
    // View Next Waiting Reservation
    //==========================================================
    public Booking getNextWaitingBooking() {
        if(waitingBookingList.isEmpty()) {
            return null;
        }
        return waitingBookingList.getEntry(1);
    }

    //==========================================================
    // FIFO Queue Dequeue
    // Process Next Reservation
    //==========================================================
    public Booking processNextReservation() {
        if(waitingBookingList.isEmpty()){
            return null;
        }
        Booking booking = waitingBookingList.getEntry(1);
        waitingBookingList.remove(1);
        booking.setRoomStatus(STATUS_SERVED);
        servedBookingList.add(booking);
        return booking;
    }
  
    //==========================================================
    // Booking Conflict
    //==========================================================
    public boolean hasConflict(
            Guest guest,
            String checkInDate,
            String checkOutDate){
        for(int i=1;i<=waitingBookingList.getSize();i++){
            Booking booking = waitingBookingList.getEntry(i);
            
            if(booking.getGuestName().equalsIgnoreCase(guest.getGuestName())){
                if(checkInDate.compareTo(booking.getCheckOutDate()) < 0 && 
                   checkOutDate.compareTo(booking.getCheckInDate()) > 0){
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
        return waitingBookingList;
    }

    //==========================================================
    // Get All Served Booking
    //==========================================================
    public ListInterface<Booking> getServedBookings() {
        return servedBookingList;
    }

    //==========================================================
    // Update Booking
    //==========================================================
    public boolean updateBooking(Booking booking){
        boolean found = false;

        // Check waiting booking list
        for(int i = 1; i <= waitingBookingList.getSize(); i++){
            Booking temp = waitingBookingList.getEntry(i);
            if(temp.getBookingID().equalsIgnoreCase(booking.getBookingID())){
                found = true;
                break;
            }
        }
        // Check served booking list
        if(!found){
            for(int i = 1; i <= servedBookingList.getSize(); i++){
                Booking temp = servedBookingList.getEntry(i);
                if(temp.getBookingID().equalsIgnoreCase(booking.getBookingID())){
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
    
    //==========================================================
    // Generate Room ID
    // 30 Hotel Rooms
    // Single  : S01 - S10
    // Medium  : M01 - M10
    // Large   : L01 - L10
    //==========================================================
    public String assignRoomID(String roomType) {
        String prefix;
        if (roomType.equalsIgnoreCase("Single")) {
            prefix = "S";
        }
        else if (roomType.equalsIgnoreCase("Medium")) {
            prefix = "M";
        }
        else if (roomType.equalsIgnoreCase("Large")) {
            prefix = "L";
        }
        else {
            return null;
        }

        for (int i = 1; i <= ROOMS_PER_TYPE; i++) {
            String roomID = prefix + String.format("%02d", i);
            boolean used = false;
            // Check waiting bookings
            for (int j = 1; j <= waitingBookingList.getSize(); j++) {
                Booking booking = waitingBookingList.getEntry(j);
                if (booking.getRoomID() != null && booking.getRoomID().equalsIgnoreCase(roomID)) {
                    used = true;
                    break;
                }
            }
            // Check served bookings
            if (!used) {
                for (int j = 1; j <= servedBookingList.getSize(); j++) {
                    Booking booking = servedBookingList.getEntry(j);
                    if (booking.getRoomID() != null && booking.getRoomID().equalsIgnoreCase(roomID)) {
                        used = true;
                        break;
                    }
                }
            }
            if (!used) {
                return roomID;
            }
        }
        return null;
    }
    
    //==========================================================
    //  Get Booking Schedule
    //==========================================================
    public ListInterface<Booking> getBookingSchedule() {
        ListInterface<Booking> schedule = new DoublyLinkedList<>();

        // Waiting bookings
        for (int i = 1; i <= waitingBookingList.getSize(); i++) {
            Booking booking = waitingBookingList.getEntry(i);
            schedule.add(booking);
        }
        // Served bookings
        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            schedule.add(booking);
        }
        return schedule;
    }

    //==========================================================
    //  Get Occupied Rooms
    //==========================================================
    public ListInterface<Booking> getOccupiedRooms() {
        ListInterface<Booking> occupied = new DoublyLinkedList<>();

        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            if (booking.getRoomStatus() != null && booking.getRoomStatus().equalsIgnoreCase("Served")) {
                occupied.add(booking);
            }
        }
        return occupied;
    }

    //==========================================================
    //  Get Booking History
    //==========================================================
    public ListInterface<Booking> getBookingHistory() {
        ListInterface<Booking> history = new DoublyLinkedList<>();

        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            history.add(servedBookingList.getEntry(i));
        }
        return history;
    }
    
    public ListInterface<Booking> searchBooking(String keyword) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        // Search waiting bookings
        for (int i = 1; i <= waitingBookingList.getSize(); i++) {
            Booking booking = waitingBookingList.getEntry(i);
            if (booking.getBookingID().equalsIgnoreCase(keyword)
                    ||booking.getGuestName().toLowerCase().contains(keyword.toLowerCase())
                    || booking.getPhoneNumber().equals(keyword)) {
                result.add(booking);
            }
        }
        // Search served bookings
        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            if (booking.getBookingID().equalsIgnoreCase(keyword)
                    || booking.getGuestName().toLowerCase().contains(keyword.toLowerCase())
                    || booking.getPhoneNumber().equals(keyword)) {
                result.add(booking);
            }
        }
        return result;
    }

    //==========================================================
    //  Search Booking By Guest Name
    //==========================================================
    public ListInterface<Booking> getBookingsByGuestName(String guestName) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        // Search waiting bookings
        for (int i = 1; i <= waitingBookingList.getSize(); i++) {
            Booking booking = waitingBookingList.getEntry(i);
            if (booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }
        // Search served bookings
        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            if (booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }
        return result;
    }
    
    public ListInterface<Booking> getBookingsByPhoneNumber(String phoneNumber) {
        ListInterface<Booking> result = new DoublyLinkedList<>();
        // Search waiting bookings
        for (int i = 1; i <= waitingBookingList.getSize(); i++) {
            Booking booking = waitingBookingList.getEntry(i);
            if (booking.getPhoneNumber().equals(phoneNumber)) {
                result.add(booking);
            }
        }
        // Search served bookings
        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            if (booking.getPhoneNumber().equals(phoneNumber)) {
                result.add(booking);
            }
        }
        return result;
    }
    
     //==========================================================
    // Get Bookings By Date
    //==========================================================
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
    
    //==========================================================
    // Get Bookings By Month
    //==========================================================
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
  
    //==========================================================
    // Calculate Booking Stay Days
    //==========================================================
    public long getBookingStayDays(Booking booking) {
        LocalDate checkIn = LocalDate.parse(booking.getCheckInDate(), DATE_FORMATTER);
        LocalDate checkOut = LocalDate.parse(booking.getCheckOutDate(), DATE_FORMATTER);
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }
    
    //==========================================================
    // Calculate Booking Stay Days Within Selected Month
    //==========================================================
    public long getBookingStayDaysInMonth(
            Booking booking,
            YearMonth yearMonth) {

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
    
    //==========================================================
    // Check If Date Has Booking
    //==========================================================
    public boolean hasBookingOnDate(LocalDate date) {
        return !getBookingsByDate(date).isEmpty();
    }

    //==========================================================
    //  Get Number Of Waiting Bookings
    //==========================================================
    public int getWaitingBookingCount() {
        return waitingBookingList.getSize();
    }

    //==========================================================
    //  Get Number Of Served Bookings
    //==========================================================
    public int getServedBookingCount() {
        return servedBookingList.getSize();
    }
    
    
    //Total Available Days = 10 × 31 = 310 room-days
    //==========================================================
    // Calculate Monthly Room Occupancy Rate
    //==========================================================
    public double getRoomOccupancyRate(YearMonth yearMonth, String roomType) {
        long bookedDays = getBookedRoomDays(yearMonth, roomType);
        int totalAvailableDays = ROOMS_PER_TYPE * yearMonth.lengthOfMonth();
        return ((double) bookedDays / totalAvailableDays) * 100;
    }
    
    //==========================================================
    // Get Total Booked Room Days By Month And Room Type
    //==========================================================
    public long getBookedRoomDays(YearMonth yearMonth, String roomType) {
        LocalDate monthStart = yearMonth.atDay(1);
        LocalDate monthEnd = yearMonth.atEndOfMonth().plusDays(1);

        long bookedDays = 0;
        ListInterface<Booking> bookingList = getAllBooking();
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
    
    //==========================================================
    // Sort Booking List By Booking ID
    //==========================================================
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
    
    //==========================================================
    // Check Out Booking By Room ID
    //==========================================================
    public Booking checkOutBookingByRoomID(String roomID) {
        for (int i = 1; i <= servedBookingList.getSize(); i++) {
            Booking booking = servedBookingList.getEntry(i);
            if (booking.getRoomID().equalsIgnoreCase(roomID) && booking.getRoomStatus().equalsIgnoreCase("Served")) {
                booking.setRoomStatus("Checked Out");
                return booking;
            }
        }
        return null;
    }
}
