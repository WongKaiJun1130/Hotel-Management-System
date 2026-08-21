package adt;
/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

import java.util.Iterator;

// List Interface for DoublyLinkedList
public interface ListInterface<T> {

    void addAndAdvance(T data);
    T rollback();
    T redo();
    void insertAfterCurrent(T data);
    T getCurrentData();
    boolean add(T data);
    int getSize();
    T getEntry(int position);
    T remove(int position);
    boolean remove(T data);
    boolean replace(int position, T data);
    boolean isEmpty();
    Iterator<T> getIterator();


    // Array List Interface
    public interface ArrayListInterface<T> {

        boolean add(T newEntry);
        boolean add(int newPosition, T newEntry);
        T remove(int removePosition);
        void clear();
        boolean replace(int replacePosition, T newEntry);
        T getEntry(int givenPosition);
        int getNumberOfEntries();
        boolean contains(T anEntry);
        boolean isEmpty();
        boolean isFull();
        int indexOf(T element);
        T getLast();
        int getSize();
        boolean remove(T element);
    }


    // Stack Interface
    public interface StackInterface<T> {

        void push(T newEntry);
        T pop();
        T peek();
        boolean isEmpty();
        void clear();
        int getCurrentSize();
    }

    // Queue Interface
    public interface QueueInterface<T> {

        Iterator<T> getIterator();
        void enqueue(T newEntry);
        T dequeue();
        T getFront();
        boolean isEmpty();
        void clear();
    }
}