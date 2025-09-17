package org.fd.mcb.modules.transaction.handler;

import lombok.RequiredArgsConstructor;
import org.fd.mcb.modules.journalentry.adapter.command.JournalEntryCommandAdapter;
import org.fd.mcb.modules.journalentry.dto.context.JournalEntryContext;
import org.fd.mcb.modules.master.adapter.command.BankAccountCommandAdapter;
import org.fd.mcb.modules.master.adapter.query.BankAccountQueryAdapter;
import org.fd.mcb.modules.master.adapter.query.PaymentTypeQueryAdapter;
import org.fd.mcb.modules.master.enums.AccountType;
import org.fd.mcb.modules.transaction.dto.context.TransactionContext;
import org.fd.mcb.modules.journalentry.enums.EntryType;
import org.fd.mcb.modules.master.model.entity.BankAccount;
import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.fd.mcb.modules.master.model.entity.Transaction;
import org.fd.mcb.modules.master.model.repository.TransactionRepository;
import org.fd.mcb.modules.transaction.dto.response.DepositResponse;
import org.fd.mcb.modules.transaction.mapper.DepositMapper;
import org.fd.mcb.modules.transaction.mapper.TransactionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionHandler {

    private final BankAccountQueryAdapter bankAccountQueryAdapter;
    private final BankAccountCommandAdapter bankAccountCommandAdapter;

    private final PaymentTypeQueryAdapter paymentTypeQueryAdapter;

    private final TransactionRepository transactionRepository;
    private final JournalEntryCommandAdapter journalEntryCommandAdapter;

    private final DepositMapper depositMapper;
    private final TransactionMapper transactionMapper;

    @Transactional
    public DepositResponse deposit(Long bankAccountId, BigDecimal amount) {
        BankAccount bankAccount = bankAccountQueryAdapter.findByAccountNumber("String", AccountType.SAVINGS);
        PaymentType paymentType = paymentTypeQueryAdapter.findByName("DEPOSIT");

        TransactionContext transactionContext = TransactionContext.builder()
                .amount(amount)
                .destinationAccount(bankAccount)
                .channel("ATM")
                .paymentType(paymentType)
                .build();
        Transaction transaction = transactionRepository.save(transactionMapper.toTransactionFromContext(transactionContext));

        bankAccount.setBalance(bankAccount.getBalance().add(amount));
        bankAccountCommandAdapter.save(bankAccount);

        JournalEntryContext journalEntryContext = JournalEntryContext.builder()
                .transaction(transaction)
                .bankAccount(bankAccount)
                .amount(amount)
                .entryType(EntryType.CREDIT)
                .build();
        journalEntryCommandAdapter.save(journalEntryContext);

        return depositMapper.toDepositResponse(transaction, bankAccount);
    }
}
