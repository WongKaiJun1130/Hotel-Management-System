package adt;

/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

import java.io.Serializable;
import java.util.Comparator;
import java.util.Iterator;
import java.util.NoSuchElementException;

import adt.ListInterface.ArrayListInterface;
import adt.ListInterface.StackInterface;
import adt.ListInterface.QueueInterface;

public class DoublyLinkedList<T> implements ListInterface<T>, StackInterface<T>, QueueInterface<T> {

    private Node<T> head;
    private Node<T> tail;
    private Node<T> current;
    private int size;

    //==========================================================================
    // Constructor
    //==========================================================================
    public DoublyLinkedList() {
        head = null;
        tail = null;
        current = null;
        size = 0;
    }

    //==========================================================================
    // Insert And Advance
    //==========================================================================
    @Override
    public void insertAndAdvance(T data) {
        Node<T> newNode = new Node<>(data);

        if (isEmpty()) {
            head = newNode;
            tail = newNode;
            current = newNode;
            size = 1;
            return;
        }

        if (current != null && current.next != null) {
            Node<T> temp = current.next;

            while (temp != null) {
                Node<T> next = temp.next;
                temp.previous = null;
                temp.next = null;
                size--;
                temp = next;
            }

            current.next = null;
            tail = current;
        }

        newNode.previous = current;
        current.next = newNode;
        current = newNode;
        tail = newNode;
        size++;
    }

    //==========================================================================
    // Rollback
    //==========================================================================
    @Override
    public T rollback() {
        if (current == null || current.previous == null) {
            return null;
        }

        current = current.previous;
        return current.data;
    }

    //==========================================================================
    // Redo
    //==========================================================================
    @Override
    public T redo() {
        if (current == null || current.next == null) {
            return null;
        }

        current = current.next;
        return current.data;
    }

    //==========================================================================
    // Splice After Current
    //==========================================================================
    @Override
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

    //==========================================================================
    // Get Current Data
    //==========================================================================
    @Override
    public T getCurrentData() {
        return (current == null) ? null : current.data;
    }

    //==========================================================================
    // Stack Methods
    //==========================================================================

    @Override
    public void push(T newEntry) {
        add(newEntry);
    }

    @Override
    public T pop() {
        if (isEmpty()) {
            return null;
        }

        return remove(getSize());
    }

    @Override
    public T peek() {
        if (isEmpty()) {
            return null;
        }

        return getEntry(getSize());
    }

    @Override
    public int getCurrentSize() {
        return getSize();
    }

    //==========================================================================
    // List Methods
    //==========================================================================

    @Override
    public boolean add(T data) {
        insertAndAdvance(data);
        return true;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public T getEntry(int position) {
        if (position < 1 || position > size) {
            return null;
        }

        Node<T> temp = head;

        for (int i = 1; i < position; i++) {
            temp = temp.next;
        }

        return temp.data;
    }

    @Override
    public boolean replace(int position, T data) {
        if (position < 1 || position > size) {
            return false;
        }

        Node<T> temp = head;

        for (int i = 1; i < position; i++) {
            temp = temp.next;
        }

        temp.data = data;
        return true;
    }

    @Override
    public T remove(int position) {
        if (position < 1 || position > size) {
            return null;
        }

        Node<T> temp = head;

        for (int i = 1; i < position; i++) {
            temp = temp.next;
        }

        if (temp.previous != null) {
            temp.previous.next = temp.next;
        } else {
            head = temp.next;
        }

        if (temp.next != null) {
            temp.next.previous = temp.previous;
        } else {
            tail = temp.previous;
        }

        if (current == temp) {
            current = temp.previous;
        }

        size--;

        if (size == 0) {
            head = null;
            tail = null;
            current = null;
        }

        return temp.data;
    }

    @Override
    public boolean remove(T data) {
        Node<T> temp = head;

        while (temp != null) {
            if ((temp.data == null && data == null)
                    || (temp.data != null && temp.data.equals(data))) {

                removeNode(temp);
                return true;
            }

            temp = temp.next;
        }

        return false;
    }

    //==========================================================================
    // Remove Node
    //==========================================================================
    private void removeNode(Node<T> node) {
        if (node == null) {
            return;
        }

        if (node.previous != null) {
            node.previous.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.previous = node.previous;
        } else {
            tail = node.previous;
        }

        if (current == node) {
            current = node.previous;
        }

        size--;

        if (size == 0) {
            head = null;
            tail = null;
            current = null;
        }
    }

    //==========================================================================
    // Is Empty
    //==========================================================================
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    //==========================================================================
    // Iterator
    //==========================================================================
    @Override
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

    //==========================================================================
    // Node
    //==========================================================================
    private static class Node<T> implements Serializable {

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
    // Queue Methods
    //==========================================================================

    @Override
    public void enqueue(T data) {
        add(data);
    }

    @Override
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }

        return remove(1);
    }

