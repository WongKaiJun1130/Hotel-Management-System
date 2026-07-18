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

import java.util.Iterator;
import java.util.Scanner;



public class VIPAllocationUI {


    private static LinkedQueue<Guest> vipQueue = new LinkedQueue<>();
    private static Scanner input = new Scanner(System.in);

    public static void menu(){
        UtilityMenu();
    }

    private static void UtilityMenu(){
        Utility.customMenu(
                new String[]{
                    "1) Add VIP Guest",
                    "2) Upgrade Guest To VIP",
                    "3) Allocate Room",
                    "4) View Next Priority Guest",
                    "5) Display VIP Queue",
                    "6) Search Guest",
                    "0) Back"
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
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              ADD VIP GUEST                   |");
        System.out.println("+----------------------------------------------+");
        String id;
        while(true){
            System.out.print("Guest ID (RXXXX): " );
            id = input.nextLine();
            if(id.matches("R\\d{4}")){
                break;
            }
            else{
                System.out.println("Invalid format! Example: R0001"
                );
            }
        }
        System.out.print("Guest Name: ");
        String name = input.nextLine();
        String tier;
        while(true){
            System.out.println("Loyalty Tier:");
            System.out.println("[1] Elite");
            System.out.println("[2] Diamond");
            System.out.println("[3] Platinum");
            System.out.println("[4] Standard");
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
        Guest guest =new Guest( id, name, tier);
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              GUEST INFORMATION               |");
        System.out.println("+----------------------------------------------+");
        System.out.println(guest);
        System.out.println();
        while(true){


            System.out.println(
            "The VIP Guest is Successful?"
            );


            System.out.println(
            "[1] Yes"
            );


            System.out.println(
            "[2] No"
            );


            System.out.print(
            "Select option: "
            );


            String confirm =
                    input.nextLine();



            if(confirm.equals("1")){


                insertPriority(guest);



                System.out.println();


                System.out.println(
                "VIP Guest Added Successfully!"
                );


                break;


            }
            else if(confirm.equals("2")){


                System.out.println();


                System.out.println(
                "Add VIP Guest Cancelled!"
                );


                break;


            }
            else{


                System.out.println(
                "Invalid choice! Please select 1 or 2."
                );


            }


        }
}








    // Priority Queue insertion
    private static void insertPriority(Guest guest){
        LinkedQueue<Guest> temp = new LinkedQueue<>();
        boolean inserted = false;
        Iterator<Guest> iterator = vipQueue.getIterator();




        while(iterator.hasNext()){


            Guest current =
                    iterator.next();



            if(!inserted &&
               guest.getPriority()
               >
               current.getPriority()){


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



        System.out.print(
        "Enter Guest ID: "
        );


        String id =
                input.nextLine();




        Guest guest =
                searchGuestObject(id);




        if(guest == null){


            System.out.println(
            "Guest not found!"
            );

            return;

        }





        System.out.println(
        "Current Tier: "
        +
        guest.getLoyaltyTier()
        );



        System.out.print(
        "Enter New Tier: "
        );


        String newTier =
                input.nextLine();



        guest.setLoyaltyTier(newTier);



        // Re-sort queue

        LinkedQueue<Guest> temp =
                new LinkedQueue<>();



        Iterator<Guest> iterator =
                vipQueue.getIterator();



        while(iterator.hasNext()){


            temp.enqueue(
                    iterator.next()
            );

        }



        vipQueue =
                new LinkedQueue<>();



        while(!temp.isEmpty()){


            insertPriority(
                    temp.dequeue()
            );


        }




        System.out.println(
        "Upgrade Successful!"
        );


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
    // 5. Display Queue
    // ==============================

    private static void displayQueue(){



        Iterator<Guest> iterator =
                vipQueue.getIterator();



        if(!iterator.hasNext()){


            System.out.println(
            "Queue Empty."
            );

            return;


        }




        System.out.println(
        "\n===== VIP QUEUE ====="
        );



        int count = 1;



        while(iterator.hasNext()){


            System.out.println(
            count + "."
            );


            System.out.println(
            iterator.next()
            );


            System.out.println(
            "-------------------"
            );


            count++;


        }


    }









    // ==============================
    // 6. Search Guest
    // ==============================

    private static void searchGuest(){



        System.out.print(
        "Enter Guest ID: "
        );



        String id =
                input.nextLine();




        Guest guest =
                searchGuestObject(id);




        if(guest == null){


            System.out.println(
            "Guest not found!"
            );


        }
        else{


            System.out.println(
            "Guest Found:"
            );


            System.out.println(guest);


        }


    }









    private static Guest searchGuestObject(String id){



        Iterator<Guest> iterator =
                vipQueue.getIterator();



        while(iterator.hasNext()){


            Guest guest =
                    iterator.next();



            if(guest.getGuestID()
                    .equalsIgnoreCase(id)){


                return guest;


            }


        }



        return null;


    }



}