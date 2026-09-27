package com.florisistem.backend.repository;

import com.florisistem.backend.model.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    // Al heredar de JpaRepository ya tenemos: findAll(), save(), deleteById(), etc.
}