    @Override
    public T getFront() {
        if (isEmpty()) {
            return null;
        }

        return getEntry(1);
    }

    //==========================================================================
    // Clear
    // Stack + Queue Common Method
    //==========================================================================
    @Override
    public void clear() {
        head = null;
        tail = null;
        current = null;
        size = 0;
    }

    //==========================================================================
    // ArrayList
    //==========================================================================
    public static class ArrayList<T> implements ArrayListInterface<T>, Serializable, Iterable<T> {

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

        @Override
        public boolean add(T newEntry) {
            if (isFull()) {
                increaseCapacity();
            }

            array[numberOfEntries] = newEntry;
            numberOfEntries++;

            return true;
        }

        @Override
        public boolean add(int newPosition, T newEntry) {
            boolean isSuccessful = true;

            if (newPosition >= 1 && newPosition <= numberOfEntries + 1) {

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

        //==========================================================================
        // Increase Capacity
        //==========================================================================
        private void increaseCapacity() {
            T[] oldArray = array;
            array = (T[]) new Object[oldArray.length * 2];

            System.arraycopy(oldArray, 0, array, 0, oldArray.length);
        }

        @Override
        public T remove(int removePosition) {
            T result = null;

            if (removePosition >= 1 && removePosition <= numberOfEntries) {

                result = array[removePosition - 1];

                if (removePosition < numberOfEntries) {
                    removeGap(removePosition);
                }

                array[numberOfEntries - 1] = null;
                numberOfEntries--;
            }

            return result;
        }

        @Override
        public void clear() {
            for (int i = 0; i < numberOfEntries; i++) {
                array[i] = null;
            }

            numberOfEntries = 0;
        }

        @Override
        public boolean replace(int replacePosition, T newEntry) {
            boolean isSuccessful = true;

            if (replacePosition >= 1 && replacePosition <= numberOfEntries) {
                array[replacePosition - 1] = newEntry;
            } else {
                isSuccessful = false;
            }

            return isSuccessful;
        }

        @Override
        public T getEntry(int givenPosition) {
            T result = null;

            if (givenPosition >= 1 && givenPosition <= numberOfEntries) {
                result = array[givenPosition - 1];
            }

            return result;
        }

        @Override
        public int getNumberOfEntries() {
            return numberOfEntries;
        }

        @Override
        public boolean contains(T anEntry) {
            boolean found = false;

            for (int i = 0; !found && i < numberOfEntries; i++) {

                if ((anEntry == null && array[i] == null)
                        || (anEntry != null && anEntry.equals(array[i]))) {

                    found = true;
                }
            }

            return found;
        }

        @Override
        public boolean isEmpty() {
            return numberOfEntries == 0;
        }

        @Override
        public boolean isFull() {
            return numberOfEntries == array.length;
        }

        @Override
        public String toString() {
            String outputStr = "";

            for (int i = 0; i < numberOfEntries; i++) {
                outputStr += array[i] + "\n";
            }

            return outputStr;
        }

        //==========================================================================
        // Make Room
        //==========================================================================
        private void makeRoom(int newPosition) {
            int newIndex = newPosition - 1;
            int lastIndex = numberOfEntries - 1;

            for (int i = lastIndex; i >= newIndex; i--) {
                array[i + 1] = array[i];
            }
        }

        //==========================================================================
        // Remove Gap
        //==========================================================================
        private void removeGap(int givenPosition) {
            int removedIndex = givenPosition - 1;
            int lastIndex = numberOfEntries - 1;

            for (int i = removedIndex; i < lastIndex; i++) {
                array[i] = array[i + 1];
            }
        }

        //==========================================================================
        // Sort
        //==========================================================================
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

        @Override
        public int indexOf(T element) {
            for (int i = 0; i < numberOfEntries; i++) {

                if ((element == null && array[i] == null)
                        || (element != null && element.equals(array[i]))) {

                    return i;
                }
            }

            return -1;
        }

        @Override
        public T getLast() {
            if (numberOfEntries == 0) {
                return null;
            }

            return array[numberOfEntries - 1];
        }

        @Override
        public int getSize() {
            return numberOfEntries;
        }

        @Override
        public boolean remove(T element) {
            int index = indexOf(element);

            if (index == -1) {
                return false;
            }

            remove(index + 1);

            return true;
        }

        //==========================================================================
        // Iterator
        //==========================================================================
        @Override
        public Iterator<T> iterator() {

            return new Iterator<T>() {

                private int index = 0;

                @Override
                public boolean hasNext() {
                    return index < numberOfEntries;
                }

                @Override
                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }

                    return array[index++];
                }
            };
        }
    }

