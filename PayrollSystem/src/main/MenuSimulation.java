package main;
import payrollsystem.Menu;
import payrollsystem.PayRollSystem;


/**
 * MenuSimulation class for running the command-line payroll system.
 */
public class MenuSimulation {

    /**
     * The entry point of the application.
     *
     * @param args command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        PayRollSystem payroll = new PayRollSystem();
        Menu menu = new Menu();
        menu.run(payroll);
    }
}
