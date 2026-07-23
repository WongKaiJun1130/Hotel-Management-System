/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_adt;
import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author USER
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<T>{
    
    private Node<T> head;
    private Node<T> tail;
    private Node<T> current;
    private int size;
    
    public DoublyLinkedList() {
        head = null;
        tail = null;
        current = null;
        size = 0;
    }
    
    public void insertAndAdvance(T data) {
        Node<T> newNode = new Node<>(data);
 
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            current = newNode;
        } else {
            newNode.previous = current;
            current.next = newNode;
            current = newNode;
            tail = newNode;
        }
 
        size++;
    }
 
    public T rollback() {
        if (current == null || current.previous == null) {
            return null;
        }
 
        current = current.previous;
        return current.data;
    }
 
    
    public T redo() {
        if (current == null || current.next == null) {
            return null;
        }
 
        current = current.next;
        return current.data;
    }
 
    public void spliceAfterCurrent(T data) {
        Node<T> newNode = new Node<>(data);
 
        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            current = newNode;
            size++;
            return;
        }
 
        Node<T> queuedNext = current.next;
 
        newNode.previous = current;
        newNode.next = queuedNext;
        current.next = newNode;
 
        if (queuedNext != null) {
            queuedNext.previous = newNode;
        } else {
            tail = newNode;
        }
 
        current = newNode;
        size++;
    }
 
    
    public T getCurrentData() {
        return (current == null) ? null : current.data;
    }
 
    public boolean isEmpty() {
        return size == 0;
    }
 
    public Iterator<T> getIterator() {
        return new DoublyLinkedListIterator();
    }
 
    private class DoublyLinkedListIterator implements Iterator<T> {
 
        private Node<T> nextNode;
 
        public DoublyLinkedListIterator() {
            nextNode = head;
        }
 
        @Override
        public boolean hasNext() {
            return nextNode != null;
        }
 
        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
 
            T data = nextNode.data;
            nextNode = nextNode.next;
            return data;
        }
    }
 
    private static class Node<T> {
 
        private T data;
        private Node<T> previous;
        private Node<T> next;
 
        private Node(T data) {
            this.data = data;
            this.previous = null;
            this.next = null;
        }
    }
    
    //==========================================================================
    // ArrayList 
    //==========================================================================
    
   public static class ArrayList<T> implements  ArrayListInterface<T> ,Serializable , Iterable<T>{

    private T[] array;
    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 10;

    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int initialCapacity) {
        numberOfEntries = 0;
        array = (T[]) new Object[initialCapacity];
    }

    public boolean add(T newEntry) {
        if (isFull()) {
            increaseCapacity();
        }
        array[numberOfEntries] = newEntry;
        numberOfEntries++;
        return true;
    }

    public boolean add(int newPosition, T newEntry) {
        boolean isSuccessful = true;

        if ((newPosition >= 1) && (newPosition <= numberOfEntries + 1)) {
            if (isFull()) {
                increaseCapacity();
            }
            makeRoom(newPosition);
            array[newPosition - 1] = newEntry;
            numberOfEntries++;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    private void increaseCapacity() {
        T[] oldArray = array;
        array = (T[]) new Object[oldArray.length*2];

        System.arraycopy(oldArray, 0, array, 0, oldArray.length);
    }

    public T remove(int removePosition) {
        T result = null;

        if ((removePosition >= 1) && (removePosition <= numberOfEntries)) {
            result = array[removePosition - 1];

            if (removePosition < numberOfEntries) {
                removeGap(removePosition);
            }

            numberOfEntries--;
        }

        return result;
    }

    public void clear() {
        numberOfEntries = 0;
    }

    public boolean replace(int replacePosition, T newEntry) {
        boolean isSuccessful = true;

        if ((replacePosition >= 1) && (replacePosition <= numberOfEntries)) {
            array[replacePosition - 1] = newEntry;
        } else {
            isSuccessful = false;
        }

        return isSuccessful;
    }

    public T getEntry(int givenPosition) {
        T result = null;
        if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
            result = array[givenPosition - 1];

        }
        return result;

    }

    public int getNumberOfEntries() {
        return numberOfEntries;
    }

    public boolean contains(T anEntry) {
        boolean found = false;
        for (int i = 0; !found && (i < numberOfEntries); i++) {
            if (anEntry.equals(array[i])) {
                found = true;
            }
        }
        return found;
    }

    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    public boolean isFull() {
        return numberOfEntries == array.length;
    }

    public String toString() {
        String outputStr = "";
        for (int i = 0; i < numberOfEntries; ++i) {
            outputStr += array[i] + "\n";
        }

        return outputStr;
    }

    private void makeRoom(int newPosition) {
        int newIndex = newPosition - 1;
        int lastIndex = numberOfEntries - 1;

        for (int i = lastIndex; i >= newIndex; i--) {
            array[i + 1] = array[i];
        }
    }

    private void removeGap(int givenPosition) {
        int removedIndex = givenPosition - 1;
        int lastIndex = numberOfEntries - 1;

        for (int i = removedIndex; i < lastIndex; i++) {
            array[i] = array[i + 1];
        }
    }

    public void sort(Comparator<T> comparator) {
        for (int i = 0; i < numberOfEntries - 1; i++) {
            for (int j = 0; j < numberOfEntries - i - 1; j++) {
                if (comparator.compare(array[j], array[j + 1]) > 0) {
                    T temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
    }
    
    

    //Enable your custom `System_adt.ArrayList` to use the for-each loop for iterating through its data.
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int index = 0;
            public boolean hasNext() {
                return index < numberOfEntries;
            }
            public T next() {
                return array[index++];
            }
        };
    }

}

    
    //==========================================================================
    // ArrayStack
    //==========================================================================
    
    public static class ArrayStack<T> implements StackInterface<T>{

    private T[] array;
    private int topIndex; // index of top entry
    private static final int DEFAULT_CAPACITY = 50;

    public ArrayStack() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayStack(int initialCapacity) {
        array = (T[]) new Object[initialCapacity];
        topIndex = -1;
    }

    public void push(T newEntry) {
        if (topIndex == array.length - 1) {
        doubleCapacity();
    }
    topIndex++;
    array[topIndex] = newEntry;
    }
    //private void doubleCapacity() {
    //array = java.util.Arrays.copyOf(array, array.length * 2);
    //}
    
    private void doubleCapacity() {
        T[] oldArray = array;
        array = (T[]) new Object[oldArray.length * 2];
        for (int i = 0; i < oldArray.length; i++) {
            array[i] = oldArray[i];
        }
    }

    public T peek() {
        T top = null;
        if (!isEmpty()) {
            top = array[topIndex];
        }
        return top;
    }

    public T pop() {
        T top = null;
        if (!isEmpty()) {
            top = array[topIndex];
            array[topIndex] = null;
            topIndex--;

        } // end if

        return top;
    }

    public boolean isEmpty() {
        return topIndex < 0;
    }

    public void clear() {
        topIndex = -1;
    }
    
    public int getCurrentSize() {
        return topIndex + 1;
    }
}

    //==========================================================================
    // ArrayQueue
    //==========================================================================
    public static class ArrayQueue<T> implements QueueInterface<T>{
    private T[] queue;
    private int front;
    private int back;
    private int numberOfEntries;

    private static final int DEFAULT_CAPACITY = 10;

    public ArrayQueue() {
        queue = (T[]) new Object[DEFAULT_CAPACITY];
        front = 0;
        back = -1;
        numberOfEntries = 0;
    }

    public void enqueue(T newEntry) {

        if (isFull()) {
            increaseCapacity();
        }

        back = (back + 1) % queue.length;
        queue[back] = newEntry;
        numberOfEntries++;
    }

    public T dequeue() {

        if (isEmpty()) {
            return null;
        }

        T frontEntry = queue[front];
        queue[front] = null;

        front = (front + 1) % queue.length;
        numberOfEntries--;

        if (numberOfEntries == 0) {
            front = 0;
            back = -1;
        }

        return frontEntry;
    }

    public T getFront() {

        if (isEmpty()) {
            return null;
        }

        return queue[front];
    }

    public boolean isEmpty() {
        return numberOfEntries == 0;
    }

    public boolean isFull() {
        return numberOfEntries == queue.length;
    }

    public void clear() {
        queue = (T[]) new Object[DEFAULT_CAPACITY];
        front = 0;
        back = -1;
        numberOfEntries = 0;
    }

    private void increaseCapacity() {

        T[] oldQueue = queue;
        queue = (T[]) new Object[oldQueue.length * 2];

        for (int i = 0; i < numberOfEntries; i++) {
            queue[i] = oldQueue[(front + i) % oldQueue.length];
        }

        front = 0;
        back = numberOfEntries - 1;
    }

    public Iterator<T> getIterator() {
        return new ArrayQueueIterator();
    }

    private class ArrayQueueIterator implements Iterator<T> {

        private int current = 0;
        public boolean hasNext() {
            return current < numberOfEntries;
        }
        public T next() {

            if (!hasNext()) {
                return null;
            }
            T data = queue[(front + current) % queue.length];
            current++;
            return data;
        }
    }
    
}
}
