/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package System_adt;

/**
 *
 * @author user
 */

import java.util.Iterator;
public class LinkedQueue<T> implements QueueInterface<T> {

  private Node firstNode; // references node at front of queue
  private Node lastNode;  // references node at back of queue

  public LinkedQueue() {
    firstNode = null;
    lastNode = null;
  } 
  
  @Override
  public void enqueue(T newEntry) {
    Node newNode = new Node(newEntry, null);

    if (isEmpty()) {
      firstNode = newNode;
    } else {
      lastNode.next = newNode;
    }

    lastNode = newNode;
  } 
  
  @Override
  public T getFront() {
    T front = null;

    if (!isEmpty()) {
      front = firstNode.data;
    }

    return front;
  } 

  @Override
  public T dequeue() {
    T front = null;

    if (!isEmpty()) {
      front = firstNode.data;
      firstNode = firstNode.next;

      if (firstNode == null) {
        lastNode = null;
      }
    } 
    return front;
  } // end dequeue

  @Override
  public boolean isEmpty() {
    return (firstNode == null) && (lastNode == null);
  }

  public void clear() {
    firstNode = null;
    lastNode = null;
  } 
  
@Override
  public Iterator<T> getIterator() {
    return new LinkedQueueIterator();
  }

  private class LinkedQueueIterator implements Iterator<T> {

    private Node currentNode;

    public LinkedQueueIterator() {
      currentNode = firstNode;
    }

    @Override
    public boolean hasNext() {
      return currentNode != null;
    }

    @Override
    public T next() {
      if (hasNext()) {
        T returnData = currentNode.data;
        currentNode = currentNode.next;
        return returnData;
      } else {
        return null;
      }
    }
  }

  private class Node {

    private T data; 
    private Node next; 

    private Node(T data) {
      this.data = data;
      this.next = null;
    } 

    private Node(T data, Node next) {
      this.data = data;
      this.next = next;
    } 
  } 
} 
