/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Utility;

/**
 *
 * @author USER
 */
public class RoomStatusUtil {
    
    public static final int Dirty = 0;
    public static final int Clean_In_Progress = 1;
    public static final int Inspected = 2;
    public static final int Ready_For_CheckIN = 3;
    public static final int Late_CheckOut_Hold = 4;
    
    // prevent instantiation - this is a pure utility class
    private RoomStatusUtil(){
    }
    
    
    public static String statusName(int statusCode){
    
        switch(statusCode){
            case Dirty : return "Dirty";
            case Clean_In_Progress : return "Clean_In_Progress";
            case Inspected : return "Inspected";
            case Ready_For_CheckIN : return "Ready_For_CheckIN";
            case Late_CheckOut_Hold : return "Late_CheckOut_Hold";
            default : return "unknown";
        }
    }
    
    public static int nextStatusAfter(int statusCode){
        switch(statusCode){
            case Dirty : return Clean_In_Progress;
            case Clean_In_Progress : return Inspected;
            case Inspected : return Ready_For_CheckIN;
            default : return -1;
        }    
    }
}
