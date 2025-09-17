package org.fd.mcb.modules.transaction.service.impl;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.command.TransactionCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.modules.transaction.dto.request.DepositRequest;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;
import org.fd.mcb.modules.transaction.mapper.DepositMapper;
import org.fd.mcb.modules.transaction.service.DepositService;
import org.fd.mcb.shared.util.TransactionUtil;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;

    private final BankAccountCommandAdapter bankAccountCommandAdapter;
    private final TransactionCommandAdapter transactionCommandAdapter;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    private final DepositMapper depositMapper;

    @Override
    public DepositResponse deposit(DepositRequest request) {
        TransactionUtil.validateInvalidAmount(request.getAmount());
        BankAccount bankAccount = bankAccountQueryAdapter.findByAccountNumber(request.getAccountNumber(), AccountType.SAVINGS);
        PaymentType paymentType = paymentTypeQueryAdapter.findByName("DEPOSIT");

        TransactionContext transactionContext = TransactionContext.builder()
                .amount(request.getAmount())
                .destinationAccount(bankAccount)
                .channel("ATM")
                .paymentType(paymentType)
                .build();
        Transaction transaction = transactionCommandAdapter.save(transactionContext);

        bankAccount.setBalance(bankAccount.getBalance().add(request.getAmount()));
        bankAccountCommandAdapter.save(bankAccount);

        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(bankAccount)
                .amount(request.getAmount())
                .entryType(EntryType.CREDIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        return depositMapper.toDepositResponse(transaction, bankAccount);
    }

}
