package control;

import dao.BookingDatabase;
import adt.ListInterface;
import adt.DoublyLinkedList;
import entity.Booking;
import entity.Guest;
import java.util.Iterator;

public class BookingControl {

    private ListInterface<Booking> waitingBookingList = new DoublyLinkedList<>();
    private ListInterface<Booking> completedBookingList = new DoublyLinkedList<>();
    private BookingDatabase bookingDatabase = new BookingDatabase();

    public BookingControl() {
        waitingBookingList = bookingDatabase.getWaitingBooking();
        completedBookingList = bookingDatabase.getCompletedBooking();
    }
 
    //==========================================================
    // Add Standard Reservation
    // Linear ADT Queue Enqueue
    //==========================================================
    public boolean addBooking(Booking booking) {
        if(getBookingByID(booking.getBookingID()) != null){
            return false;
        }
        booking.setRoomStatus("Waiting");
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
        Iterator<Booking> completed = completedBookingList.getIterator();
            while(completed.hasNext()){
            Booking booking = completed.next();
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
        for(int i=1; i<=completedBookingList.getSize(); i++){
            all.add(completedBookingList.getEntry(i));
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
        booking.setRoomStatus("Completed");
        completedBookingList.add(booking);
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
    // Get All Completed Booking
    //==========================================================
    public ListInterface<Booking> getCompletedBookings() {
        return completedBookingList;
    }

    //==========================================================
    // Update Booking
    //==========================================================
    public boolean updateBooking(Booking booking){
        boolean found = false;

        // Check waiting list
        for(int i = 1; i <= waitingBookingList.getSize(); i++){
            Booking temp = waitingBookingList.getEntry(i);
            if(temp.getBookingID().equalsIgnoreCase(booking.getBookingID())){
                found = true;
                break;
            }
        }
        // Check completed list
        if(!found){
            for(int i = 1; i <= completedBookingList.getSize(); i++){
                Booking temp = completedBookingList.getEntry(i);
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

        for (int i = 1; i <= 10; i++) {
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
            // Check completed bookings
            if (!used) {
                for (int j = 1; j <= completedBookingList.getSize(); j++) {
                    Booking booking = completedBookingList.getEntry(j);
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

        // Completed bookings
        for (int i = 1; i <= completedBookingList.getSize(); i++) {
            Booking booking = completedBookingList.getEntry(i);
            schedule.add(booking);
        }

        return schedule;
    }

    //==========================================================
    //  Get Occupied Rooms
    //==========================================================
    public ListInterface<Booking> getOccupiedRooms() {

        ListInterface<Booking> occupied = new DoublyLinkedList<>();

        for (int i = 1; i <= completedBookingList.getSize(); i++) {

            Booking booking = completedBookingList.getEntry(i);

            if (booking.getRoomStatus() != null
                    && booking.getRoomStatus().equalsIgnoreCase("Completed")) {

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

        for (int i = 1; i <= completedBookingList.getSize(); i++) {

            history.add(completedBookingList.getEntry(i));
        }

        return history;
    }

    //==========================================================
    //  Search Booking By Guest Name
    //==========================================================
    public ListInterface<Booking> getBookingsByGuestName(String guestName) {

        ListInterface<Booking> result = new DoublyLinkedList<>();

        for (int i = 1; i <= waitingBookingList.getSize(); i++) {

            Booking booking = waitingBookingList.getEntry(i);

            if (booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }

        for (int i = 1; i <= completedBookingList.getSize(); i++) {

            Booking booking = completedBookingList.getEntry(i);

            if (booking.getGuestName().equalsIgnoreCase(guestName)) {
                result.add(booking);
            }
        }

        return result;
    }

    //==========================================================
    //  Get Number Of Waiting Bookings
    //==========================================================
    public int getWaitingBookingCount() {
        return waitingBookingList.getSize();
    }

    //==========================================================
    //  Get Number Of Completed Bookings
    //==========================================================
    public int getCompletedBookingCount() {
        return completedBookingList.getSize();
    }
}
