package com.ToolsChallenge.repository;

import com.ToolsChallenge.entities.DescricaoEntity;
import com.ToolsChallenge.entities.PagamentoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescricaoRepository extends JpaRepository<DescricaoEntity, Long> {
}
