package org.fd.mcb.modules.transaction.service;

import org.fd.mcb.modules.transaction.dto.request.DepositRequest;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;

public interface DepositService {

    DepositResponse deposit(DepositRequest request);

}
