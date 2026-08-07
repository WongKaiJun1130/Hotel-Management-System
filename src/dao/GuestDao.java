package dao;

import Adt.DoublyLinkedList;
import Entity.Guest;
import java.io.*;

public class GuestDao {

    private String fileName = "GuestDatabase.dat";

    public static void createGuestData() {

        DoublyLinkedList.ArrayList<Guest> guests = new DoublyLinkedList.ArrayList<>();

        guests.add(new Guest("R0001", "John Tan", "0123456789", "Elite", "Big Room", "Waiting", "20/07/2026", "20/07/2026 10:30"));
        guests.add(new Guest("R0002", "Wong Lee", "0134567890", "Diamond", "Medium Room", "Waiting", "21/07/2026", "21/07/2026 08:45"));
        guests.add(new Guest("R0003", "Alice Lim", "0145678901", "Platinum", "Small Room", "Waiting", "22/07/2026", "22/07/2026 09:15"));
        guests.add(new Guest("R0004", "David Wong", "0166789012", "Elite", "Big Room", "Waiting", "23/07/2026", "23/07/2026 09:00"));
        guests.add(new Guest("R0005", "Jason Lee", "0177890123", "Standard", "Small Room", "Waiting", "24/07/2026", "24/07/2026 07:30"));
        guests.add(new Guest("R0006", "Sarah Tan", "0188901234", "Diamond", "Medium Room", "Waiting", "25/07/2026", "25/07/2026 10:00"));
        guests.add(new Guest("R0007", "Michael Chen", "0199012345", "Platinum", "Medium Room", "Waiting", "26/07/2026", "26/07/2026 08:30"));
        guests.add(new Guest("R0008", "Emily Wong", "0121122334", "Elite", "Big Room", "Waiting", "27/07/2026", "27/07/2026 11:00"));
        guests.add(new Guest("R0009", "Kevin Lim", "0132233445", "Standard", "Small Room", "Waiting", "28/07/2026", "28/07/2026 09:30"));
        guests.add(new Guest("R0010", "Jessica Ng", "0143344556", "Diamond", "Medium Room", "Waiting", "29/07/2026", "29/07/2026 08:15"));

        GuestDao dao = new GuestDao();
        dao.saveToFile(guests);

        System.out.println(guests.getNumberOfEntries() + " Guests Saved!");
    }

    //====================================================
    // Save Guest Data
    //====================================================
    public void saveToFile(DoublyLinkedList.ArrayList<Guest> guestList) {

        File file = new File(fileName);

        try {

            ObjectOutputStream ooStream = new ObjectOutputStream(new FileOutputStream(file));

            ooStream.writeObject(guestList);

            ooStream.close();

            System.out.println("\nGuest Database Saved Successfully!");

        } catch (FileNotFoundException ex) {

            System.out.println("\nFile not found.");

        } catch (IOException ex) {

            System.out.println("\nCannot save guest database.");
        }
    }

    //====================================================
    // Load Guest Data
    //====================================================
    public DoublyLinkedList.ArrayList<Guest> retrieveFromFile() {

        File file = new File(fileName);

        DoublyLinkedList.ArrayList<Guest> guestList = new DoublyLinkedList.ArrayList<>();

        try {

            ObjectInputStream oiStream = new ObjectInputStream(new FileInputStream(file));

            guestList = (DoublyLinkedList.ArrayList<Guest>) oiStream.readObject();

            oiStream.close();

        } catch (FileNotFoundException ex) {

            System.out.println("\nNo Guest Database Found.");

        } catch (IOException ex) {

            System.out.println("\nCannot read guest database.");

        } catch (ClassNotFoundException ex) {

            System.out.println("\nClass not found.");
        }

        return guestList;
    }
}