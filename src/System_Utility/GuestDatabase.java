/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package System_Utility;

import System_Entity.Guest;
import java.io.*;
import System_adt.ArrayList;

public class GuestDatabase {

    private static final String FILE_NAME = "GuestDatabase.dat";
    // Save
    public static void saveGuests(ArrayList<Guest> guests){

        try{

            ObjectOutputStream out = new ObjectOutputStream( new FileOutputStream(FILE_NAME));
            out.writeObject(guests);
            out.close();
            System.out.println("Database Saved Successfully!");

        }
        catch(IOException e){
            e.printStackTrace();
        }

    }

    // Load
    public static ArrayList<Guest> loadGuests(){
        ArrayList<Guest> guests = new ArrayList<>();
        try{
            ObjectInputStream in =
                    new ObjectInputStream(
                            new FileInputStream(FILE_NAME)
                    );

            guests =
                    (ArrayList<Guest>) in.readObject();

            in.close();

        }
        catch(Exception e){
            System.out.println("Database Not Found.");
        }
        return guests;

    }

}