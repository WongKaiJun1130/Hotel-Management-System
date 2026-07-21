/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package System_UI;

/**
 *
 * @author user
 */


import System_Entity.Guest;
import System_adt.LinkedQueue;
import System_Utility.Utility;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Iterator;
import java.util.Scanner;



public class VIPAllocationUI {


    private static LinkedQueue<Guest> vipQueue = new LinkedQueue<>();
    private static Scanner input = new Scanner(System.in);
    private static int nextGuestID = 1;

    public static void menu(){
        UtilityMenu();
    }

    private static void UtilityMenu(){
        Utility.customMenu(
                new String[]{
                    "[1] Add VIP Guest",
                    "[2] Upgrade Guest To VIP",
                    "[3] Allocate Room",
                    "[4] View Next Priority Guest",
                    "[5] Display VIP Queue",
                    "[6] Search Guest",
                    "[0] Back"
                },
                "VIP & Loyalty Tier Priority Room Allocation",
                "Select option: ",
                new Runnable[]{
                    () -> addGuest(),
                    () -> upgradeGuest(),
                    () -> allocateRoom(),
                    () -> viewNextGuest(),
                    () -> displayQueue(),
                    () -> searchGuest(),
                    () -> MainUI.MainUI()
                }


        );


    }

    // ==============================
    // 1. Add VIP Guest
    // ==============================

    private static void addGuest(){

        String roomType;
        String id = generateGuestID();
        String tier;
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              ADD VIP GUEST                   |");
        System.out.println("+----------------------------------------------+");


        // =========================
        // Guest ID Validation
        // =========================
        System.out.println("Guest ID :" + id);
        
        // =========================
        // Guest Name
        // =========================

        System.out.print("Guest Name: ");
        String name = input.nextLine();



        // =========================
        // Loyalty Tier
        // =========================
        while(true){
            
            System.out.println("---------------------------------------------------------");
            System.out.println("|                     Loyalty Tier                      |");
            System.out.println("---------------------------------------------------------");
            System.out.println("| [1] Elite | [2] Diamond | [3] Platinum | [4] Standard |");
            System.out.println("---------------------------------------------------------");
            System.out.print("Select Tier: ");
            String choice = input.nextLine();


            switch(choice){

                case "1":
                    tier = "Elite";
                    break;

                case "2":
                    tier = "Diamond";
                    break;

                case "3":
                    tier = "Platinum";
                    break;

                case "4":
                    tier = "Standard";
                    break;

                default:
                    System.out.println("Invalid choice! Please enter 1-4.");
                    continue;
            }

            break;
        }



        // =========================
        // Room Information
        // =========================
        while(true){

            System.out.println("-------------------");
            System.out.println("|    Room Type    |");
            System.out.println("-------------------");
            System.out.println("| [1] Small Room  |");
            System.out.println("| [2] Medium Room |");
            System.out.println("| [3] Big Room    |");
            System.out.println("-------------------");
            System.out.print("Select Room Type [1-3]: ");
            String choice = input.nextLine();
            switch(choice){

                case "1":
                    roomType = "Small Room";
                    break;


                case "2":
                    roomType = "Medium Room";
                    break;


                case "3":
                    roomType = "Big Room";
                    break;


                default:
                    System.out.println("Invalid choice! Please select 1-3.");
                    continue;

            }


            break;

        }


        String roomStatus = "Waiting";

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String checkInDate =today.format(formatter);
        System.out.println();
        System.out.println("Default Check-In Date: " + checkInDate);
        String changeDate;
        while(true){
            System.out.print("Change Check-In Date? (Y/N): ");
            changeDate = input.nextLine();
            if(changeDate.equalsIgnoreCase("Y") || changeDate.equalsIgnoreCase("N")){
                break;
            }
            else{
                System.out.println(
                    "Invalid choice! Please enter Y or N."
                );
            }
        }



        if(changeDate.equalsIgnoreCase("Y")){

            while(true){
                System.out.print( "Enter New Check-In Date (DD/MM/YYYY): ");
                String newDate = input.nextLine();

                // Date format validation
                if(newDate.matches("\\d{2}/\\d{2}/\\d{4}")){
                    checkInDate = newDate;
                    break;
                }
                else{
                    System.out.println( "Invalid date format! Example: 20/07/2026"
                    );
                }
            }
        }
        else if(changeDate.equalsIgnoreCase("N")){
            System.out.println( "Using default check-in date.");
        }

            // =========================
            // Create Guest Object
            // =========================

            Guest guest = new Guest(id, name, tier, roomType, roomStatus, checkInDate);
            System.out.println();
            System.out.println("+----------------------------------------------+");
            System.out.println("|              GUEST INFORMATION               |");
            System.out.println("+----------------------------------------------+");
            System.out.printf("| %-18s : %-23s |\n", "Guest ID", guest.getGuestID());
            System.out.printf("| %-18s : %-23s |\n", "Guest Name", guest.getGuestName());
            System.out.printf("| %-18s : %-23s |\n", "Loyalty Tier", guest.getLoyaltyTier());
            System.out.printf("| %-18s : %-23d |\n", "Priority", guest.getPriority());
            System.out.printf("| %-18s : %-23s |\n", "Room Type", guest.getRoomType());
            System.out.printf("| %-18s : %-23s |\n", "Room Status", guest.getRoomStatus());
            System.out.printf("| %-18s : %-23s |\n", "Check-In Date", guest.getCheckInDate());
            System.out.println("+----------------------------------------------+");

            // =========================
            // Confirmation
            // =========================
            while(true){

                System.out.println();
                System.out.println("------------------------------");
                System.out.println("|The VIP Guest is Successful?|");
                System.out.println("------------------------------");
                System.out.println("|       [1] Yes   [2] No     |");
                System.out.println("------------------------------");
                System.out.print("Select option: ");
                String confirm = input.nextLine();
                if(confirm.equals("1")){
                    insertPriority(guest);
                    nextGuestID++;
                    System.out.println();
                    System.out.println("VIP Guest Added Successfully!");
                    break;
                }
                else if(confirm.equals("2")){
                    System.out.println();
                    System.out.println("Add VIP Guest Cancelled!");
                    break;
                }
                else{
                    System.out.println("Invalid choice! Please select 1 or 2." );
                }

            }
    }



