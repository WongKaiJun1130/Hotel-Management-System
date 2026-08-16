///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package boundary_UI;
//
//import utility.Navigation;
//import utility.Utility;
//
///**
// *
// * @author USER
// */
//public class RegisterUI {
//    
//    // Main menu //
//    public static void menu() {
//        Navigation.stack.push(CustomerMainMenu);
//
//        boolean exit = false;
//
//        while (!Navigation.stack.isEmpty() && !exit) {
//            Runnable currentMenu = Navigation.stack.peek();
//            if (currentMenu != null) {
//                currentMenu.run();
//            } else {
//                exit = true;
//            }
//        }
//        System.out.println("Returning to main menu...");
//    
//    } // end register main menu //
//    
//    private static final Runnable CustomerMainMenu = () -> {
//        String[] options = {
//            "1. Customer Management",
//            "2. Register",
//            "0. Back"
//        };
//        
//        Runnable[] actions = {
//            () -> System.out.println("Customer Management is not implemented yet."),
//            () -> System.out.println("Register is not implemented yet."),
//            () -> Navigation.stack.pop()
//        };
//
//        Utility.customMenu(
//                options,
//                "Register Menu",
//                "Enter your choice: ",
//                actions
//        );
//    
//    };
//}