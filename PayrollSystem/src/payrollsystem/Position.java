package payrollsystem;

import java.util.ArrayList;

/**
 * Represents a position within the university payroll system
 * Has a position title, a category, and a payscale
 */
public class Position {
    private final String title;
    private final ArrayList<Double> payScale;
    private String category;

    /**
     * Constructs a new Position object
     *
     * @param category The category of the position.
     * @param line     a comma separated list containing the title and pay scale
     */
    public Position(String category, String line) {
        setCategory(category);
        String[] payStr = line.split(",");
        this.title = payStr[0];
        ArrayList<Double> payscaleArrayList = new ArrayList<>(1);
        for (int i = 1; i < payStr.length; i++) {
            payscaleArrayList.add(Double.parseDouble(payStr[i]));
        }
        payScale = payscaleArrayList;
    }

    /**
     * Sets the category of the position, ensuring proper casing.
     *
     * @param categoryString the category String to be formatted.
     */
    private void setCategory(String categoryString) {
        String[] words = categoryString.split(" ");
        String categoryCaseCorrect = "";
        for (String w : words) {
            String w3 = w;
            if (!w.equals("UL")) {
                w = w.toLowerCase();
                String w1 = w.substring(0, 1).toUpperCase();
                String w2 = w.substring(1);
                w3 = w1 + w2;
            }
            categoryCaseCorrect += w3 + ' ';
        }
        category = categoryCaseCorrect.trim();
    }

    /**
     * Returns the pay scale for this position.
     *
     * @return the payscale as an ArrayList of Doubles.
     */
    public ArrayList<Double> getPayScale() {
        return this.payScale;
    }

    /**
     * Returns the title of this position.
     *
     * @return The title of the position.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the category of this position.
     *
     * @return The category of this position.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Returns a string representation of the position, including category, title and pay scale.
     *
     * @return A formatted string containing position details.
     */
    public String toString() {
        StringBuilder arrayOfPay = new StringBuilder("Pay Grades: ");
        for (double p : payScale) {
            String pay = String.format("%.2f", p);
            arrayOfPay.append("€" + pay + ", ");
        }
        arrayOfPay.delete(arrayOfPay.length() - 2, arrayOfPay.length());
        String arrayOfPayStr = arrayOfPay.toString();
        return String.format("Category: %s Title: %s%nMax Rank: %d%n%s", this.category, this.title, payScale.size(), arrayOfPayStr);
    }
}
