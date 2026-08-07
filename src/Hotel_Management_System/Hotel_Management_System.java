/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

import UI.MainUI;
import dao.GuestDao;
import dao.LoyaltyDao;
import dao.RoomDao;


public class Hotel_Management_System {

    public static void main(String[] args) {
        GuestDao guestDatabase = new GuestDao();
        LoyaltyDao loyaltyDatabase = new LoyaltyDao();
       
        guestDatabase.createGuestData();
        loyaltyDatabase.createLoyaltyData();
        
        RoomDao.createRoomData();
        MainUI.MainUI();
    }
}


//package main;
//
//import boundary.MainUI;
//
//public class Hotel_Management_System {
//    public static void main(String[] args) {
//        MainUI mainUI = new MainUI();
//        mainUI.menu();
//    }
//}