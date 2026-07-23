package dao;

import System_Entity.Guest;
import System_adt.*;
import java.io.*;

public class GuestDatabase {


    private String fileName = "GuestDatabase.dat";
    public static void createGuestData() {
        DoublyLinkedList.ArrayList<Guest> guests = new DoublyLinkedList.ArrayList<>();

        guests.add(new Guest("R0001","John Tan","Elite","Big Room","Waiting","20/07/2026"));
        guests.add(new Guest("R0002","Wong Lee","Diamond","Middle Room","Waiting","21/07/2026"));
        guests.add(new Guest("R0003","Alice Lim","Platinum","Small Room","Waiting","22/07/2026"));
        guests.add(new Guest("R0004","David Wong","Elite","Big Room","Waiting","23/07/2026"));
        guests.add(new Guest("R0005","Jason Lee","Standard","Small Room","Waiting","24/07/2026"));
        guests.add(new Guest("R0006","Sarah Tan","Diamond","Middle Room","Waiting","25/07/2026"));
        guests.add(new Guest("R0007","Michael Chen","Platinum","Middle Room","Waiting","26/07/2026"));
        guests.add(new Guest("R0008","Emily Wong","Elite","Big Room","Waiting","27/07/2026"));
        guests.add(new Guest("R0009","Kevin Lim","Standard","Small Room","Waiting","28/07/2026"));
        guests.add(new Guest("R0010","Jessica Ng","Diamond","Middle Room","Waiting","29/07/2026"));
        GuestDatabase dao = new GuestDatabase();
        dao.saveToFile(guests);
        
        
        System.out.println( guests.getNumberOfEntries() + " Guests Saved!" );
    }
    //====================================================
    // Save Guest Data
    //====================================================
    public void saveToFile(DoublyLinkedList.ArrayList<Guest> guestList) {


        File file = new File(fileName);


        try {

            ObjectOutputStream ooStream =
                    new ObjectOutputStream(
                            new FileOutputStream(file)
                    );


            ooStream.writeObject(guestList);


            ooStream.close();


            System.out.println("\nGuest Database Saved Successfully!");


        } 
        catch (FileNotFoundException ex) {

            System.out.println("\nFile not found.");

        } 
        catch (IOException ex) {

            System.out.println("\nCannot save guest database.");

        }

    }



    //====================================================
    // Load Guest Data
    //====================================================
    public DoublyLinkedList.ArrayList<Guest> retrieveFromFile() {


        File file = new File(fileName);


        DoublyLinkedList.ArrayList<Guest> guestList =
                new DoublyLinkedList.ArrayList<>();


        try {


            ObjectInputStream oiStream =
                    new ObjectInputStream(
                            new FileInputStream(file)
                    );


            guestList =
                    (DoublyLinkedList.ArrayList<Guest>)
                    oiStream.readObject();


            oiStream.close();


        }
        catch (FileNotFoundException ex) {


            System.out.println("\nNo Guest Database Found.");


        }
        catch (IOException ex) {


            System.out.println("\nCannot read guest database.");


        }
        catch (ClassNotFoundException ex) {


            System.out.println("\nClass not found.");

        }


        return guestList;

    }

}