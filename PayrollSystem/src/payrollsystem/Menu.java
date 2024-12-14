package payrollsystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Menu class to run and operate the command line interface and associated functions
 */
public class Menu {
    private final Scanner in;

    public Menu() {
        in = new Scanner(System.in);
    }

    /**
     * Runs the payroll system.
     *
     * @param payroll the payroll system instance
     */
    public void run(PayRollSystem payroll) {
        boolean more = true;
        boolean evenMore = true;

        while (more) {
            String options = "LQ";
            System.out.println("L)og In\t\tQ)uit");
            String command;

            // fixed issue where in would process the enter key as the command
            do {
                command = in.nextLine().toUpperCase();
            } while (command.isEmpty());

            // make sure the user can only enter valid commands and only the ones accessible to them
            try {
                do {
                    if (!options.contains(command)) {
                        throw new PayRollException("Invalid Command.\n" + "   ----------");
                    }
                    // unreachable method if not in separate if checks
                    if (!options.contains(command)) {
                        System.out.print("L)og In\tQ)uit\n");
                        command = in.nextLine().toUpperCase();
                    }
                } while (!options.contains(command));
            } catch (PayRollException ex) {
                System.out.println(ex.getMessage());
            }

            if (command.equals("L")) {
                evenMore = true;
                Employee employee;
                int idNum = 0;

                try {
                    employee = logIn();
                    idNum = employee.getEmployeeId();
                    if(findUserType(idNum) == 3){
                        System.out.println("\nLogged in as HR.");
                    } else {
                        System.out.println("\nWelcome, " + employee.getName() + ".");
                        System.out.println(employee.getRole() + ".");
                    }
                } catch (PayRollException ex) {
                    System.out.println(ex.getMessage()); // Print the exception message
                    continue; // Go back to the main menu loop
                }

                // Decides which commands are accessible to the logged in employee
                String launchS = "D)etails\tV)iew Recent Payslip\tH)istorical Payslips\nC)urrent Role Info\t";
                String allowed = "DVHLTC";
                if (findUserType(idNum) == 1) {
                    //default case
                } else if (findUserType(idNum) == 2) {
                    launchS += "A)dd Employee\tR)emove Employee\t";
                    allowed += "AR";
                } else if (findUserType(idNum) == 3) {
                    launchS = "E)mployee Details\tP)romote an Employee\t";
                    allowed += "EP";
                }
                if (employee instanceof PartTimeEmployee) {
                    launchS += "S)ubmit Pay Claim\t";
                    allowed += "S";
                }
                if (employee.getPromotionDue()) {
                    launchS += "M)anage Promotion Offers\t";
                    allowed += "M";
                }
                launchS += "T)ime Simulation\tL)og Out\n";

                while (evenMore) {
                    // print the options specific to the user's accessibility
                    System.out.print("\nDate: " + PayRollSystem.simulatedDate.format(PayRollSystem.DATE_FORMAT) + "\n" + launchS);
                    command = "";

                    do {
                        command = in.nextLine().toUpperCase();
                    } while (command.equals(""));

                    try {
                        do {
                            if (!allowed.contains(command)) {
                                throw new PayRollException("Invalid Command.\n" + "   ----------");
                            }
                            if (!allowed.contains(command)) {
                                System.out.print(launchS);
                                command = in.nextLine().toUpperCase();
                            }
                        } while (!allowed.contains(command));
                    } catch (PayRollException ex) {
                        System.out.println(ex.getMessage());
                    }

                    if (command.equals("D") && allowed.contains("D")) {
                        System.out.println(employee);
                    } else if (command.equals("V") && allowed.contains("V")) {
                        try {
                            if (employee.getHistoricalPayslips().isEmpty()) {
                                throw new PayRollException("No payslips available to view.");
                            } else {
                                employee.getHistoricalPayslips().getLast().printPaySlip();
                            }
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if (command.equals("C") && allowed.contains("C")) {
                        System.out.println(employee.getPosition().toString());
                    } else if (command.equals("H") && allowed.contains("H")) {
                        boolean hOK = false;
                        String selectedAmount = "";
                        try {
                            if (employee.getHistoricalPayslips().isEmpty()) {
                                throw new PayRollException("No payslips available to view.");
                            } else {
                                while (!hOK) {
                                    System.out.printf("Number of Payslips to print (maximum available: %d): ",
                                            employee.getHistoricalPayslips().size());
                                    selectedAmount = in.nextLine();
                                    // check if the amount is <= the array size
                                    try {
                                        if (!selectedAmount.matches("[\\d]")) {
                                            throw new PayRollException("Invalid Input.\n\t----------");
                                        }
                                        if (Integer.parseInt(selectedAmount) > employee.getHistoricalPayslips().size()) {
                                            throw new PayRollException("Maximum Available: " + employee.getHistoricalPayslips().size());
                                        }
                                        hOK = true;
                                    } catch (PayRollException ex) {
                                        System.out.println(ex.getMessage());
                                    }
                                }
                                int i,j;
                                for (i = employee.getHistoricalPayslips().size(), j = Integer.parseInt(selectedAmount); j > 0; i--, j--) {
                                    Payslip p = employee.getHistoricalPayslips().get(i-1);
                                    p.printPaySlip();
                                }
                            }
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if (command.equals("A") && allowed.contains("A")) {
                        //write to the csv file with all details needed to construct another Employee object
                        try {
                            System.out.print("Enter Employee Name: ");
                            String eName = in.nextLine();
                            System.out.print("Enter Employee Phone Number: ");
                            String ePhoneNum = in.nextLine();
                            System.out.print("Enter Employee Position: ");
                            String ePosName = in.nextLine();
                            int eRank = 0;
                            boolean validRank = false;

                            // Validate and parse rank input
                            while (!validRank) {
                                System.out.print("Enter Employee Rank: ");
                                String rankInput = in.nextLine();
                                try {
                                    eRank = Integer.parseInt(rankInput);
                                    validRank = true;
                                } catch (NumberFormatException ex) {
                                    System.out.println("Invalid rank input. Please enter a numeric value for rank.");
                                }
                            }
                            System.out.print("Enter Health Insurance Level (Basic,Standard,Enhanced,Premium): ");
                            String eInsuranceTier = in.nextLine().toLowerCase().trim();
                            if (!eInsuranceTier.equals("basic") &&
                                    !eInsuranceTier.equals("standard") &&
                                    !eInsuranceTier.equals("enhanced") &&
                                    !eInsuranceTier.equals("premium")) {
                                throw new PayRollException("Insurance Tier error");
                            }
                            System.out.println("Is Parttime (\"Yes\" or \"No\"): ");
                            String ePTYorN = in.nextLine().toLowerCase().trim();
                            String isParttime = "";
                            switch (ePTYorN) {
                                case "yes":
                                    isParttime = "Parttime";
                                    break;
                                case "no":
                                    break;
                                default:
                                    throw new PayRollException("It was a yes or no question.");
                            }
                            String details = String.format("%s,%s,%s,%d,%s,%s", eName, ePhoneNum, ePosName, eRank,
                                    PayRollSystem.simulatedDate.format(PayRollSystem.DATE_FORMAT), eInsuranceTier);
                            try {
                                if (isParttime.isEmpty()) {
                                    FullTimeEmployee e = new FullTimeEmployee(details);
                                    payroll.addEmployee(e);
                                } else {
                                    PartTimeEmployee e = new PartTimeEmployee(details);
                                    payroll.addEmployee(e);
                                }
                            } catch (IllegalArgumentException ex) {
                                System.out.println("Error creating employee: " + ex.getMessage());
                            }
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());
                        } catch (InputMismatchException ex) {
                            System.out.println("Inappropriate data field entry");
                        }

                    } else if (command.equals("R") && allowed.contains("R")) {
                        removeEmployee(payroll);
                    } else if (command.equals("E") && allowed.contains("E")) {
                        viewEmployee();
                    } else if (command.equals("M") && allowed.contains("M")) {
                        Position p = PayRollSystem.getPositionOfName(employee.getPromotionPos());
                        int newRank;
                        if (employee.getReachedTopOfScale() != null) {
                            int diffYear = PayRollSystem.simulatedDate.getYear() - employee.getReachedTopOfScale().getYear();
                            if (diffYear > p.getPayScale().size()) {
                                newRank = p.getPayScale().size();
                            } else {
                                newRank = diffYear;
                            }
                        } else {
                            newRank = 1;
                        }
                        System.out.printf("Promotion Offer: %s at rank %d%n", employee.getPromotionPos(), newRank);
                        System.out.println("A)ccept\tR)eject");
                        String response;
                        String allowedResponses = "AR";

                        do {
                            response = in.nextLine().toUpperCase();
                        } while (response.equals(""));

                        try {
                            do {
                                if (!allowedResponses.contains(response)) {
                                    throw new PayRollException("Invalid Command.\n" + "   ----------");
                                }
                                if (!allowedResponses.contains(response)) {
                                    System.out.println("A)ccept\tR)eject");
                                    response = in.nextLine().toUpperCase();
                                }
                            } while (!allowedResponses.contains(response));
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());
                        }

                        if (response.equals("A")) {
                            payroll.executePromotion(employee, employee.getPromotionPos());
                            employee.setPromotionDue(false);
                            System.out.println("Promotion Accepted.");
                            launchS = launchS.replace("M)anage Promotion Offers\t", "");
                            allowed = allowed.replace("M", "");

                        } else if (response.equals("R")) {
                            System.out.println("Promotion Rejected.");
                            launchS = launchS.replace("M)anage Promotion Offers\t", "");
                            allowed = allowed.replace("M", "");
                        }
                    } else if (command.equals("P") && allowed.contains("P")) {
                        boolean pOK = false;
                        String employeeID = "";
                        try {
                            while (!pOK) {
                                System.out.print("Enter Employee ID Number: ");
                                employeeID = in.nextLine();
                                // check if the id number exists
                                try {
                                    idNumberExists(employeeID);
                                    if (employee.getEmployeeId() == Integer.parseInt(employeeID)) {
                                        throw new PayRollException("nice try, no promoting yourself");
                                    }
                                    Employee e = findEmployeeById(Integer.parseInt(employeeID));
                                    if (e instanceof PartTimeEmployee) {
                                        throw new PayRollException("Only full-time employees can be promoted.");
                                    }
                                    pOK = true;
                                } catch (PayRollException ex) {
                                    System.out.println(ex.getMessage());
                                }
                            }
                            System.out.print("Enter New Position: ");
                            String newPos;
                            do {
                                newPos = in.nextLine();
                            } while (newPos.isEmpty());
                            payroll.offerPromotion(Integer.parseInt(employeeID), newPos);
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());
                        }
                    } else if (command.equals("S") && allowed.contains("S")) {
                        System.out.print("Enter Total Hours for Month: ");
                        double hours = in.nextDouble();
                        // submit hours and make payslip for part-time employee
                        ((PartTimeEmployee) employee).setHoursWorked(hours);
                        ((PartTimeEmployee) employee).setLastClaimDate(PayRollSystem.simulatedDate);
                        if (((PartTimeEmployee) employee).isClaimSubmittedOnTime()) {
                            employee.addPayslip(new Payslip(employee));
                            ((PartTimeEmployee) employee).setHoursWorked(0);
                        } else {
                            ((PartTimeEmployee) employee).setUnpaidHours(hours);
                            ((PartTimeEmployee) employee).setHoursWorked(0);
                        }
                    } else if (command.equals("T") && allowed.contains("T")) {
                        System.out.println("Advance Time By One M)onth\tY)ear");
                        String ans;
                        String allowedInput = "MY";

                        do {
                            ans = in.nextLine().toUpperCase();
                        } while (ans.equals(""));

                        try {
                            do {
                                if (!allowedInput.contains(ans)) {
                                    throw new PayRollException("Invalid Command.\n   ----------");
                                }
                                if (!allowedInput.contains(ans)) {
                                    System.out.print("M)onth\tY)ear");
                                    ans = in.nextLine().toUpperCase();
                                }
                            } while (!allowedInput.contains(ans));
                        } catch (PayRollException ex) {
                            System.out.println(ex.getMessage());

                        }
                        if (ans.equals("M")) {
                            payroll.advanceMonth();
                        } else if (ans.equals("Y")) {
                            payroll.advanceYear();
                        }
                    } else if (command.equals("L") && allowed.contains("L")) {
                        evenMore = false;
                        System.out.println("Logged out successfully.");
                    }
                }
            } else if (command.equals("Q")) {
                more = false;
            }
        }
    }

    /**
     * Converts the employee's ID number to their index in the employee array and all CSVs
     *
     * @param inputId ID number of target employee
     * @return employee's position in all arrayLists
     */
    protected int convertToIndex(int inputId) {
        return (inputId % 1000);
    }

    /**
     * Finds an employee by their ID number.
     *
     * @param id The ID number of the employee to find.
     * @return The Employee object corresponding to the given ID.
     * @throws PayRollException If the employee ID does not exist.
     */
    protected Employee findEmployeeById(int id) throws PayRollException {
        for (Employee employee : PayRollSystem.employeeArrayList) {
            if (employee.getEmployeeId() == id) {
                return employee;
            }
        }
        throw new PayRollException("ID Number does not exist: " + id);
    }

    /**
     * Identifies the type of user in the system, HR, admin, or regular employee based on the first digit of their ID
     *
     * @param inputId the id of the target user
     * @return the first digit of their id
     */
    protected int findUserType(int inputId) {
        return (inputId / 1000);
    }

    /**
     * Checks if an ID number exists in the system.
     *
     * @param idInput The ID number to validate.
     * @return True if the ID exists; otherwise, false.
     * @throws PayRollException If the ID is invalid or does not exist.
     */
    protected boolean idNumberExists(String idInput) throws PayRollException {
        boolean found = false;
        if (!Pattern.matches("[0-9]+", idInput) || idInput.length() != 4) {
            throw new PayRollException("Not a valid ID number. Please enter a series of 4 digits.\n" +
                    "   ---------------------------------------------------");
        } else if (convertToIndex(Integer.parseInt(idInput)) < PayRollSystem.employeeArrayList.size()) {
            for (Employee emp : PayRollSystem.employeeArrayList) {
                if (emp.getEmployeeId() == Integer.parseInt(idInput)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) {
            throw new PayRollException("ID Number does not exist. Try again.\n" +
                    "   ------------------------------");
        }
        return found;
    }

    /**
     * Verifies if the given password matches the stored password for the specified ID.
     *
     * @param id            The ID of the employee.
     * @param passwordInput The password to verify.
     * @return True if the password is correct.
     * @throws PayRollException If the password is incorrect or there is a file reading error.
     */
    private boolean passwordIsCorrect(int id, String passwordInput) throws PayRollException {
        Employee e = PayRollSystem.employeeArrayList.get(convertToIndex(id));
        String actualPassword = "";
        try {
            String[] passwordFileContents = Files.readString(Path.of("PasswordFile.csv")).split("(\\r?\\n)");
            actualPassword = passwordFileContents[convertToIndex(id)];
        } catch (IOException ex) {
            throw new PayRollException("file reading error");
        }
        if (!actualPassword.equals(passwordInput)) throw new PayRollException("Password Incorrect. Try again.\n" +
                "   ------------------------");
        return true;
    }

    /**
     * Handles the login process for the payroll system.
     *
     * @return The Employee object of the logged-in user.
     * @throws PayRollException If the login fails due to incorrect ID or password.
     */
    private Employee logIn() throws PayRollException {
        boolean idOK = false;
        boolean passwordOK = false;
        int idNum = 0;

        System.out.println("-- Log In --");

        // ID Validation Loop
        while (!idOK) {
            System.out.print("ID Number: ");
            String idInput = in.nextLine().trim();

            try {
                idNumberExists(idInput);
                idNum = Integer.parseInt(idInput);
                idOK = true;
            } catch (PayRollException ex) {
                System.out.println(ex.getMessage());
            }
        }
        //Max attempts created to prevent soft locks
        int maxAttempts = 3;
        int attempts = 0;
        while (!passwordOK) {
            if (attempts >= maxAttempts) {
                throw new PayRollException("Maximum Login Attempts reached. Returning to main menu.");
            }
            int attemptsLeft = maxAttempts - attempts;
            System.out.print("Enter password (You have " + attemptsLeft + " attempt" + (attemptsLeft > 1 ? "s" : "") + "): ");
            String passwordInput = in.nextLine();
            try {
                passwordIsCorrect(idNum, passwordInput);
                passwordOK = true;
            } catch (PayRollException ex) {
                System.out.println(ex.getMessage());
                attempts++;
            }
        }
        // Find and return the logged-in employee
        return PayRollSystem.employeeArrayList.get(convertToIndex(idNum));
    }

    /**
     * Handles the removal of an employee from the system.
     *
     * @param payroll The instance of the payroll system managing employees.
     */
    private void removeEmployee(PayRollSystem payroll) {
        try {
            System.out.print("Enter the ID of the employee to remove: ");
            int idToRemove = Integer.parseInt(in.nextLine().trim());

            Employee employeeToRemove = findEmployeeById(idToRemove);
            System.out.println("Are you sure you want to remove " + employeeToRemove.getName() + " (ID: " + idToRemove + ")? (yes/no)");
            String confirmation = in.nextLine().trim().toLowerCase();
            if (!confirmation.equals("yes")) {
                System.out.println("Removal canceled.");
                return;
            }
            payroll.removeEmployee(employeeToRemove);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a numeric ID.");
        } catch (PayRollException e) {
            System.out.println(e.getMessage()); // Handle case where ID does not exist
        }
    }

    /**
     * Method to handle the viewing of other employee's details in the system.
     * Outputs in print statements.
     */
    private void viewEmployee() {
        boolean viewIdOK = false;
        int viewID = 0;
        while (!viewIdOK) {
            System.out.print("Enter ID of Employee to View Details: ");
            String idToView = in.nextLine().trim();

            // check if the id number exists in the csv file
            try {
                idNumberExists(idToView);
                viewID = Integer.parseInt(idToView); // if the id is correct, parse it to an int
                viewIdOK = true; // end this loop
            } catch (PayRollException ex) {
                System.out.println(ex.getMessage());
            }
        }
        Employee employeeToView = PayRollSystem.employeeArrayList.get(convertToIndex(viewID));
        System.out.println("\n--- Employee Details ---\n");
        System.out.println(employeeToView);
        System.out.println("-------------------------");
    }

}