    //==========================================================================
    // ArrayStack
    //==========================================================================
    public static class ArrayStack<T> implements StackInterface<T> {

        private T[] array;
        private int topIndex;
        private static final int DEFAULT_CAPACITY = 50;

        public ArrayStack() {
            this(DEFAULT_CAPACITY);
        }

        public ArrayStack(int initialCapacity) {
            array = (T[]) new Object[initialCapacity];
            topIndex = -1;
        }

        @Override
        public void push(T newEntry) {
            if (topIndex == array.length - 1) {
                doubleCapacity();
            }

            topIndex++;
            array[topIndex] = newEntry;
        }

        //==========================================================================
        // Double Capacity
        //==========================================================================
        private void doubleCapacity() {
            T[] oldArray = array;
            array = (T[]) new Object[oldArray.length * 2];

            for (int i = 0; i < oldArray.length; i++) {
                array[i] = oldArray[i];
            }
        }

        @Override
        public T peek() {
            if (isEmpty()) {
                return null;
            }

            return array[topIndex];
        }

        @Override
        public T pop() {
            if (isEmpty()) {
                return null;
            }

            T top = array[topIndex];

            array[topIndex] = null;
            topIndex--;

            return top;
        }

        @Override
        public boolean isEmpty() {
            return topIndex < 0;
        }

        @Override
        public void clear() {
            for (int i = 0; i <= topIndex; i++) {
                array[i] = null;
            }

            topIndex = -1;
        }

        @Override
        public int getCurrentSize() {
            return topIndex + 1;
        }
    }

    //==========================================================================
    // ArrayQueue
    //==========================================================================
    public static class ArrayQueue<T> implements QueueInterface<T> {

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

        @Override
        public void enqueue(T newEntry) {
            if (isFull()) {
                increaseCapacity();
            }

            back = (back + 1) % queue.length;
            queue[back] = newEntry;
            numberOfEntries++;
        }

        @Override
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

        @Override
        public T getFront() {
            if (isEmpty()) {
                return null;
            }

            return queue[front];
        }

        @Override
        public boolean isEmpty() {
            return numberOfEntries == 0;
        }

        public boolean isFull() {
            return numberOfEntries == queue.length;
        }

        @Override
        public void clear() {
            queue = (T[]) new Object[DEFAULT_CAPACITY];
            front = 0;
            back = -1;
            numberOfEntries = 0;
        }

        //==========================================================================
        // Increase Capacity
        //==========================================================================
        private void increaseCapacity() {
            T[] oldQueue = queue;

            queue = (T[]) new Object[oldQueue.length * 2];

            for (int i = 0; i < numberOfEntries; i++) {
                queue[i] = oldQueue[(front + i) % oldQueue.length];
            }

            front = 0;
            back = numberOfEntries - 1;
        }

        //==========================================================================
        // Iterator
        //==========================================================================
        @Override
        public Iterator<T> getIterator() {
            return new ArrayQueueIterator();
        }

        private class ArrayQueueIterator implements Iterator<T> {

            private int current = 0;

            @Override
            public boolean hasNext() {
                return current < numberOfEntries;
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }

                T data = queue[(front + current) % queue.length];

                current++;

                return data;
            }
        }
    }
}