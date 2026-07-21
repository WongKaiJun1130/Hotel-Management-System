/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Entity;

/**
 *
 * @author USER
 */
public class StatusEntry {
    
    private int statusCode;
    private String note;
    private long timeStamp;
    
    
    public StatusEntry(int statusCode, String note){
        this.statusCode = statusCode;
        this.note = (note == null) ? "" : note;
        this.timeStamp = System.currentTimeMillis();
    }
    
    public int getStatusCode(){
        return statusCode;
    }
    
    public String node(){
        return note;
    }
    
    public long timeStamp(){
        return timeStamp;
    }
    
}
