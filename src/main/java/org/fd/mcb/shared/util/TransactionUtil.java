package org.fd.mcb.shared.util;

import lombok.experimental.UtilityClass;
import org.fd.mcb.shared.exception.InvalidAmountException;

import java.math.BigDecimal;

@UtilityClass
public class TransactionUtil {

    public static void validateInvalidAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException();
        }
    }

}
