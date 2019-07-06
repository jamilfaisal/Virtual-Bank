package src.accounts;

import java.math.BigDecimal;

public interface DebtAccount {
    boolean deposit(BigDecimal money);
    boolean withdraw(BigDecimal money);

}
