/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Control;

import System_adt.ArrayStack;
import System_adt.StackInterface;

/**
 *
 * @author USER
 */
public class RoomTracker {
    private String roomNum;
    private StackInterface<String> status;
    
    public RoomTracker(String roomNum){
        this.roomNum = roomNum;
        this.status = new ArrayStack<>();    
    }
    
    public void updateStatus(String newStatus) {
        status.push(newStatus);
        System.out.println("Room " + roomNum + " -> " + newStatus);
    }
    
    public void rollbackStatus() {
        if (status.isEmpty()) {
            System.out.println("No status to roll back for Room " + roomNum);
            return;
        }
        String removed = status.pop();
        String current = status.isEmpty() ? "No status logged" : status.peek();
        System.out.println("Rolled back '" + removed + "' -> Room " + roomNum + " is now: " + current);
    }
    
    public String getCurrentStatus() {
        return status.isEmpty() ? "No status logged" : status.peek();
    }

    public String getRoomNumber() {
        return roomNum;
    }

    public boolean hasHistory() {
        return !status.isEmpty();
    }
}
