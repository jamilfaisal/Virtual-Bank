package src.accounts;

import java.math.BigDecimal;

public interface AssetAccount {
    boolean deposit(BigDecimal money);

    boolean withdraw(BigDecimal money);

    boolean isPremier();

    void setPremier();
}
