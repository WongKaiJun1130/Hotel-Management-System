/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

/**
 *
 * @author kaijun,kahshun,weikin
 */

import Boundary.MainUI;
import dao.BookingDao;
import dao.GuestDao;
import dao.LoyaltyDao;
import dao.RoomDao;

public class Hotel_Management_System {

    public static void main(String[] args) {

        // Create all initial data in memory
        GuestDao.createGuestData();
        BookingDao.createBookingData();
        LoyaltyDao.createLoyaltyData();
        RoomDao.createRoomData();

        // Start the main menu
        MainUI.MainUI();
    }
}
