
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;


import boundary_UI.MainUI;
import dao.BookingDatabase;
import dao.GuestDatabase;
import dao.LoyaltyDatabase;
import dao.RoomDao;



public class Hotel_Management_System {

    public static void main(String[] args) {

        BookingDatabase bookingDatabase = new BookingDatabase();
        GuestDatabase guestDatabase = new GuestDatabase();
        LoyaltyDatabase loyaltyDatabase = new LoyaltyDatabase();
       
        BookingDatabase.createBookingData();
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