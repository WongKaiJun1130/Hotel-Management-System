package dao;

import Adt.DoublyLinkedList;
import Adt.ListInterface;
import Entity.Booking;

public class BookingDao {

    //====================================================
    // Initialize Waiting Booking Data
    //====================================================
    public ListInterface<Booking> initializeWaitingBookingDAO() {

        ListInterface<Booking> waitingBooking = new DoublyLinkedList<>();

        waitingBooking.add(new Booking(
                "B0001",
                "John Tan",
                "VIP Room",
                "20/08/2026",
                "22/08/2026",
                "Waiting"));

        waitingBooking.add(new Booking(
                "B0002",
                "Wong Lee",
                "Deluxe Room",
                "21/08/2026",
                "24/08/2026",
                "Waiting"));

        waitingBooking.add(new Booking(
                "B0003",
                "Alice Lim",
                "Normal Room",
                "25/08/2026",
                "27/08/2026",
                "Waiting"));

        waitingBooking.add(new Booking(
                "B0004",
                "David Wong",
                "VIP Room",
                "28/08/2026",
                "30/08/2026",
                "Waiting"));

        waitingBooking.add(new Booking(
                "B0005",
                "Jason Lee",
                "Deluxe Room",
                "01/09/2026",
                "03/09/2026",
                "Waiting"));

        return waitingBooking;
    }

    //====================================================
    // Initialize Completed Booking Data
    //====================================================
    public ListInterface<Booking> initializeCompletedBookingDAO() {

        ListInterface<Booking> completedBooking = new DoublyLinkedList<>();

        completedBooking.add(new Booking(
                "B0006",
                "Sarah Tan",
                "Normal Room",
                "10/07/2026",
                "12/07/2026",
                "Completed"));

        completedBooking.add(new Booking(
                "B0007",
                "Michael Chen",
                "Deluxe Room",
                "13/07/2026",
                "15/07/2026",
                "Completed"));

        completedBooking.add(new Booking(
                "B0008",
                "Emily Wong",
                "VIP Room",
                "16/07/2026",
                "18/07/2026",
                "Completed"));

        completedBooking.add(new Booking(
                "B0009",
                "Kevin Lim",
                "Normal Room",
                "19/07/2026",
                "21/07/2026",
                "Completed"));

        completedBooking.add(new Booking(
                "B0010",
                "Jessica Ng",
                "Deluxe Room",
                "22/07/2026",
                "24/07/2026",
                "Completed"));

        return completedBooking;
    }
    
    public void createBookingData() {
        ListInterface<Booking> waitingBooking = initializeWaitingBookingDAO();
        ListInterface<Booking> completedBooking = initializeCompletedBookingDAO();
        System.out.println("10 Booking Records Created!");
    }
}