/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Utility;

/**
 *
 * @author USER
 */
public class RoomTypeUtil {
    
    private static final int Normal_Room = 0;
    private static final int Deluxe_Room = 1;
    private static final int VIP_Room = 2;
    
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
