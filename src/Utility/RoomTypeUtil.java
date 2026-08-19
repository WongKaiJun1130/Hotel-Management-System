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
    
    public static final int Normal_Room = 0;
    public static final int Deluxe_Room = 1;
    public static final int VIP_Room = 2;
    
    // prevent instantiation
    private RoomTypeUtil(){
    }
    
    public static String roomTypeName(int roomType){
        
        switch(roomType){
            case Normal_Room : return "Normal Room";
            case Deluxe_Room : return "Deluxe Room";
            case VIP_Room : return "VIP Room";
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
            case Normal_Room:
                return normalized.equals("normal room") || normalized.equals("normal") || normalized.equals("single");
            case Deluxe_Room:
                return normalized.equals("deluxe room") || normalized.equals("deluxe") || normalized.equals("medium");
            case VIP_Room:
                return normalized.equals("vip room") || normalized.equals("vip") || normalized.equals("large");
            default:
                return false;
        }
    }
}