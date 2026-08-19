
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;


import boundary.MainUI;
import dao.BookingDao;
import dao.GuestDao;
import dao.LoyaltyDao;
import dao.RoomDao;



public class Hotel_Management_System {

    public static void main(String[] args) {

        BookingDao bookingDatabase = new BookingDao();
        GuestDao guestDatabase = new GuestDao();
        LoyaltyDao loyaltyDatabase = new LoyaltyDao();
       
        bookingDatabase.createBookingData();
        guestDatabase.createGuestData();
        loyaltyDatabase.createLoyaltyData();
        
        RoomDao.createRoomData();
        MainUI.MainUI();
    }
}

//public class Hotel_Management_System {
//
//    public static void main(String[] args) {
//
//        // Create all initial data in memory
//        GuestDao.createGuestData();
//        BookingDao.createBookingData();
//        LoyaltyDao.createLoyaltyData();
//        RoomDao.createRoomData();
//
//        // Start the main menu
//        MainUI.MainUI();
//    }
//}