    private static String generateGuestID(){
        return String.format("R%04d" , nextGuestID);
    }
    
    // Priority Queue insertion
    private static void insertPriority(Guest guest){
        LinkedQueue<Guest> temp = new LinkedQueue<>();
        boolean inserted = false;
        Iterator<Guest> iterator = vipQueue.getIterator();
        while(iterator.hasNext()){
            Guest current = iterator.next();
            if(!inserted && guest.getPriority() > current.getPriority()){
                temp.enqueue(guest);
                inserted = true;
            }
            temp.enqueue(current);
        }
        if(!inserted){
            temp.enqueue(guest);
        }
        vipQueue = temp;
    }



     // ==============================
     // 2. Upgrade Guest To VIP
     // ==============================

     private static void upgradeGuest(){
         System.out.println();
         System.out.println("+----------------------------------------------+");
         System.out.println("|              UPGRADE VIP GUEST               |");
         System.out.println("+----------------------------------------------+");
         System.out.print("Enter Guest ID: ");
         String id = input.nextLine();
         Guest guest = searchGuestByID(id);
         if(guest == null){
             System.out.println();
             System.out.println( "Guest not found!" );
             return;
         }
         String oldTier = guest.getLoyaltyTier();
         int oldPriority = guest.getPriority();
         // =========================
         // Display Current Guest Info
         // =========================
         System.out.println();
         System.out.println("+------------------------------------------------------------+");
         System.out.println("|                  CURRENT GUEST INFORMATION                 |");
         System.out.println("+------------------------------------------------------------+");
         System.out.printf("| %-18s : %-37s |\n", "Guest ID",guest.getGuestID());
         System.out.printf("| %-18s : %-37s |\n","Guest Name",guest.getGuestName() );
         System.out.printf("| %-18s : %-37s |\n", "Current Tier", oldTier);
         System.out.printf("| %-18s : %-37d |\n", "Priority", oldPriority );
         System.out.println("+------------------------------------------------------------+");
         // =========================
         // Select New Tier
         // =========================
         String newTier;
         while(true){
             System.out.println();
             System.out.println("---------------------------------------------------------");
             System.out.println("|                  NEW Loyalty Tier                     |");
             System.out.println("---------------------------------------------------------");
             System.out.println("| [1] Elite | [2] Diamond | [3] Platinum | [4] Standard |");
             System.out.println("---------------------------------------------------------");
             System.out.println("| Select New Loyalty Tier: |");
             System.out.println("----------------------------");
             System.out.print( "Enter Choice: " );
             String choice = input.nextLine();
             switch(choice){
                 case "1":
                     newTier = "Elite";
                     break;
                 case "2":
                     newTier = "Diamond";
                     break;
                 case "3":
                     newTier = "Platinum";
                     break;
                 case "4":
                     newTier = "Standard";
                     break;
                 default:
                     System.out.println(
                         "Invalid choice! Please select 1-4."
                     );
                     continue;
             }
             break;
         }
         // =========================
         // Confirmation
         // =========================


         System.out.println();
         System.out.println("Upgrade " + guest.getGuestName() + " to " + newTier + "?");
         System.out.println("[1] Confirm");
         System.out.println("[2] Cancel");
         System.out.print("Select option: ");
         String confirm = input.nextLine();
         if(confirm.equals("1")){
             
             // Update tier
             guest.setLoyaltyTier(newTier);
             
             // =========================
             // Re-sort Priority Queue
             // =========================
             LinkedQueue<Guest> temp = new LinkedQueue<>();
             Iterator<Guest> iterator = vipQueue.getIterator();
             while(iterator.hasNext()){
                 temp.enqueue(
                         iterator.next()
                 );
             }
             vipQueue = new LinkedQueue<>();
             while(!temp.isEmpty()){
                 insertPriority( temp.dequeue());
             }
             System.out.println();
             System.out.println("+------------------------------------------------------------+");
             System.out.println("|             NEW UPGRADE GUEST INFORMATION                  |");
             System.out.println("+------------------------------------------------------------+");
             System.out.printf("| %-18s : %-37s |\n", "Guest ID",guest.getGuestID());
             System.out.printf("| %-18s : %-37s |\n","Guest Name",guest.getGuestName() );
             System.out.printf("| %-18s : %-37s |\n", "New Tier", guest.getLoyaltyTier());
             System.out.printf("| %-18s : %-37d |\n", "Priority", guest.getPriority() );
             System.out.println("+------------------------------------------------------------+");
             System.out.println( "Upgrade Successful!" );         }
         else{
             System.out.println();
             System.out.println(
                 "Upgrade Cancelled!"
             );
         }
     }








