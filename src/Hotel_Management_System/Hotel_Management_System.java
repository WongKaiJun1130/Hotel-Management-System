/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

import System_UI.MainUI;
import dao.GuestDatabase;

public class Hotel_Management_System {

    public static void main(String[] args) {
        GuestDatabase dao = new GuestDatabase();
        dao.createGuestData();
        MainUI.MainUI();

    }
}