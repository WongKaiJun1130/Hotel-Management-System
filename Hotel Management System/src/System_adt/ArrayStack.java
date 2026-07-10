/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package System_adt;

/**
 *
 * @author USER
 */
public class ArrayStack<T> implements StackInterface<T> {

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

    @Override
    public void push(T newEntry) {
        if (topIndex == array.length - 1) {
        doubleCapacity();
    }
    topIndex++;
    array[topIndex] = newEntry;
    }
    private void doubleCapacity() {
    array = java.util.Arrays.copyOf(array, array.length * 2);
    }

    @Override
    public T peek() {
        T top = null;

        if (!isEmpty()) {
            top = array[topIndex];
        }

        return top;
    }

    @Override
    public T pop() {
        T top = null;
        if (!isEmpty()) {
            top = array[topIndex];
            array[topIndex] = null;
            topIndex--;

        } // end if

        return top;
    }

    @Override
    public boolean isEmpty() {
        return topIndex < 0;
    }

    @Override
    public void clear() {
        topIndex = -1;
    }
    
    @Override
    public int getCurrentSize() {
        return topIndex + 1;
    }
}
