package com.toolschallenge.repository;

import com.toolschallenge.entities.DescricaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DescricaoRepository extends JpaRepository<DescricaoEntity, Long> {
}
