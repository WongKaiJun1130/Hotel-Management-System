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
import java.util.Comparator;

//================================================
// List Interface for DoublyLinkedList
//================================================
    public interface ListInterface<T> {
       public void insertAndAdvance(T data);
       public T rollback();
       public T redo();
       public void spliceAfterCurrent(T data);
       public T getCurrentData();
       public boolean isEmpty();
       public Iterator<T>getIterator();
   }
    
//==============================================================================
// Array List Interface 
//==============================================================================
  interface ArrayListInterface <T> {
    public boolean add(T newEntry);
    public boolean add(int newPosition,T newEntry);
    public T remove(int removePosition);
    public void clear();
    public boolean replace(int replacePosition,T newEntry);
    public T getEntry(int givenPosition);
    public int getNumberOfEntries();
    public boolean contains(T anEntry);
    public boolean isEmpty();
    public boolean isFull();
    public void sort(java.util.Comparator<T> comparator);
    
}
//==============================================================================
// Queue Interface 
//==============================================================================
 interface QueueInterface<T> {
    public Iterator<T> getIterator();
    public void enqueue(T newEntry);
    public T dequeue();
    public T getFront();
    public boolean isEmpty();
    public void clear();
}


//==============================================================================
// Stack Interface 
//==============================================================================
 interface StackInterface<T> {
    public void push(T newEntry);
    public T pop();
    public T peek();
    public boolean isEmpty();
    public void clear();
    public int getCurrentSize();
}



    
