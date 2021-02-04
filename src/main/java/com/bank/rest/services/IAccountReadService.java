package com.bank.rest.services;

import com.bank.rest.data.Customer;
import com.bank.rest.data.Transaction;
import java.math.BigDecimal;
import java.util.Collection;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface IAccountReadService {

    BigDecimal getAccountBalance(String accountId);

    Customer getAccountHolder(String username);

    BigDecimal getAccountBalance(Long accountId);

    Collection getAllAccountsOfCustomer(Long customerID);

    Collection<Transaction> getAllTransactionsOfCustomer(String userName);

}
