package ru.alarkhipov.crm.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alarkhipov.crm.entities.SellerEntity;

public interface SellerRepository extends JpaRepository<SellerEntity, Long> {
}
