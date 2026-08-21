package dao;
/**
 *
 * @author Yeong Wei Kin
 */

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Booking;

public class BookingDao {

    
    private static ListInterface<Booking> bookingList = new DoublyLinkedList<>();


    
    public static void createBookingData() {

        bookingList = new DoublyLinkedList<>();


        
        bookingList.add(new Booking(
                "B0001",
                "John Tan",
                "0123456789",
                "R0001",
                "Large",
                "L01",
                "20-08-2026",
                "22-08-2026",
                "Served"
        ));

        bookingList.add(new Booking(
                "B0002",
                "Wong Lee",
                "0134567890",
                "R0002",
                "Medium",
                "M01",
                "21-08-2026",
                "24-08-2026",
                "Served"
        ));

        bookingList.add(new Booking(
                "B0003",
                "Alice Lim",
                "0145678901",
                "R0003",
                "Single",
                "S01",
                "25-08-2026",
                "27-08-2026",
                "Served"
        ));

        bookingList.add(new Booking(
                "B0004",
                "David Wong",
                "0166789012",
                "R0004",
                "Large",
                "L02",
                "28-08-2026",
                "30-08-2026",
                "Served"
        ));

        bookingList.add(new Booking(
                "B0005",
                "Jason Lee",
                "0177890123",
                "R0005",
                "Medium",
                "M02",
                "01-09-2026",
                "03-09-2026",
                "Served"
        ));


        
        bookingList.add(new Booking(
                "B0006",
                "Sarah Tan",
                "0188901234",
                "R0006",
                "Single",
                "S02",
                "10-07-2026",
                "12-07-2026",
                "Waiting"
        ));

        bookingList.add(new Booking(
                "B0007",
                "Michael Chen",
                "0199012345",
                "R0007",
                "Medium",
                "M03",
                "13-07-2026",
                "15-07-2026",
                "Waiting"
        ));

        bookingList.add(new Booking(
                "B0008",
                "Emily Wong",
                "0121122334",
                "R0008",
                "Large",
                "L03",
                "16-07-2026",
                "18-07-2026",
                "Waiting"
        ));

        bookingList.add(new Booking(
                "B0009",
                "Kevin Lim",
                "0132233445",
                "R0009",
                "Single",
                "S03",
                "19-07-2026",
                "21-07-2026",
                "Waiting"
        ));

        bookingList.add(new Booking(
                "B0010",
                "Jessica Ng",
                "0143344556",
                "R0010",
                "Medium",
                "M04",
                "22-07-2026",
                "24-07-2026",
                "Waiting"
        ));


        System.out.println(
                bookingList.getSize()
                + " Booking Created In Memory!"
        );
    }


    
    public void saveToFile(
            ListInterface<Booking> bookings) {

        bookingList =
                copyBookingList(bookings);
    }


    
    public ListInterface<Booking> getAllBookings() {

        return copyBookingList(
                bookingList
        );
    }


    
    public ListInterface<Booking> getWaitingBooking() {

        ListInterface<Booking> result =
                new DoublyLinkedList<>();


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getRoomStatus()
                            .equalsIgnoreCase("Waiting")) {

                result.add(booking);
            }
        }


        return result;
    }


    
    public ListInterface<Booking> getServedBooking() {

        ListInterface<Booking> result =
                new DoublyLinkedList<>();


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getRoomStatus()
                            .equalsIgnoreCase("Served")) {

                result.add(booking);
            }
        }


        return result;
    }


    
    public boolean addWaitingBooking(
            Booking booking) {

        if (booking == null) {
            return false;
        }


        booking.setRoomStatus(
                "Waiting"
        );

        bookingList.add(
                booking
        );


        return true;
    }


    
    public boolean addServedBooking(
            Booking booking) {

        if (booking == null) {
            return false;
        }


        booking.setRoomStatus(
                "Served"
        );

        bookingList.add(
                booking
        );


        return true;
    }


    
    public boolean servedBooking(
            String bookingID) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return false;
        }


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )
                    && booking.getRoomStatus()
                            .equalsIgnoreCase(
                                    "Waiting"
                            )) {

                booking.setRoomStatus(
                        "Served"
                );

                return true;
            }
        }


        return false;
    }


    
    public Booking searchWaitingBookingByID(
            String bookingID) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return null;
        }


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )
                    && booking.getRoomStatus()
                            .equalsIgnoreCase(
                                    "Waiting"
                            )) {

                return booking;
            }
        }


        return null;
    }


    
    public Booking searchServedBookingByID(
            String bookingID) {

        if (bookingID == null
                || bookingID.trim().isEmpty()) {

            return null;
        }


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getBookingID()
                            .equalsIgnoreCase(
                                    bookingID.trim()
                            )
                    && booking.getRoomStatus()
                            .equalsIgnoreCase(
                                    "Served"
                            )) {

                return booking;
            }
        }


        return null;
    }


    
    public Booking removeWaitingBooking(
            int position) {

        int count = 0;


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getRoomStatus()
                            .equalsIgnoreCase(
                                    "Waiting"
                            )) {

                count++;


                if (count == position) {

                    return bookingList.remove(
                            i
                    );
                }
            }
        }


        return null;
    }


    
    public Booking removeServedBooking(
            int position) {

        int count = 0;


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking != null
                    && booking.getRoomStatus()
                            .equalsIgnoreCase(
                                    "Served"
                            )) {

                count++;


                if (count == position) {

                    return bookingList.remove(
                            i
                    );
                }
            }
        }


        return null;
    }


    
    public int getTotalWaitingBooking() {

        return getWaitingBooking()
                .getSize();
    }


    
    public int getTotalServedBooking() {

        return getServedBooking()
                .getSize();
    }


    
    public boolean isWaitingBookingEmpty() {

        return getWaitingBooking()
                .isEmpty();
    }


    
    public boolean isServedBookingEmpty() {

        return getServedBooking()
                .isEmpty();
    }


    
    public String generateBookingID() {

        int maxID = 0;


        for (int i = 1;
             i <= bookingList.getSize();
             i++) {

            Booking booking =
                    bookingList.getEntry(i);


            if (booking == null) {
                continue;
            }


            String id =
                    booking.getBookingID();


            if (id != null
                    && id.matches("B\\d{4}")) {

                try {

                    int number =
                            Integer.parseInt(
                                    id.substring(1)
                            );


                    if (number > maxID) {

                        maxID =
                                number;
                    }

                } catch (
                        NumberFormatException e) {

                    // Ignore invalid ID
                }
            }
        }


        return String.format(
                "B%04d",
                maxID + 1
        );
    }


    
    private static ListInterface<Booking>
            copyBookingList(
                    ListInterface<Booking> sourceList) {

        ListInterface<Booking> copiedList =
                new DoublyLinkedList<>();


        if (sourceList == null) {

            return copiedList;
        }


        for (int i = 1;
             i <= sourceList.getSize();
             i++) {

            Booking booking =
                    sourceList.getEntry(i);


            if (booking != null) {

                copiedList.add(
                        booking
                );
            }
        }


        return copiedList;
    }
}