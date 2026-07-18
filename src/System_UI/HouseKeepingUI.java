package System_UI;

import System_Utility.Navigation;
import System_Utility.Utility;
import System_adt.StackInterface;
public class HouseKeepingUI {
    
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
//        () -> Navigation.stack.push(showRoomStatus),
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

  
  
   



}