package com.securebank.repository;

import com.securebank.model.FixedDeposit;
import com.securebank.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDeposit, Long> {
    List<FixedDeposit> findByUser(User user);
    List<FixedDeposit> findByActiveTrue();
}