package com.instituto.PROYECTOCLIMA.repository;

import com.instituto.PROYECTOCLIMA.model.Lectura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LecturaRepository extends JpaRepository <Lectura,Long>{

}
