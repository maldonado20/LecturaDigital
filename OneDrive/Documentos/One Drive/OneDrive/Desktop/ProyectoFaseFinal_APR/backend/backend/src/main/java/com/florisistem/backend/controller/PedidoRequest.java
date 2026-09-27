package com.florisistem.backend.controller;

import java.util.List;

public class PedidoRequest {
    private String nombreReceptor;
    private String telefonoReceptor;
    private String direccionEntrega;
    private String fechaEntrega; // Formato YYYY-MM-DD
    private String horaEntrega;  // Formato HH:MM:SS
    private String dedicatoria;
    private List<ItemInsumo> items;

    // Getters y Setters
    public String getNombreReceptor() { return nombreReceptor; }
    public void setNombreReceptor(String nombreReceptor) { this.nombreReceptor = nombreReceptor; }
    public String getTelefonoReceptor() { return telefonoReceptor; }
    public void setTelefonoReceptor(String telefonoReceptor) { this.telefonoReceptor = telefonoReceptor; }
    public String getDireccionEntrega() { return direccionEntrega; }
    public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
    public String getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(String fechaEntrega) { this.fechaEntrega = fechaEntrega; }
    public String getHoraEntrega() { return horaEntrega; }
    public void setHoraEntrega(String horaEntrega) { this.horaEntrega = horaEntrega; }
    public String getDedicatoria() { return dedicatoria; }
    public void setDedicatoria(String dedicatoria) { this.dedicatoria = dedicatoria; }
    public List<ItemInsumo> getItems() { return items; }
    public void setItems(List<ItemInsumo> items) { this.items = items; }

    public static class ItemInsumo {
        private Long insumoId;
        private Integer cantidad;

        // Getters y Setters
        public Long getInsumoId() { return insumoId; }
        public void setInsumoId(Long insumoId) { this.insumoId = insumoId; }
        public Integer getCantidad() { return cantidad; }
        public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
    }
}
