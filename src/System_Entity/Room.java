/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Entity;


import System_adt.DoublyLinkedList;
/**
 *
 * @author USER
 */
public class Room {
    
    private String roomNum;
    private int roomType;
    private DoublyLinkedList<StatusEntry> statusHistory;
    
    public Room(String roomNum , int roomType){
        this.roomNum = roomNum;
        this.roomType = roomType;
        this.statusHistory = new DoublyLinkedList<>();
    }
    
    public String getRoomNum(){
        return roomNum;
    }
    
    public int getRoomType(){
        return roomType;
    }
    
    public void setRoomType(int roomType){
        this.roomType = roomType;
    }
    
    public DoublyLinkedList<StatusEntry>getStatusHistory(){
        return statusHistory;
    }
}

