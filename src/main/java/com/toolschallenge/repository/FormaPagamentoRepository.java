package com.toolschallenge.repository;

import com.toolschallenge.entities.FormaPagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamentoEntity, Long> {
}
