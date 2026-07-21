/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package System_adt;

/**
 *
 * @author USER
 */

import java.util.Iterator;

public interface DoublyLinkedListInterface<T> {
    
    public void insertAndAdvance(T data);
    
    public T rollback();
    
    public T redo();
    
    public void spliceAfterCurrent(T data);
    
    public T getCurrentData();
    
    public boolean isEmpty();
    
    public Iterator<T>getIterator();
    
}
