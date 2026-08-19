/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

/**
 *
 * @author Kah Shun
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

    // Booking module labels rooms "Single/Medium/Large" while Housekeeping
    // labels them "Normal/Deluxe/VIP Room" - same three tiers, different
    // vocabulary. This lets cross-module code (matching a Housekeeping
    // Room against a Booking's room-type string) recognise either naming
    // instead of silently matching nothing.
    public static boolean matchesLabel(int roomType, String label){
        if (label == null) {
            return false;
        }

        String normalized = label.trim().toLowerCase();

        switch (roomType) {
            case Single_Room:
                return normalized.equals("single room") || normalized.equals("Single") || normalized.equals("single");
            case Medium_Room:
                return normalized.equals("medium room") || normalized.equals("Medium") || normalized.equals("medium");
            case Large_Room:
                return normalized.equals("large room") || normalized.equals("Large") || normalized.equals("large");
            default:
                return false;
        }
    }
}