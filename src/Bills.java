package src;

import java.io.Serializable;
import java.math.BigDecimal;

public class Bills implements Serializable {
    private final BigDecimal value;
    private int quantity;

    public Bills(BigDecimal value, int quantity) {
        if (value != null)
            this.value = value;
        else {
            BigDecimal defaultValue = new BigDecimal(0.00);
            defaultValue = defaultValue.setScale(2, BigDecimal.ROUND_HALF_EVEN);
            this.value = defaultValue;
        }
        this.quantity = quantity;
    }

    /**
     * Returns the number of bills in this src.Bills object
     *
     * @return int containing number of bills in this object
     */
    public int getQuantity(){
        return this.quantity;
    }

    /**
     * Adds bills to this object
     *
     * @param quantity of bills to add to this object
     */
    public void addQuantity(int quantity){
        if (quantity >= 0)
            this.quantity += quantity;
        else
            System.out.println("The quantity input is a negative number! (src.Bills - addQuantity)");
    }

    /**
     * Removes bills from this object
     *
     * @param quantity of bills to take
     */
    public void reduceQuantity(int quantity){
        if (quantity<=this.quantity)
            this.quantity -= quantity;
        else
            System.out.println("The quantity input is bigger than the actual quantity! (src.Bills - reduceQuantity)");
    }

    /**
     * Returns the total value of all bills in this object
     *
     * @return BigDecimal containing the bill value * quantity
     */
    public BigDecimal getTotalValue() {
        return value.multiply(new BigDecimal(quantity));
    }
}
