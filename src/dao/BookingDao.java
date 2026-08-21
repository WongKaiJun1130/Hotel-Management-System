package dao;
/**
 *
 * @author Yeong Wei Kin
 */

import adt.DoublyLinkedList;
import adt.ListInterface;
import entity.Booking;

public class BookingDao {

    //====================================================
    // In-Memory Booking Data
    //====================================================
    private static ListInterface<Booking> bookingList = new DoublyLinkedList<>();


    //====================================================
    // Create Initial Booking Data
    //====================================================
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
            "B0011",
            "Daniel Lim",
            "0112233445",
            "R0011",
            "Large",
            "L03",
            "03-08-2026",
            "05-08-2026",
            "Served"
    ));

    bookingList.add(new Booking(
            "B0012",
            "Michelle Tan",
            "0113344556",
            "R0012",
            "Medium",
            "M03",
            "07-08-2026",
            "09-08-2026",
            "Served"
    ));

    bookingList.add(new Booking(
            "B0013",
            "Andrew Lee",
            "0114455667",
            "R0013",
            "Large",
            "L04",
            "12-08-2026",
            "14-08-2026",
            "Served"
    ));

    bookingList.add(new Booking(
            "B0014",
            "Samantha Wong",
            "0115566778",
            "R0014",
            "Single",
            "S04",
            "18-08-2026",
            "20-08-2026",
            "Served"
    ));

    bookingList.add(new Booking(
            "B0015",
            "Brian Ng",
            "0116677889",
            "R0015",
            "Medium",
            "M05",
            "05-09-2026",
            "07-09-2026",
            "Served"
    ));


    //================================================
    // Waiting Booking
    //================================================

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
            "M04",
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
            "L05",
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
            "M06",
            "22-07-2026",
            "24-07-2026",
            "Waiting"
    ));

    bookingList.add(new Booking(
            "B0016",
            "Rachel Lee",
            "0117788990",
            "R0016",
            "Single",
            "S05",
            "20-08-2026",
            "22-08-2026",
            "Waiting"
    ));

    bookingList.add(new Booking(
            "B0017",
            "Steven Wong",
            "0118899001",
            "R0017",
            "Large",
            "L06",
            "22-08-2026",
            "24-08-2026",
            "Waiting"
    ));

    bookingList.add(new Booking(
            "B0018",
            "Nicole Tan",
            "0119900112",
            "R0018",
            "Medium",
            "M07",
            "24-08-2026",
            "26-08-2026",
            "Waiting"
    ));

    bookingList.add(new Booking(
            "B0019",
            "Aaron Lim",
            "0121011223",
            "R0019",
            "Large",
            "L07",
            "27-08-2026",
            "29-08-2026",
            "Waiting"
    ));

    bookingList.add(new Booking(
            "B0020",
            "Chloe Lee",
            "0132122334",
            "R0020",
            "Single",
            "S06",
            "08-09-2026",
            "10-09-2026",
            "Waiting"
    ));


    System.out.println(
            bookingList.getSize()
            + " Booking Created In Memory!"
    );
}


    //====================================================
    // Save Booking Data In Memory
    //====================================================
    public void saveToFile(
            ListInterface<Booking> bookings) {

        bookingList =
                copyBookingList(bookings);
    }


    //====================================================
    // Get All Booking
    //====================================================
    public ListInterface<Booking> getAllBookings() {

        return copyBookingList(
                bookingList
        );
    }


    //====================================================
    // Get Waiting Booking
    //====================================================
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


    //====================================================
    // Get Served Booking
    //====================================================
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


    //====================================================
    // Add Waiting Booking
    //====================================================
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


    //====================================================
    // Add Served Booking
    //====================================================
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


    //====================================================
    // Change Waiting Booking To Served
    //====================================================
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


    //====================================================
    // Search Waiting Booking By ID
    //====================================================
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


    //====================================================
    // Search Served Booking By ID
    //====================================================
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


    //====================================================
    // Remove Waiting Booking
    //====================================================
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


    //====================================================
    // Remove Served Booking
    //====================================================
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


    //====================================================
    // Get Total Waiting Booking
    //====================================================
    public int getTotalWaitingBooking() {

        return getWaitingBooking()
                .getSize();
    }


    //====================================================
    // Get Total Served Booking
    //====================================================
    public int getTotalServedBooking() {

        return getServedBooking()
                .getSize();
    }


    //====================================================
    // Check Waiting Booking Is Empty
    //====================================================
    public boolean isWaitingBookingEmpty() {

        return getWaitingBooking()
                .isEmpty();
    }


    //====================================================
    // Check Served Booking Is Empty
    //====================================================
    public boolean isServedBookingEmpty() {

        return getServedBooking()
                .isEmpty();
    }


    //====================================================
    // Generate Next Booking ID
    //====================================================
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


    //====================================================
    // Copy Booking List
    //====================================================
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