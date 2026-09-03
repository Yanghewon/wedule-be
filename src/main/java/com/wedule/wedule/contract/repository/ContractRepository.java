package com.wedule.wedule.contract.repository;

import com.wedule.wedule.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {
    Optional<Contract> findByReservationId(Long reservationId);
}