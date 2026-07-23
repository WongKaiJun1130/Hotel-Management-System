


// want to save booking objects into a file
public class Booking  {
    private String bookingID;
    private String guestName;
    private String roomType;
    private String checkInDate;
    private String checkOutDate;
    private String roomStatus;

     //Constructor
    public Booking(String bookingID,
                   String guestName,
                   String roomType,
                   String checkInDate,
                   String checkOutDate,
                   String roomStatus) {

    this.bookingID = bookingID;
    this.guestName = guestName;
    this.roomType = roomType;
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

    public String getRoomType() {
        return roomType;
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
    
     public void setRoomType(String roomType) {
        this.roomType = roomType;
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
                + "\nRoom Type: " + roomType
                + "\nCheck-In Date: " + checkInDate
                + "\nCheck-Out Date: " + checkOutDate
                + "\nRoom Status: " + roomStatus;
    }
}
    

   