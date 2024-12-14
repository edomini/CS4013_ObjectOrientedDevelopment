package payrollsystem;

import java.time.LocalDate;

/**
 * Represents a payslip for an employee, including all earnings and deductions.
 */
public class Payslip {
    private final Employee employee;
    private final LocalDate payDate;
    private double nettPay;
    private final int rank;
    private final String position;
    private final double basePay;
    private double hoursWorked;
    private double hourlyRate;

    /**
     * Constructs a Payslip for an employee, using the simulated payroll date.
     * Initializes pay-related fields based on the employee's rank and type (full-time or part-time).
     *
     * @param employee The employee for whom the payslip is generated.
     */
    public Payslip(Employee employee) {
        this.employee = employee;
        int year = PayRollSystem.simulatedDate.getYear();
        int month = PayRollSystem.simulatedDate.getMonthValue();
        this.payDate = LocalDate.of(year, month, 25);
        this.position = employee.getPosition().getTitle();
        this.rank = employee.getRank();
        this.basePay = employee.calculateBasePay();
        if (employee instanceof PartTimeEmployee) {
            this.hoursWorked = ((PartTimeEmployee) employee).getHoursWorked();
            this.hourlyRate = ((PartTimeEmployee) employee).getHourlyRate();
        }
    }

    /**
     * Gets the date of the payment.
     *
     * @return The date of the payslip.
     */
    public LocalDate getPayDate() {
        return payDate;
    }

    /**
     * Calculates the PRSI deduction, which is 4% of the base pay.
     *
     * @return The calculated PRSI deduction.
     */
    public double calcPRSI() {
        return basePay * 0.04;
    }

    /**
     * Calculates the Universal Social Charge (USC) based on the base pay, using tiered rates.
     *
     * @return The calculated USC deduction.
     */
    public double calcUSC() {
        double USC = 0;
        //on income <= 12012
        if (basePay <= 12012) {
            USC = basePay * 0.005;
            // on income made between 12013 and 21295
        } else if (basePay <= 21295) {
            USC = (12012 * 0.005) + ((basePay - 12012) * 0.02);
            // on income made  between 21296 and 70044
        } else if (basePay <= 70044) {
            USC = (12012 * 0.005) + ((21296 - 12012) * 0.02) + (basePay * 0.045);
        } else {
            USC = (12012 * 0.005) + ((21296 - 12012) * 0.02) + ((70044 - 21296) * 0.045) + (basePay * 0.08);
        }
        return USC;
    }

    /**
     * Calculates the Pay As You Earn (PAYE) deduction based on the base pay.
     * The first €36,800 is taxed at 20%, and the remainder is taxed at 40%.
     *
     * @return The calculated PAYE deduction.
     */
    public double calcPaye() {
        double PAYE = 0;
        //20% on income up to 36800
        if (basePay <= 36800) {
            PAYE = basePay * 0.2;
        } else {
            // remaining part off the salary = salary - 36800 that's already taxed
            PAYE = PAYE + ((basePay - 36800) * 0.4);
        }
        return PAYE;

    }

    /**
     * Calculates the union fees, which are 1% of the base pay.
     *
     * @return The calculated union fees.
     */
    public double calcUnionFees() {
        return 0.1 * basePay;
    }

    /**
     * Calculates the net pay by subtracting all deductions from the base pay.
     *
     * @return The net pay.
     */
    public double calcNetPay() {
        nettPay = basePay - (calcUSC() + calcPaye() + calcUnionFees() + calcPRSI() + employee.getHealthInsurance());
        return nettPay;
    }

    /**
     * Prints the payslip in a formatted manner, including all earnings and deductions.
     * Includes hours worked and hourly rate for part-time employees.
     */
    public void printPaySlip() {
        String name = "UL Payroll System";
        int employeeID = employee.getEmployeeId();
        String employeeName = employee.getName();

        System.out.println("********************PAYSLIP********************");
        System.out.println("Company Name: " + name);
        System.out.println("Employee ID: " + employeeID);
        System.out.println("Employee Name: " + employeeName);
        System.out.println("Employee Position: " + position);
        System.out.println("Employee Rank: " + rank);
        System.out.println("Payment Date: " + payDate.format(PayRollSystem.DATE_FORMAT));
        System.out.println("----------------------------------------------");

        //deductions
        double totalDeductions = (calcUSC() + calcPaye() + calcUnionFees() + calcPRSI() + employee.getHealthInsurance());
        System.out.printf("Deductions\nHealth Insurance: €%.2f\nUSC: €%.2f\nIncome Tax: €%.2f\nPRSI: €%.2f\nUnion Fees: €%.2f\nTotal Deductions: €%.2f\n", employee.getHealthInsurance(), calcUSC(), calcPaye(), calcPRSI(), calcUnionFees(), totalDeductions);
        System.out.println("----------------------------------------------");

        //summary
        System.out.println("Summary");
        // checks if the employee is an instance of the Part time employee class
        if (employee instanceof PartTimeEmployee) {
            System.out.printf("Hours worked : %.2f\nPay Rate : €%.2f\n", hoursWorked, hourlyRate);
        }
        System.out.printf("Gross Pay : €%.2f\nNet Pay : €%.2f\nPay method : Bank Transfer\n", basePay, calcNetPay());
        System.out.println("***********************************************");
    }
}
