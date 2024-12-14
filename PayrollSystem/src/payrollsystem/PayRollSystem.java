package payrollsystem;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Manages the back end of the payroll system, including employees, positions and payslips.
 */
public class PayRollSystem {
    public static ArrayList<Employee> employeeArrayList = new ArrayList<>(1);
    public static ArrayList<Position> positionArrayList = new ArrayList<>(1);
    public static LocalDate simulatedDate = LocalDate.now();
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yy");
    public static ArrayList<String> passwordList = new ArrayList<>();

    /**
     * Constructs a new PayRollSystem object, initialising positions, employees and printing info
     */
    public PayRollSystem() {
        generatePositions();
        generateEmployees();
        loadPasswords();
        generatePastPayslips();
        System.out.println("A List Of All Employees for Demonstration Purposes Only");
        printEmployeeIdsAndNames();
    }

    /**
     * Prints the IDs, names, and roles of all employees in the system.
     */
    public void printEmployeeIdsAndNames() {
        System.out.printf("%-5s: %-20s: %-30s\t\t%-5s: %-20s: %-30s\n", "ID", "Name", "Role", "ID", "Name", "Role");
        for (int i = 0; i < employeeArrayList.size(); i += 2) {
            // Get the first employee in the pair
            Employee firstEmployee = employeeArrayList.get(i);
            String firstInfo = String.format("%-5d: %-20s: %-30s", firstEmployee.getEmployeeId(), firstEmployee.getName(), firstEmployee.getRole());
            String secondInfo = "";
            if (i + 1 < employeeArrayList.size()) {
                Employee secondEmployee = employeeArrayList.get(i + 1);
                secondInfo = String.format("\t\t%-5d: %-20s: %-30s", secondEmployee.getEmployeeId(), secondEmployee.getName(), secondEmployee.getRole());
            }
            System.out.println(firstInfo + secondInfo);
        }
        System.out.println("The password is always employee name with no spaces or apostrophes followed by 123.\n" +
                "The Human Resources password is HR123.\n");
    }


