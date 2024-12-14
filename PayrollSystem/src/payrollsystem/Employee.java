package payrollsystem;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * An abstract class representing an employee in the payroll system.
 */
public abstract class Employee {
    private int employeeId;
    private String name;
    private final String phoneNumber;
    private String email;
    protected int rank;
    protected Position position;
    private LocalDate dateOfLastPromotion;
    private double healthInsurance;
    private final ArrayList<Payslip> historicalPayslips;
    private Boolean promotionDue;
    private String promotionPos;
    private LocalDate reachedTopOfScale;
    private static int numberOfEmployees = 0;

    /**
     * Constructor for the Employee class.
     *
     * @param pName         the name of the employee
     * @param pNumber       the phone number of the employee
     * @param pPositionName the position title of the employee
     * @param pRank         the rank of the employee
     * @param pDate         the date of the last promotion
     * @param pInsurance    the level of health insurance
     */
    public Employee(String pName, String pNumber, String pPositionName, String pRank, String pDate, String pInsurance) {
        name = pName;
        email = name.toLowerCase().replace(" ", ".") + "@ul.ie";// Generate email from name
        phoneNumber = pNumber;
        position = PayRollSystem.getPositionOfName(pPositionName);

        // Generate unique ID based on position category
        int accessSeries = 1000;
        switch (position.getCategory()) {
            case "Human Resources":
                accessSeries = 3000;
                break;
            case "Administrative":
                accessSeries = 2000;
                break;
            default:
                break;
        }
        employeeId = accessSeries + numberOfEmployees;
        rank = Integer.parseInt(pRank);// Convert rank from string to integer
        promotionDue = false;
        String[] dateInfo = pDate.split("/");

        // Parse date of last promotion from string to LocalDate
        dateOfLastPromotion = LocalDate.of(2000 + Integer.parseInt(dateInfo[2]),
                Integer.parseInt(dateInfo[1]), Integer.parseInt(dateInfo[0]));

        // Determine health insurance cost based on the level provided
        switch (pInsurance.toLowerCase()) {
            case "basic":
                healthInsurance = 40;
                break;
            case "standard":
                healthInsurance = 60;
                break;
            case "enhanced":
                healthInsurance = 100;
                break;
            case "premium":
                healthInsurance = 140;
        }
        numberOfEmployees++;
        historicalPayslips = new ArrayList<>(1);
        if (position.getPayScale().size() == rank)
            setReachedTopOfScale(dateOfLastPromotion);// if by the time an employee is initialized, they are already at the top of their payScale, we set the reachedTopOfScale to the date of their last promotion
    }

    /**
     * Gets the employee's ID.
     *
     * @return the unique employee ID, generated when the employee is created
     */
    public int getEmployeeId() {
        return employeeId;
    }

    /**
     * Gets the employee's name.
     *
     * @return the name of the employee
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the employee's phone number.
     *
     * @return the contact phone number of the employee
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Gets the employee's position.
     *
     * @return the position object associated with the employee
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the cost of the employee's health insurance.
     *
     * @return the monthly health insurance cost for the employee
     */
    public double getHealthInsurance() {
        return healthInsurance;
    }

    /**
     * Gets the employee's current rank.
     *
     * @return the rank of the employee within their position's pay scale
     */
    public int getRank() {
        return rank;
    }

    /**
     * Gets the date when the employee reached the top of their salary scale.
     *
     * @return the date the employee reached the top rank, or null if not reached
     */
    public LocalDate getReachedTopOfScale() {
        return reachedTopOfScale;
    }

    /**
     * Gets the date of the employee's last promotion.
     *
     * @return the LocalDate object representing the last promotion date
     */
    public LocalDate getLastPromotionDate() {
        return dateOfLastPromotion;
    }

    /**
     * Checks if the employee is eligible for a promotion.
     *
     * @return true if the employee is due for a promotion, false otherwise
     */
    public Boolean getPromotionDue() {
        return promotionDue;
    }

    /**
     * Gets the employee's historical payslips.
     *
     * @return a list of all payslips generated for the employee
     */
    public ArrayList<Payslip> getHistoricalPayslips() {
        return historicalPayslips;
    }

    /**
     * Gets the role of the employee based on their position and employment type.
     *
     * @return the role of the employee
     */
    public String getRole() {
        String role;
        if (this instanceof PartTimeEmployee) {
            role = "Part-Time ";
        } else {
            role = "Full-Time ";
        }
        switch (position.getCategory()) {
            case "Administrative":
                role += "Administrator";
                break;
            case "Human Resources":
                role += "Human Resources";
                break;
            default:
                role += "Basic Employee";
                break;
        }
        return role;
    }

    /**
     * Sets the date of the last promotion for the employee.
     *
     * @param dateOfLastPromotion the date of the last promotion
     */
    public void setDateOfLastPromotion(LocalDate dateOfLastPromotion) {
        this.dateOfLastPromotion = dateOfLastPromotion;
    }

    /**
     * Sets the position of the employee.
     *
     * @param position the new position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * Sets the employee's position to be promoted to
     *
     * @param newPos name of the new position
     */
    public void setPromotionPos(String newPos) {
        this.promotionPos = newPos;
    }

    /**
     * Gets the position that the employee is to be promoted to
     *
     * @return The name of new position.
     */
    public String getPromotionPos() {
        return promotionPos;
    }

    /**
     * Sets the rank of the employee.
     *
     * @param rank the new rank
     */
    public void setRank(int rank) {
        this.rank = Math.min(rank, position.getPayScale().size());
    }

    /**
     * Sets the date when the employee reached the top of their salary scale.
     *
     * @param y the date the employee reached the top of the scale
     */
    public void setReachedTopOfScale(LocalDate y) {
        this.reachedTopOfScale = y;
    }

    /**
     * Marks whether the employee is due for a promotion.
     *
     * @param b true if promotion is due, false otherwise
     */
    public void setPromotionDue(boolean b) {
        this.promotionDue = b;
    }

    /**
     * Adds a payslip to the employee's historical records if it doesn't already exist.
     *
     * @param p the payslip to be added
     */
    public void addPayslip(Payslip p) {
        boolean exists = false;
        for (Payslip ps : historicalPayslips) {
            if (p.getPayDate().equals(ps.getPayDate())) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            historicalPayslips.add(p);
        }
    }

    /**
     * Calculates the base pay of the employee. This method is implemented in
     * subclasses for full-time and part-time employees.
     *
     * @return the base pay of the employee
     */
    public abstract double calculateBasePay();


    /**
     * Increments the rank of the employee by 1. If the employee reaches the top of
     * the pay scale, records the date they reached the top rank.
     */
    public void incrementRank() {
        ArrayList<Double> salaryScale = getPosition().getPayScale();
        if (getRank() < salaryScale.size()) {// Check if not at top rank
            if (getRank() == salaryScale.size() - 1) {
                setRank(getRank() + 1);
                setReachedTopOfScale(PayRollSystem.simulatedDate);
            } else {
                setRank(getRank() + 1);
            }
        }
    }

    /**
     * Converts the employee object to a formatted string representation.
     *
     * @return a string containing the employee's details
     */
    @Override
    public String toString() {
        return String.format(
                "Employee ID: %d\nName: %s\nEmail: %s\nRank: %d\nCategory: %s\nPosition: %s\nLast Promotion Date: %s\nHealth Insurance: %.2f",
                employeeId,
                name,
                email,
                rank,
                position.getCategory(),
                position.getTitle(),
                dateOfLastPromotion.format(PayRollSystem.DATE_FORMAT),
                healthInsurance
        );
    }
}
