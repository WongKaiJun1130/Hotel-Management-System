package System_Control;

import dao.BookingDatabase;
import System_adt.ListInterface;
import System_adt.DoublyLinkedList;
import System_Entity.Booking;
import System_Entity.Guest;
import java.util.Iterator;

public class BookingControl {
    private DoublyLinkedList<Booking> waitingBookingList;
    private DoublyLinkedList<Booking> completedBookingList;
    private BookingDatabase bookingDatabase;
    
    //==========================================================
    // Constructor
    //==========================================================
    public BookingControl(){
        bookingDatabase = new BookingDatabase();
        
        waitingBookingList = bookingDatabase.getWaitingBooking();
        completedBookingList = bookingDatabase.getCompletedBooking();
        
        if(waitingBookingList == null){
            waitingBookingList = new DoublyLinkedList<>();
        }
            
        if(completedBookingList == null){
            completedBookingList = new DoublyLinkedList<>();
        }
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
        boolean result = waitingBookingList.add(booking);
        saveData();
        return result;
    }

    //==========================================================
    // Cancel Booking
    //==========================================================
    public boolean cancelBooking(String bookingID){
        for(int i=1; i<=waitingBookingList.getSize(); i++){
            Booking booking = waitingBookingList.getEntry(i);
            if(booking.getBookingID().equalsIgnoreCase(bookingID)){
                boolean result = waitingBookingList.remove(booking);
                saveData();
                return result;
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
        saveData();
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
    
    private void saveData(){
        bookingDatabase.saveToFile(waitingBookingList, completedBookingList);
    }
}