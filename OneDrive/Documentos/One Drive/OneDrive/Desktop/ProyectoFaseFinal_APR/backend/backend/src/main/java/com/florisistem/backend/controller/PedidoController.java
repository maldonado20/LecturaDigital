package com.florisistem.backend.controller;

import com.florisistem.backend.model.*;
import com.florisistem.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private DetallePedidoRepository detallePedidoRepository;

    @Autowired
    private InsumoRepository insumoRepository;

    // 1. Ruta para CREAR un pedido personalizado (Cálculo dinámico)
    @PostMapping
    public Pedido crearPedido(@RequestBody PedidoRequest request) {
        Pedido pedido = new Pedido();
        pedido.setNombreReceptor(request.getNombreReceptor());
        pedido.setTelefonoReceptor(request.getTelefonoReceptor());
        pedido.setDireccionEntrega(request.getDireccionEntrega());
        pedido.setFechaEntrega(LocalDate.parse(request.getFechaEntrega()));
        pedido.setLaHoraEntrega(LocalTime.parse(request.getHoraEntrega()));
        pedido.setDedicatoria(request.getDedicatoria());
        pedido.setEstado("PENDIENTE");

        // Guardamos el pedido base primero con precio cero temporalmente
        pedido.setPrecioTotal(BigDecimal.ZERO);
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        BigDecimal totalAcumulado = BigDecimal.ZERO;
        List<DetallePedido> detalles = new ArrayList<>();

        // Recorremos cada insumo del arreglo floral para calcular precios
        for (PedidoRequest.ItemInsumo item : request.getItems()) {
            Insumo insumo = insumoRepository.findById(item.getInsumoId())
                    .orElseThrow(() -> new RuntimeException("Insumo no encontrado"));

            // Calcular subtotal de esta línea (Precio unitario * cantidad)
            BigDecimal subtotal = insumo.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad()));
            totalAcumulado = totalAcumulado.add(subtotal);

            // Crear el detalle del renglón
            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setInsumo(insumo);
            detalle.setCantidadUtilizada(item.getCantidad());
            detalle.setPrecioSubtotal(subtotal);
            detalles.add(detalle);

            // AUTOMATIZACIÓN: Restar del inventario
            insumo.setCantidadDisponible(insumo.getCantidadDisponible() - item.getCantidad());
            insumoRepository.save(insumo);
        }

        // Guardamos todos los detalles en la base de datos
        detallePedidoRepository.saveAll(detalles);

        // Actualizamos el precio final calculado automáticamente en el pedido
        pedidoGuardado.setPrecioTotal(totalAcumulado);
        return pedidoRepository.save(pedidoGuardado);
    }

    // 2. Ruta para que el ADMINISTRADOR consulte todos los pedidos recibidos
    @GetMapping
    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAllByOrderByFechaCreacionDesc();
    }

    // 3. Ruta para que el ADMINISTRADOR actualice el estado de un pedido
    @PutMapping("/{id}/estado")
    public Pedido actualizarEstadoPedido(@PathVariable Long id, @RequestBody String nuevoEstado) {
        // Buscamos el pedido en la base de datos por su ID
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con el ID: " + id));

        // Limpiamos las comillas extras que a veces envía el texto plano desde la web
        String estadoLimpio = nuevoEstado.replace("\"", "").trim().toUpperCase();

        // Actualizamos el estado del pedido
        pedido.setEstado(estadoLimpio);

        // Guardamos los cambios en PostgreSQL y devolvemos el pedido actualizado
        return pedidoRepository.save(pedido);
    }
}
