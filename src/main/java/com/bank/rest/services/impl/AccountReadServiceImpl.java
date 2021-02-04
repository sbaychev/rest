package com.bank.rest.services.impl;

import com.bank.rest.data.Account;
import com.bank.rest.data.Customer;
import com.bank.rest.data.Transaction;
import com.bank.rest.repository.IAccountRepo;
import com.bank.rest.repository.ICustomerRepo;
import com.bank.rest.repository.ITransactionRepo;
import com.bank.rest.services.IAccountReadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccountReadServiceImpl implements IAccountReadService {

    private static final Logger LOG = LoggerFactory.getLogger(AccountReadServiceImpl.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private
    IAccountRepo iAccountRepo;

    @Autowired
    private ICustomerRepo iCustomerRepo;

    @Autowired
    private ITransactionRepo iTransactionRepo;

    public AccountReadServiceImpl(IAccountRepo iAccountRepo, ICustomerRepo iCustomerRepo,
        ITransactionRepo iTransactionRepo) {
        this.iAccountRepo = iAccountRepo;
        this.iCustomerRepo = iCustomerRepo;
        this.iTransactionRepo = iTransactionRepo;
    }

    @Override
    public BigDecimal getAccountBalance(String accountId) {

        Account account = iAccountRepo.findByAccountId(accountId);

        if (null != account) {
            return account.getAccountBalance();
        }
        return null;
    }

    @Override
    public Customer getAccountHolder(String userName) {
        Customer customer = iCustomerRepo.findByUsername(userName);
        try {
            LOG.info(objectMapper.writeValueAsString(customer));
        } catch (JsonProcessingException e) {
        }
        return customer;
    }

    @Override
    public BigDecimal getAccountBalance(Long accountID) {
        return null;
    }

    @Override
    @Transactional
    public Collection<Account> getAllAccountsOfCustomer(Long customerID) {

        return iAccountRepo.streamAllAccounts().collect(Collectors.toList());
    }

    @Override
    public Collection<Transaction> getAllTransactionsOfCustomer(String username) {
        return iTransactionRepo.findByCustomerUsername(username);
    }
}
