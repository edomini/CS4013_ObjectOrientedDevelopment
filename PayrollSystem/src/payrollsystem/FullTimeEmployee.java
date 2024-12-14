package payrollsystem;

import java.util.ArrayList;

/**
 * Represents a full-time employee and their salary details.
 * Inherits from the abstract Employee class.
 */
public class FullTimeEmployee extends Employee {
    private int rankIndex;
    private double annualSalary;
    private double basePay;

    /**
     * Constructs an instance of FullTimeEmployee based on details parsed from a CSV string,
     * Passes these details to the parent Employee class constructor.
     *
     * @param details A comma-separated string containing the employee's information.
     */
    public FullTimeEmployee(String details) {
        String[] d = details.split(",");
        super(d[0], d[1], d[2], d[3], d[4], d[5]);
    }

    /**
     * Calculates and returns the monthly base pay for the employee.
     *
     * @return The monthly base pay.
     */
    @Override
    public double calculateBasePay() {
        ArrayList<Double> salaryScale = getPosition().getPayScale();

        setRankIndex();
        if (getRankIndex() < 0 || getRankIndex() >= salaryScale.size()) {
            throw new IllegalArgumentException("Rank out of bounds for the salary scale.");
        }
        setAnnualSalary(salaryScale);
        setBasePay();
        return basePay;
    }

    /**
     * Gets the rank index, which is the index of the employee's rank in the salary scale.
     *
     * @return The rank index.
     */
    public int getRankIndex() {
        return rankIndex;
    }

    /**
     * Sets the rank index based on the employee's rank, ensuring it reflects the salary scale.
     */
    public void setRankIndex() {
        this.rankIndex = getRank() - 1;
    }

    /**
     * Returns the annual salary of the employee.
     *
     * @return The annual salary.
     */
    public double getAnnualSalary() {
        return annualSalary;
    }

    public void setAnnualSalary(ArrayList<Double> salaryScale) {
        this.annualSalary = salaryScale.get(getRankIndex());
        setBasePay();
    }

    /**
     * Returns the monthly base pay of the employee.
     *
     * @return The monthly base pay.
     */
    public double getBasePay() {
        return basePay;
    }

    /**
     * Sets the monthly base pay for the employee by dividing the annual salary by 12.
     */
    public void setBasePay() {
        this.basePay = getAnnualSalary() / 12;
    }
}