    // ==============================
    // 3. Allocate Room
    // ==============================

    private static void allocateRoom(){



        Guest guest =
                vipQueue.dequeue();



        if(guest == null){


            System.out.println(
            "No waiting guest."
            );


        }
        else{


            System.out.println(
            "Room Assigned To:"
            );


            System.out.println(guest);


        }


    }










    // ==============================
    // 4. View Next Priority Guest
    // ==============================

    private static void viewNextGuest(){



        Guest guest =
                vipQueue.getFront();




        if(guest == null){


            System.out.println(
            "No waiting guest."
            );


        }
        else{


            System.out.println(
            "Next Priority Guest:"
            );


            System.out.println(guest);


        }


    }









    // ==============================
    // 5. display Guest
    // ==============================
   private static void displayQueue() {

        Iterator<Guest> iterator = vipQueue.getIterator();

        if (!iterator.hasNext()) {
            System.out.println("\nVIP Queue is Empty.");
            return;
        }


        System.out.println("====================================================================================================================");
        System.out.printf("|%55s%-59s|\n", "Customer VIP Queue", "");
        System.out.println("====================================================================================================================");
        System.out.printf("| %-3s | %-10s | %-20s | %-15s | %-8s | %-12s | %-12s | %-12s |\n",
                "No", "Guest ID", "Guest Name ", "Loyalty Tier" , "Priority" , "Room Type" , "Status" , "Check-In");
        System.out.println("====================================================================================================================");

        int count = 1;

        while (iterator.hasNext()) {

            Guest guest = iterator.next();

            System.out.printf("| %-3d | %-10s | %-20s | %-15s | %-8d | %-12s | %-12s | %-12s |\n",                    
                    count,
                    guest.getGuestID(),
                    guest.getGuestName(),
                    guest.getLoyaltyTier(),
                    guest.getPriority(),
                    guest.getRoomType(),
                    guest.getRoomStatus(),
                    guest.getCheckInDate());

            count++;
        }

        System.out.println("--------------------------------------------------------------------------------------------------------------------");
        System.out.printf("| Total VIP Guests : %-93d |\n", count - 1);
        System.out.println("--------------------------------------------------------------------------------------------------------------------");

}








