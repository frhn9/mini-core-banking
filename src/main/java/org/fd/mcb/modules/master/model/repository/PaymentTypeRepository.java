package org.fd.mcb.modules.master.model.repository;

import org.fd.mcb.modules.master.model.entity.PaymentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentTypeRepository extends JpaRepository<PaymentType, Integer> {
    Optional<PaymentType> findByName(String name);
}