/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package System_Utility;

import System_adt.StackInterface;
import System_adt.ArrayStack;

/**
 *
 * @author USER
 */
public class Navigation {
    
    public static StackInterface<Runnable> stack = new ArrayStack<>();

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
