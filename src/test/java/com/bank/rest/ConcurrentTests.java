package com.bank.rest;

import com.bank.rest.data.Account;
import com.bank.rest.data.Customer;
import com.bank.rest.data.Transaction;
import com.bank.rest.data.TransactionType;
import com.bank.rest.repository.IAccountRepo;
import com.bank.rest.repository.ICustomerRepo;
import com.bank.rest.services.IAccountReadServices;
import com.bank.rest.services.IAccountWriteServices;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment =
    SpringBootTest.WebEnvironment.MOCK,
    classes = RestApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(
    locations = "classpath:application-integrationtest.properties")
public class ConcurrentTests {

    @Autowired
    private IAccountReadServices mockIAccountReadServices;

    @Autowired
    private IAccountWriteServices mockIAccountWriteServices;

    @Autowired
    private IAccountRepo mockIAccountRepo;

    @Autowired
    private ICustomerRepo mockICustomerRepo;

    @BeforeTestMethod
    public void setUp() {
        Account account = mockIAccountRepo.save(Account.builder()
            .accountBalance(new BigDecimal(10000))
            .accountHolder(Collections.emptySet())
            .accountNumber("123456A")
            .build());

        Account account1 = mockIAccountRepo.save(Account.builder()
            .accountBalance(new BigDecimal(50000))
            .accountHolder(Collections.emptySet())
            .accountNumber("123456B")
            .build());

        BigDecimal balance = account.getAccountBalance();

        account.setAccountBalance(balance.add(BigDecimal.valueOf(10L)));
        Customer customer = Customer.builder()
            .accounts(new LinkedHashSet(Arrays.asList(account, account1)))
            .firstName("John")
            .lastName("Smith")
            .username("jon01").build();

        Transaction transaction = Transaction.builder()
            .transactionType(TransactionType.CREDIT)
            .beginningAccountBalance(balance)
            .endingAccountBalance(balance.add(BigDecimal.valueOf(10L)))
            .customer(customer)
            .date(Date.valueOf(LocalDate.now()))
            .build();

        Set<Transaction> transactionList = account.getTransaction() == null ? new LinkedHashSet<>() :
            account.getTransaction();
        transactionList.add(transaction);
        account.setTransaction(transactionList);
        transaction.setAccount(account);

        Set<Transaction> transactionLis = customer.getTransaction() == null ? new LinkedHashSet<>() :
            account.getTransaction();
        transactionLis.add(transaction);
        customer.setTransaction(transactionLis);

        mockICustomerRepo.save(customer);
    }

    @Test
    public void testConcurrentReadsInReadService() throws InterruptedException {
        TestUtil.runMultithreaded(() -> {
            Customer customer = null;
            try {
                customer = mockIAccountReadServices.getAccountHolder("jon01");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Assert.assertNotNull(customer);
        }, 25);
    }

    @Test
    public void testConcurrentWritesInWriteService() throws InterruptedException {

        AtomicInteger integer = new AtomicInteger(1);

        TestUtil.runMultithreaded(() -> {
            Boolean customer = Boolean.FALSE;
            try {
                customer = mockIAccountWriteServices.performCreditOperation("123456A", "123456B",
                    String.valueOf(integer.getAndIncrement()), "1");
            } catch (Exception e) {
                e.printStackTrace();
            }
            Assert.assertTrue(customer);
        }, 5);
    }
}
