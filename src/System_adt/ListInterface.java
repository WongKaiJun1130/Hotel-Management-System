/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package System_adt;

/**
 *
 * @author USER
 */
public interface ListInterface <T> {
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
