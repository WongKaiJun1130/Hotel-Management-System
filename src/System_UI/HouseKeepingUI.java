package System_UI;

import System_Entity.Room;
import System_Entity.StatusEntry;
import System_Utility.Navigation;
import System_Utility.Utility;
import System_Utility.RoomStatusUtil;
import System_adt.*;
import System_Utility.RoomTypeUtil;

import java.util.Scanner;
import java.util.Iterator;


public class HouseKeepingUI {
    
    private static DoublyLinkedList.ArrayList<Room> rooms = new DoublyLinkedList.ArrayList<>();
    private static Scanner input = new Scanner(System.in);
    
    // ==========================
    // Main Housekeeping Menu
    // ==========================
    public static void menu() {

        Navigation.stack.push(HouseKeepingMenu);

        boolean exit = false;

        while (!Navigation.stack.isEmpty() && !exit) {

            Runnable currentMenu = Navigation.stack.peek();

            if (currentMenu != null) {
                currentMenu.run();
            } else {
                exit = true;
            }
        }

        System.out.println("Returning to Main Menu...");
    }

    private static final Runnable HouseKeepingMenu = () -> {

    String[] options = {
        "1. Show Room Status",
        "2. Manage Room Status",
        "3. Report 1",
        "4. Report 2",
        "0. Back to Main Menu"
    };

    Runnable[] actions = {
        () -> showRoomStatus(),
//        () -> Navigation.stack.push(manageRoom),
//        () -> Navigation.stack.push(report1),
//        () -> Navigation.stack.push(report2),
        () -> Navigation.stack.pop()
    };

    Utility.customMenu(
            options,
            "Housekeeping Menu",
            "Enter your choice: ",
            actions
    );

};
    
    // show room status //
    private static void showRoomStatus(){
        
        System.out.println();
        System.out.println("+----------------------------------------------+");
        System.out.println("|              ROOM STATUS OVERVIEW            |");
        System.out.println("+----------------------------------------------+");
        
        if(rooms.isEmpty()){
            System.out.println("No room registered yet");
            return;
        }
        
        for(int i = 1; i <= rooms.getNumberOfEntries() ; i++){
        
       Room room = rooms.getEntry(i);
       StatusEntry current = room.getStatusHistory().getCurrentData();
       System.out.print("Room" + room.getRoomNum() + "[" + RoomTypeUtil.roomTypeName(room.getRoomType()) + "]" + RoomStatusUtil.statusName(current.getStatusCode()));
    }
    }
}