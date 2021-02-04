package com.bank.rest.repository;

import com.bank.rest.data.Customer;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ICustomerRepo extends JpaRepository<Customer, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Customer findByUsername(String userName);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Customer> S save(S customer);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Customer> S saveAndFlush(S customer);
}
