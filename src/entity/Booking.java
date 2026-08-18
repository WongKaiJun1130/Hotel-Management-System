package entity;

import java.io.Serializable;

// want to save booking objects into a file
public class Booking implements Serializable {    
    
    private static final long serialVersionUID = 1L;
    
    private String bookingID;
    private String guestName;
    private String phoneNumber;
    private String guestID;
    private String roomType;
    private String roomID;
    private String checkInDate;
    private String checkOutDate;
    private String roomStatus;

     //Constructor
    public Booking(String bookingID,
                   String guestName,
                   String phoneNumber,
                   String guestID,
                   String roomType,
                   String roomID,
                   String checkInDate,
                   String checkOutDate,
                   String roomStatus) {

    this.bookingID = bookingID;
    this.guestName = guestName;
    this.phoneNumber = phoneNumber;
    this.guestID = guestID;
    this.roomType = roomType;
    this.roomID = roomID;
    this.checkInDate = checkInDate;
    this.checkOutDate = checkOutDate;
    this.roomStatus = roomStatus;
}
    
    //getter
     public String getBookingID() {
        return bookingID;
    }
    
    public String getGuestName() {
        return guestName;
    }
   
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public String getGuestID() {
        return guestID;
    }

    public String getRoomType() {
        return roomType;
    }
    
     public String getRoomID() {
        return roomID;
    }

    public String getCheckInDate() {
        return checkInDate;
    }
    
        public String getCheckOutDate() {
        return checkOutDate;
    }

     public String getRoomStatus() {
        return roomStatus;
    }

    public void confirm() {
        roomStatus = "Confirmed";
    }

    public void cancel() {
        roomStatus = "Cancelled";
    }

    
    //setter
    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }
    
    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public void setGuestID(String guestID) {
        this.guestID = guestID;
    }

     public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
    
     public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
     
    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }
    
    public void setRoomStatus(String roomStatus) {
        this.roomStatus = roomStatus;
    }
    
    @Override
    public String toString() {
        return "Booking ID: " + bookingID
             + "\nGuest Name: " + guestName
             + "\nPhone Number: " + phoneNumber
             + "\nGuest ID: " + guestID
             + "\nRoom Type: " + roomType
             + "\nRoom ID: " + roomID
             + "\nCheck-In Date: " + checkInDate
             + "\nCheck-Out Date: " + checkOutDate
             + "\nRoom Status: " + roomStatus;
    }
}
    

   