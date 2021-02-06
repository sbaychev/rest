package com.bank.rest.services.impl;

import com.bank.rest.data.Account;
import com.bank.rest.data.Customer;
import com.bank.rest.data.Transaction;
import com.bank.rest.data.TransactionType;
import com.bank.rest.repository.IAccountRepo;
import com.bank.rest.repository.ICustomerRepo;
import com.bank.rest.repository.ITransactionRepo;
import com.bank.rest.services.IAccountWriteService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
//@Scope("prototype")
public class AccountWriteServiceImpl implements IAccountWriteService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountWriteServiceImpl.class);

//    @Autowired
//    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private
    IAccountRepo iAccountRepo;

    @Autowired
    private ICustomerRepo iCustomerRepo;

    @Autowired
    private ITransactionRepo iTransactionRepo;

    @Override
    public boolean performCreditOperation(String fromAccountNumber, String toAccountNumber, String amount,
        String customerID) {

        Account account = iAccountRepo.findByAccountId(fromAccountNumber);

        if (account == null) {
            return false;
        }

        Customer customer;

        Optional<Customer> op = iCustomerRepo.findById(Long.valueOf(customerID));
        if (op.isPresent()) {
            customer = op.get();
        } else {
            return false;
        }

        BigDecimal balance = account.getAccountBalance();

        BigDecimal operationResult = BillingStrategy.creditStrategy().calculate(balance,
            BigDecimal.valueOf(Long.parseLong(amount)));

        Transaction transaction = iTransactionRepo.save(Transaction.builder()
            .transactionType(TransactionType.CREDIT)
            .beginningAccountBalance(balance)
            .endingAccountBalance(operationResult)
            .customer(customer)
            .account(account)
            .date(Date.from(Instant.now()))
            .build());

        account.setAccountBalance(operationResult);

        Set<Account> custAccs = customer.getAccounts();
        custAccs.add(account);
        customer.setAccounts(custAccs);

        try {
            LOG.info(objectMapper.writeValueAsString(transaction) + "\n");
        } catch (JsonProcessingException e) {
            LOG.info(e.getMessage());
        }

        return true;
    }

    @Override
    public boolean performDebitOperation(String fromAccountNumber, String toAccountNumber, String amount,
        String customerID) {
        return false;
    }

    @Override
    public Customer save(Customer customer) {
        return null;
    }
}

@FunctionalInterface
interface BillingStrategy {

    MathContext mc = new MathContext(12); // 12 precision

    BigDecimal calculate(BigDecimal sumTotalAmount, BigDecimal... amountToCalculate);

    static BillingStrategy debitStrategy() {
        return (sumTotalAmount, amountToCalculate) -> Arrays.stream(amountToCalculate)
            .reduce(sumTotalAmount, (bigDecimal, subtrahend) -> bigDecimal.subtract(subtrahend, mc));
    }

    static BillingStrategy creditStrategy() {
        return (sumTotalAmount, amountToCalculate) -> Arrays.stream(amountToCalculate)
            .reduce(sumTotalAmount, (bigDecimal, added) -> bigDecimal.add(added, mc));
    }
}