    /**
     * Generates the list of positions from the PositionDetails.csv file, to be used in the construction of employee objects
     */
    private void generatePositions() {
        String filePath = "PositionDetails.csv";
        try {
            String contentStr = Files.readString(Path.of("PositionDetails.csv"));
            String[] blocks = contentStr.split("(\\r?\\n){2}");
            for (String b : blocks) {
                ArrayList<String> lines = new ArrayList<>(1);
                Scanner scanner = new Scanner(b);
                while (scanner.hasNextLine()) {
                    lines.add(scanner.nextLine());
                }
                for (int i = 1; i < lines.size(); i++) {
                    positionArrayList.add(new Position(lines.getFirst(), lines.get(i)));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates the list of positions from the EmployeeDetails.csv file
     */
    private void generateEmployees() {
        String filePath = "EmployeeDetails.csv";
        ArrayList<Employee> empList = new ArrayList<>(1);
        try {
            File employeesFile = new File(filePath);
            Scanner scanner = new Scanner(employeesFile);
            while (scanner.hasNextLine()) {
                String employeeInfo = scanner.nextLine();
                String[] partsOfEmployeeInfo = employeeInfo.split(",");

                if (partsOfEmployeeInfo.length == 7) {
                    empList.add(new PartTimeEmployee(employeeInfo));
                } else if (partsOfEmployeeInfo.length == 6) {
                    empList.add(new FullTimeEmployee(employeeInfo));
                } else {
                    throw new PayRollException("Fault in Employee CSV formatting");
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + filePath);
        }

        employeeArrayList = empList;
    }

    /**
     * Returns the position object of the given title.
     *
     * @param name the name of the position to be found
     * @return The position with the given title
     * @throws PayRollException If no position with the given name exists.
     */
    public static Position getPositionOfName(String name) {
        boolean positionFound = false;
        for (Position p : positionArrayList) {
            if (p.getTitle().equalsIgnoreCase(name)) return p;
        }
        if (!positionFound) throw new PayRollException(name + ", no such position exists");
        return null;
    }

    /**
     * Adds a new employee to the system, and writes it to a running csv of details
     *
     * @param e The employee to be added
     */
    public void addEmployee(Employee e) {
        employeeArrayList.add(e);
        System.out.println("Employee " + e.getName() + " added to the system.");
        updateEmployeeCSV();
        updateEmployeeCSV();
        String newPassword = e.getName().replaceAll("['\\s]", "") + "123";
        passwordList.add(newPassword);
        updatePasswordCSV();

    }

    public void removeEmployee(Employee employee) {
        if (employeeArrayList.contains(employee)) {
            employeeArrayList.remove(employee);
            System.out.println("Employee " + employee.getName() + " with ID " + employee.getEmployeeId() + " removed from the system.");
            updateEmployeeCSV();

            // Remove password
            String passwordToRemove = employee.getName().replaceAll("['\\s]", "") + "123";
            if (passwordList.remove(passwordToRemove)) {
                updatePasswordCSV();
            }
        } else {
            System.out.println("Employee " + employee.getName() + " not found in the system.");
        }
    }

    /**
     * Completes the promotion action with the promotion offered to the given employee
     *
     * @param e      The employee accepting the promotion
     * @param newPos The title of the position to which they are being promoted
     */
    public void executePromotion(Employee e, String newPos) {
        Position p = getPositionOfName(newPos);
        int newRank;
        if (e.getReachedTopOfScale() != null) {
            int diffYear = simulatedDate.getYear() - e.getReachedTopOfScale().getYear();
            if (diffYear > p.getPayScale().size()) {
                newRank = p.getPayScale().size();
            } else {
                newRank = diffYear;
            }
        } else {
            newRank = 1;
        }
        e.setPosition(p);
        e.setRank(newRank);
        e.setDateOfLastPromotion(simulatedDate);
        updateEmployeeCSV();
    }

    /**
     * Offers a new position to a given employee
     *
     * @param employeeID The ID number of the employee being offered the position
     * @param newPos     the title of the offered promotion position
     */
    public void offerPromotion(int employeeID, String newPos) {
        Employee e = employeeArrayList.get(employeeID % 1000);
        if (e instanceof PartTimeEmployee) return;
        //a check to throw an exception if the position doesn't exist
        Position testPos = getPositionOfName(newPos);
        e.setPromotionPos(newPos);
        e.setPromotionDue(true);
    }

    /**
     * Method to check if it is appropriate to move employees up their salary scale
     */
    public void moveUpSalaryScale() {
        //check if the month is October because that is when employee move up the salary scale
        if (simulatedDate.getMonthValue() == 10) {
            //loop to go through each employee in the arraylist
            for (Employee e : employeeArrayList) {
                //checks if the employee was promoted this year already - if false then employee is eligible for annual rank increase.
                if (e.getLastPromotionDate().getYear() < simulatedDate.getYear()) {
                    e.incrementRank();
                    updateEmployeeCSV();
                }
            }
        }
    }

    /**
     * Advances the simulated date by one month and then performs all periodic functions of the payroll system
     */
    public void advanceMonth() {
        simulatedDate = simulatedDate.plusMonths(1);
        moveUpSalaryScale();
        //added check for day < 25
        if (LocalDate.now().getDayOfMonth() >= 25) {
            for (Employee e : employeeArrayList) {
                if (e instanceof FullTimeEmployee) {
                    e.addPayslip(new Payslip(e));
                } else if (e instanceof PartTimeEmployee) {
                    if (((PartTimeEmployee) e).isClaimSubmittedOnTime() ||
                            (((PartTimeEmployee) e).getUnpaidHours() != 0 && ((PartTimeEmployee) e).getLastClaimDate().isBefore(simulatedDate))) {
                        ((PartTimeEmployee) e).setHoursWorked(((PartTimeEmployee) e).getHoursWorked() + ((PartTimeEmployee) e).getUnpaidHours());
                        e.addPayslip(new Payslip(e));
                        ((PartTimeEmployee) e).setHoursWorked(0);
                        ((PartTimeEmployee) e).setUnpaidHours(0);
                    }
                }
            }
        } else {
            simulatedDate = simulatedDate.minusMonths(1);
            for (Employee e : employeeArrayList) {
                if (e instanceof FullTimeEmployee) {
                    e.addPayslip(new Payslip(e));
                } else if (e instanceof PartTimeEmployee) {
                    if (((PartTimeEmployee) e).isClaimSubmittedOnTime() ||
                            (((PartTimeEmployee) e).getUnpaidHours() != 0 && ((PartTimeEmployee) e).getLastClaimDate().isBefore(simulatedDate))) {
                        ((PartTimeEmployee) e).setHoursWorked(((PartTimeEmployee) e).getHoursWorked() + ((PartTimeEmployee) e).getUnpaidHours());
                        e.addPayslip(new Payslip(e));
                        ((PartTimeEmployee) e).setHoursWorked(0);
                        ((PartTimeEmployee) e).setUnpaidHours(0);
                    }
                }
            }
            simulatedDate = simulatedDate.plusMonths(1);
        }
    }

    /**
     * Advances the month 12 times
     */
    public void advanceYear() {
        for (int i = 0; i < 12; i++) {
            advanceMonth();
        }
    }

    /**
     * Loads the passwords from the PasswordFile.csv into the passwordList ArrayList.
     * If the file does not exist, it creates a new empty file.
     */
    private void loadPasswords() {
        String filePath = "PasswordFile.csv";
        try {
            File passwordFile = new File(filePath);
            if (!passwordFile.exists()) {
                System.err.println("Password file not found. Creating a new one.");
                passwordFile.createNewFile();
                return;
            }

            Scanner scanner = new Scanner(passwordFile);
            while (scanner.hasNextLine()) {
                String password = scanner.nextLine().trim();
                if (!password.isEmpty()) {
                    passwordList.add(password);
                }
            }
            scanner.close();
        } catch (IOException e) {
            System.err.println("Error loading password file.");
            e.printStackTrace();
        }
    }

    /**
     * Updates the PasswordFile.csv to reflect the current passwords in the passwordList ArrayList.
     */
    private void updatePasswordCSV() {
        String filePath = "PasswordFile.csv";
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String password : passwordList) {
                writer.write(password + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error updating the password file.");
            e.printStackTrace();
        }
    }

    /**
     * Method that rewrites the CurrentEmployeeDetails.csv file
     */
    private void updateEmployeeCSV() {
        String filePath = "CurrentEmployeeDetails.csv";
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Employee e : employeeArrayList) {
                String healthInsurance;
                switch ((int) e.getHealthInsurance()) { // Cast to int if necessary
                    case 40:
                        healthInsurance = "Basic";
                        break;
                    case 60:
                        healthInsurance = "Standard";
                        break;
                    case 100:
                        healthInsurance = "Enhanced";
                        break;
                    case 140:
                        healthInsurance = "Premium";
                        break;
                    default:
                        healthInsurance = "NULL";
                }

                String dateFormatted = e.getLastPromotionDate().format(DATE_FORMAT); // Format the date

                String line = String.format("%s,%s,%s,%d,%s,%s",
                        e.getName(),
                        e.getPhoneNumber(),
                        e.getPosition().getTitle(),
                        e.getRank(),
                        dateFormatted, // Use formatted date
                        healthInsurance);

                if (e instanceof PartTimeEmployee) {
                    line += ",Parttime"; // Add "Parttime" if applicable
                }

                writer.write(line + System.lineSeparator()); // Write to CSV
            }
        } catch (IOException e) {
            System.err.println("Error updating the CSV file.");
            e.printStackTrace();
        }
    }

    /**
     * Generates past payslips for employees starting from four months prior to the current date.
     * Simulates a timeline, advancing the month, and generates payslips for eligible employees.
     */
    public void generatePastPayslips() {
        simulatedDate = LocalDate.now().minusMonths(5);
        int monthsIncl25th;
        if (simulatedDate.getDayOfMonth() >= 25) {
            monthsIncl25th = 5;
        } else {
            monthsIncl25th = 4;
        }
        for (int i = 0; i < monthsIncl25th; i++) {
            advanceMonth();
        }
        simulatedDate = LocalDate.now();
    }
}
