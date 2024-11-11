package com.ToolsChallenge.repository;

import com.ToolsChallenge.entities.FormaPagamentoEntity;
import com.ToolsChallenge.entities.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamentoEntity, Long> {
}
