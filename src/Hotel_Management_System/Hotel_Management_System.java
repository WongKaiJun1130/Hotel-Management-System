/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Hotel_Management_System;

import System_UI.MainUI;
import System_UI.VIPAllocationUI;

public class Hotel_Management_System {

    public static void main(String[] args) {

        VIPAllocationUI.loadGuestDatabase();
        MainUI menu = new MainUI(); 
        menu.MainUI();

    }
}