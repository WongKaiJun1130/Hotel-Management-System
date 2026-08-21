/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

/**
 *
 * @author Chia Kah Shun
 */
public class RoomTypeUtil {
    
    public static final int Single_Room = 0;
    public static final int Medium_Room = 1;
    public static final int Large_Room = 2;
    
    // prevent instantiation
    private RoomTypeUtil(){
    }
    
    public static String roomTypeName(int roomType){
        
        switch(roomType){
            case Single_Room : return "Single Room";
            case Medium_Room : return "Medium Room";
            case Large_Room : return "Large Room";
            default : return "Unknown";
        }
    }

    public static boolean matchesLabel(
        int roomType,
        String label) {
        if (label == null) {
            return false;
        }
        String normalized = label.trim().toLowerCase();
        switch (roomType) {
            case Single_Room:
                return normalized.equals("single")
                        || normalized.equals("single room");
            case Medium_Room:
                return normalized.equals("medium")
                        || normalized.equals("medium room");
            case Large_Room:
                return normalized.equals("large")
                        || normalized.equals("large room");
            default:
                return false;
        }
    }
}