/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entity;


import java.io.Serializable;
/**
 *
 * @author Kah Shun
 */
public class StatusEntry implements Serializable{
    
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
    
    public String getNote() {
        return note;
    }
    
    public String node(){
        return note;
    }
    
    public long getTimestamp(){
        return timeStamp;
    }
    
}
