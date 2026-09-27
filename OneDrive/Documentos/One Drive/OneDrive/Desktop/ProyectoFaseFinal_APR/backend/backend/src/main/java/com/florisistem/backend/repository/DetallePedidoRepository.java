package com.florisistem.backend.repository;

import com.florisistem.backend.model.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {
    // Esto nos servirá para consultar qué componentes incluye un pedido específico
    List<DetallePedido> findByPedidoId(Long pedidoId);
}
