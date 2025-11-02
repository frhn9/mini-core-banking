package org.fd.mcb.modules.master.adapter.command.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.master.adapter.command.AccountHoldCommandAdapter;
import org.fd.mcb.modules.master.enums.HoldStatus;
import org.fd.mcb.modules.master.mapper.AccountHoldMapper;
import org.fd.mcb.modules.master.model.entity.AccountHold;
import org.fd.mcb.modules.master.model.repository.AccountHoldRepository;
import org.fd.mcb.modules.transaction.dto.context.AccountHoldContext;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class AccountHoldCommandAdapterImpl implements AccountHoldCommandAdapter {

    private final AccountHoldRepository accountHoldRepository;

    private final AccountHoldMapper accountHoldMapper;

    @Override
    public AccountHold save(AccountHoldContext context) {
        return accountHoldRepository.save(accountHoldMapper.toAccountHoldFromContext(context));
    }

    @Override
    public void releaseHold(Long holdId) {
        AccountHold hold = accountHoldRepository.findById(holdId)
                .orElseThrow(() -> new RuntimeException("Account hold not found"));
        hold.setStatus(HoldStatus.RELEASED);
        hold.setReleasedAt(ZonedDateTime.now());
        accountHoldRepository.save(hold);
    }
}
