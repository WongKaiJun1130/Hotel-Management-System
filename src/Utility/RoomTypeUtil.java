/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utility;

/**
 *
 * @author USER
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
}
