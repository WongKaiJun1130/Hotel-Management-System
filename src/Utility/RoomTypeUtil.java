/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

/**
 *
 * @author Kah Shun
 */

public class RoomTypeUtil {

    public static final int Single = 0;
    public static final int Medium = 1;
    public static final int Large = 2;

    public static String roomTypeName(int type) {
        switch (type) {
            case Single:
                return "Single";
            case Medium:
                return "Medium";
            case Large:
                return "Large";
            default:
                return "Unknown";
        }
    }
}