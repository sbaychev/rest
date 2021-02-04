package com.bank.rest.repository;

import com.bank.rest.data.Account;
import com.bank.rest.data.Customer;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface IAccountRepo extends JpaRepository<Account, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Account findByAccountId(String accountId);

    /**
     * Saves the given {@link Account}.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Account> S save(S account);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends Account> S saveAndFlush(S account);

    /**
     * Sample method to derive a query from using JDK 8's {@link Optional} as return type.
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<Account> findByAccountHolder(Customer accountHolderId);

    /**
     * Sample method to demonstrate support for {@link Stream} as a return type with a custom query. The query is
     * executed in a streaming fashion which means that the method returns as soon as the first results are ready.
     */
    @Query("select c from Account c")
    Stream<Account> streamAllAccounts();

    /**
     * Sample method to demonstrate support for {@link Stream} as a return type with a derived query. The query is
     * executed in a streaming fashion which means that the method returns as soon as the first results are ready.
     */
    Stream<Account> findAllByAccountBalanceNotNull();

    @Async
    CompletableFuture<List<Account>> readAllBy();

    @Modifying
    @Query(value = "UPDATE Account u SET u.accountBalance = ? WHERE u.accountNumber = ?", nativeQuery = true)
    int updateAccountSetBalanceForAccount(String balance, String accountNumber);
}