    // ==============================
    // 6. Search Guest
    // ==============================

    private static void searchGuest() {

        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              SEARCH VIP GUEST                |");
        System.out.println("+----------------------------------------------+");
        System.out.print("Enter Search Keyword: ");
        String keyword = input.nextLine();
        searchGuestObject(keyword);

    }








     // ==============================
     // Search Guest Object By Name
     // ==============================

     private static void searchGuestObject(String keyword) {
        Iterator<Guest> iterator = vipQueue.getIterator();
        boolean found = false;
        int count = 1;



        while(iterator.hasNext()){


            Guest guest = iterator.next();



            String searchData =
                    guest.getGuestID() + " "
                    + guest.getGuestName() + " "
                    + guest.getLoyaltyTier() + " "
                    + guest.getRoomType() + " "
                    + guest.getRoomStatus() + " "
                    + guest.getCheckInDate();



            // Search all information
            if(searchData.toLowerCase()
                    .contains(keyword.toLowerCase())){


                if(!found){


                    System.out.println();
                    System.out.println("=====================================================================================================================");
                    System.out.printf("| %-3s | %-10s | %-20s | %-15s | %-8s | %-12s | %-12s | %-12s |\n",
                            "No",
                            "Guest ID",
                            "Guest Name",
                            "Loyalty Tier",
                            "Priority",
                            "Room Type",
                            "Status",
                            "Check-In");
                    System.out.println("=====================================================================================================================");


                    found = true;

                }



                System.out.printf("| %-3d | %-10s | %-20s | %-15s | %-8d | %-12s | %-12s | %-12s |\n",
                        count,
                        guest.getGuestID(),
                        guest.getGuestName(),
                        guest.getLoyaltyTier(),
                        guest.getPriority(),
                        guest.getRoomType(),
                        guest.getRoomStatus(),
                        guest.getCheckInDate());


                count++;


            }


        }

        if(!found){


            System.out.println();
            System.out.println("----------------------------------------------");
            System.out.println("|             Guest not found!               |");
            System.out.println("----------------------------------------------");


        }
        else{


            System.out.println("=====================================================================================================================");

            System.out.println(
                    "Total Matching Guests : " + (count - 1)
            );

            System.out.println("=====================================================================================================================");

        }

    }
     
 
    private static Guest searchGuestByID(String id) {

        Iterator<Guest> iterator = vipQueue.getIterator();


        while(iterator.hasNext()) {

            Guest guest = iterator.next();


            if(guest.getGuestID()
                    .equalsIgnoreCase(id)) {

                return guest;

            }

        }


        return null;
    }



}