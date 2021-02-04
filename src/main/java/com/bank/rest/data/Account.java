package com.bank.rest.data;

import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"transaction", "accountHolder"})
@Table(name = "account")
public class Account extends DeactivatableEntity<Long> {

    @ManyToMany(mappedBy = "accounts", fetch = FetchType.LAZY)
    @JsonBackReference
    private Set<Customer> accountHolder;

    @Column(unique = true)
    private String accountId;

    @Column
    private BigDecimal accountBalance;

    //TODO: EventListener and TransactionEventListener vs cascading persistence
    @OneToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE}, mappedBy = "account")
    @JsonBackReference
    private Set<Transaction> transaction;

    public Account() {
        this.transaction = new LinkedHashSet<>(1);
        this.accountHolder = new LinkedHashSet<>(1);
        this.accountId = "";
        this.accountBalance = new BigDecimal(0);
    }
}
