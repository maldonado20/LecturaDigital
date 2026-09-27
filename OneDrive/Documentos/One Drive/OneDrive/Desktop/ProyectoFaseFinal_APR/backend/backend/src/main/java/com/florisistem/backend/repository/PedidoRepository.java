package com.florisistem.backend.repository;

import com.florisistem.backend.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    // Esto nos servirá para que el administrador vea los pedidos ordenados por fecha
    List<Pedido> findAllByOrderByFechaCreacionDesc();
}
