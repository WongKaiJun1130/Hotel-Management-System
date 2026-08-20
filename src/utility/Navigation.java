/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utility;

import adt.DoublyLinkedList;
import adt.ListInterface.StackInterface;

/**
 *
 * @author USER
 */
public class Navigation {

    public static StackInterface<Runnable> stack = new DoublyLinkedList<>();

    public static void goBack() {

        if (!stack.isEmpty()) {

            stack.pop();

            if (!stack.isEmpty()) {
                stack.peek().run();
            } else {
                System.out.println("No previous menu.");
            }
        }
    }
}