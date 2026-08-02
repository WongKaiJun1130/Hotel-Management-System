package dao;

import System_adt.DoublyLinkedList;
import System_Entity.Booking;

import java.io.*;

public class BookingDatabase {

    private final String fileName = "BookingDatabase.dat";

    //====================================================
    // Create Initial Booking Data
    //====================================================
    public static void createBookingData() {
        DoublyLinkedList<Booking> waitingBooking = new DoublyLinkedList<>();
        DoublyLinkedList<Booking> completedBooking = new DoublyLinkedList<>();

        //==============================
        // Waiting Booking
        //==============================
        waitingBooking.add(
            new Booking(
                "B0001",
                "John Tan",
                "Big Room",
                "20/08/2026",
                "22/08/2026",
                "Waiting"
            )
        );

        waitingBooking.add(
            new Booking(
                "B0002",
                "Wong Lee",
                "Medium Room",
                "21/08/2026",
                "24/08/2026",
                "Waiting"
            )
        );

        waitingBooking.add(
            new Booking(
                "B0003",
                "Alice Lim",
                "Small Room",
                "25/08/2026",
                "27/08/2026",
                "Waiting"
            )
        );

        waitingBooking.add(
            new Booking(
                "B0004",
                "David Wong",
                "Big Room",
                "28/08/2026",
                "30/08/2026",
                "Waiting"
            )
        );

        waitingBooking.add(
            new Booking(
                "B0005",
                "Jason Lee",
                "Medium Room",
                "01/09/2026",
                "03/09/2026",
                "Waiting"
            )
        );


        //==============================
        // Completed Booking
        //==============================
        completedBooking.add(
            new Booking(
                "B0006",
                "Sarah Tan",
                "Small Room",
                "10/07/2026",
                "12/07/2026",
                "Completed"
            )
        );


        completedBooking.add(
            new Booking(
                "B0007",
                "Michael Chen",
                "Medium Room",
                "13/07/2026",
                "15/07/2026",
                "Completed"
            )
        );


        completedBooking.add(
            new Booking(
                "B0008",
                "Emily Wong",
                "Big Room",
                "16/07/2026",
                "18/07/2026",
                "Completed"
            )
        );


        completedBooking.add(
            new Booking(
                "B0009",
                "Kevin Lim",
                "Small Room",
                "19/07/2026",
                "21/07/2026",
                "Completed"
            )
        );


        completedBooking.add(
            new Booking(
                "B0010",
                "Jessica Ng",
                "Medium Room",
                "22/07/2026",
                "24/07/2026",
                "Completed"
            )
        );

        BookingDatabase database = new BookingDatabase();
        database.saveToFile(waitingBooking, completedBooking);
        System.out.println("10 Booking Records Saved!");
    }
    
    //====================================================
    // Save Booking Data
    //====================================================
    public void saveToFile(DoublyLinkedList<Booking> waitingBooking, DoublyLinkedList<Booking> completedBooking){
        File file = new File(fileName);
        try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file))){
            output.writeObject(waitingBooking);
            output.writeObject(completedBooking);
            System.out.println("Booking Database Saved Successfully!");
            
        }catch(IOException ex){
            System.out.println("Cannot save booking database.");
        }
    }

    //====================================================
    // Load Waiting Booking
    //====================================================
    public DoublyLinkedList<Booking> getWaitingBooking(){
        DoublyLinkedList<Booking> waiting = new DoublyLinkedList<>();

        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))){
            waiting =(DoublyLinkedList<Booking>) input.readObject();
        }catch(Exception ex){
            System.out.println("No waiting booking database found.");
        }
        return waiting;
    }

    //====================================================
    // Load Completed Booking
    //====================================================
    public DoublyLinkedList<Booking> getCompletedBooking(){
        DoublyLinkedList<Booking> completed = new DoublyLinkedList<>();
        try(ObjectInputStream input = new ObjectInputStream(new FileInputStream(fileName))){
            input.readObject();
            completed = (DoublyLinkedList<Booking>) input.readObject();

        }catch(Exception ex){
            System.out.println("No completed booking database found.");
        }
        return completed;
    }
}
