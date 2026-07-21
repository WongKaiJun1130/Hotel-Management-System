/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_adt;

/**
 *
 * @author USER
 */
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoublyLinkedList<T> implements  DoublyLinkedListInterface<T> {
    
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
    
    @Override
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
 
    @Override
    public T rollback() {
        if (current == null || current.previous == null) {
            return null;
        }
 
        current = current.previous;
        return current.data;
    }
 
    @Override
    public T redo() {
        if (current == null || current.next == null) {
            return null;
        }
 
        current = current.next;
        return current.data;
    }
 
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
 
    @Override
    public T getCurrentData() {
        return (current == null) ? null : current.data;
    }
 
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
 
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
    
}
