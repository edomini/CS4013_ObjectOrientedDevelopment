package payrollsystem;

import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Represents a part-time employee and their associated pay details.
 * Inherits from the abstract Employee class.
 */
public class PartTimeEmployee extends Employee {
    private double hoursWorked;
    private LocalDate lastClaimDate;
    private double hourlyRate;
    private int rankIndex;
    private double annualSalary;
    private double unpaidHours; //hours from late pay-claim submission from the previous month

    /**
     * Constructs an instance of PartTimeEmployee based on details parsed from a CSV string,
     * Passes these details to the parent Employee class constructor.
     * Initializes hours worked and calculates pay-related attributes based on position and rank.
     *
     * @param details A comma-separated string containing the employee's information.
     * @throws IllegalArgumentException if the employee's rank is out of bounds for the salary scale.
     */
    public PartTimeEmployee(String details) {
        String[] d = details.split(",");
        super(d[0], d[1], d[2], d[3], d[4], d[5]);
        this.hoursWorked = 0;

        ArrayList<Double> salaryScale = getPosition().getPayScale();
        setRankIndex();
        if (getRankIndex() < 0 || getRankIndex() >= salaryScale.size()) {
            throw new IllegalArgumentException("Rank out of bounds for the salary scale: " + getRankIndex());
        }
        setAnnualSalary(salaryScale);
        setHourlyRate();
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
     * Gets the annual salary for the employee.
     *
     * @return The annual salary.
     */
    public double getAnnualSalary() {
        return annualSalary;
    }

    /**
     * Sets the annual salary based on the rank index and salary scale.
     *
     * @param salaryScale The salary scale for the position.
     */
    public void setAnnualSalary(ArrayList<Double> salaryScale) {
        this.annualSalary = salaryScale.get(getRankIndex());
        setHourlyRate();
    }

    /**
     * Gets the hourly rate for the employee.
     *
     * @return The hourly rate.
     */
    public double getHourlyRate() {
        return hourlyRate;
    }

    /**
     * Sets the hourly rate by dividing the annual salary by 1440 (assumed work hours per year).
     */
    public void setHourlyRate() {
        this.hourlyRate = getAnnualSalary() / 1440;
    }

    /**
     * Calculates the base pay for the pay period based on hours worked and hourly rate.
     *
     * @return The calculated base pay.
     */
    @Override
    public double calculateBasePay() {
        return hourlyRate * hoursWorked;
    }

    /**
     * Finds the second Friday of the given month and year.
     *
     * @param simDate The date to base the calculation on (usually the simulated date used when advancing time in the menu class).
     * @return The date of the second Friday.
     */
    private LocalDate findSecondFriday(LocalDate simDate) {
        int year = simDate.getYear();
        int month = simDate.getMonthValue();
        LocalDate date = LocalDate.of(year, month, 1);

        int fridayCount = 0;

        // Loop through the days of the month
        while (date.getMonthValue() == month) {
            if (date.getDayOfWeek() == DayOfWeek.FRIDAY) {
                fridayCount++;
                if (fridayCount == 2) {
                    break; // Stop when the second Friday is found
                }
            }
            date = date.plusDays(1);
        }

        return date; // Return the date of the second Friday
    }

    /**
     * Checks whether the last pay-claim was submitted on time.
     *
     * @return True if the claim was submitted on time, false otherwise.
     */
    public boolean isClaimSubmittedOnTime() {
        if (lastClaimDate == null) {
            return false;
        } else {
            return lastClaimDate.isBefore(findSecondFriday(PayRollSystem.simulatedDate)) &&
                    lastClaimDate.isAfter(findSecondFriday(PayRollSystem.simulatedDate.minusMonths(1)));
        }
    }

    /**
     * Gets the total hours worked in the current period.
     *
     * @return The hours worked.
     */
    public double getHoursWorked() {
        return hoursWorked;
    }

    /**
     * Adds or resets the hours worked.
     *
     * @param hoursWorked Hours to add or set to 0 to reset.
     */
    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    /**
     * Gets the unpaid hours due to late claims.
     *
     * @return The unpaid hours.
     */
    public double getUnpaidHours() {
        return unpaidHours;
    }

    /**
     * Adds or resets the unpaid hours.
     *
     * @param unpaidHours Unpaid hours to add or reset to 0.
     */
    public void setUnpaidHours(double unpaidHours) {
        if (unpaidHours == 0) {
            this.unpaidHours = unpaidHours; //setting it to zero clears it
        } else {
            this.unpaidHours += unpaidHours; //otherwise these can stack from late acclaim submissions until next payslip is generated
        }
    }

    /**
     * Gets the last pay-claim date.
     *
     * @return The last claim date.
     */
    public LocalDate getLastClaimDate() {
        return lastClaimDate;
    }

    /**
     * Sets the last pay-claim date.
     *
     * @param lastClaimDate The date of the last claim.
     */
    public void setLastClaimDate(LocalDate lastClaimDate) {
        this.lastClaimDate = lastClaimDate;
    }

    /**
     * Makes a formatted string with part-time employee details, including hours worked and hourly rate.
     *
     * @return a string containing the employee's details
     */
    @Override
    public String toString() {
        if (lastClaimDate != null) {
            return super.toString() + String.format(
                    "\nHours Worked: %.2f\nLast Claim Date: %s\nHourly Rate: €%.2f",
                    hoursWorked,
                    lastClaimDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    hourlyRate);
        } else {
            return super.toString() + String.format(
                    "\nHours Worked: %.2f\nHourly Rate: €%.2f",
                    hoursWorked,
                    hourlyRate);
        }
    }
}
