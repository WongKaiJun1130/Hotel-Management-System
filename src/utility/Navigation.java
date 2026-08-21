
package utility;

/**
 *
 * @author Wong Kai Jun, Yeong Wei Kin, Chia Kah Shun, Heng CHuan Wai
 */

import adt.DoublyLinkedList;
import adt.ListInterface.StackInterface;

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