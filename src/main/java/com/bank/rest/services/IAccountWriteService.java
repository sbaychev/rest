package com.bank.rest.services;

import com.bank.rest.data.Customer;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(isolation = Isolation.SERIALIZABLE)
public interface IAccountWriteService {

    /**
     * @param fromAccountNumber .
     * @param toAccountNumber .
     * @param amount .
     * @param customerID .
     * @return boolean
     */
    boolean performCreditOperation(String fromAccountNumber, String toAccountNumber, String amount,
        String customerID);

    /**
     * @param fromAccountNumber .
     * @param toAccountNumber .
     * @param amount .
     * @param customerID .
     * @return boolean
     */
    boolean performDebitOperation(String fromAccountNumber, String toAccountNumber, String amount,
        String customerID);

    /**
     * @param customer .
     * @return Customer
     */
    Customer save(Customer customer);
}
