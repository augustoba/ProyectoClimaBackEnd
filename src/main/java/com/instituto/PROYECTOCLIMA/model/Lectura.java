package com.instituto.PROYECTOCLIMA.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.sql.Timestamp;

@Entity(name = "lecturas")
@Data
public class Lectura {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id= null;
    private Double temperatura= null;
    private Double presion= null;
    private Double humedad = null;
    private Timestamp fecha = null;

    public Lectura() {
    }

    public Lectura(Long id, Double temperatura, Double presion, Double humedad, Timestamp fecha) {
        this.id = id;
        this.temperatura = temperatura;
        this.presion = presion;
        this.humedad = humedad;
        this.fecha = fecha;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getTemperatura() {
        return temperatura;
    }

    public void setTemperatura(Double temperatura) {
        this.temperatura = temperatura;
    }

    public Double getPresion() {
        return presion;
    }

    public void setPresion(Double presion) {
        this.presion = presion;
    }

    public Double getHumedad() {
        return humedad;
    }

    public void setHumedad(Double humedad) {
        this.humedad = humedad;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Lectura{" +
                "id=" + id +
                ", temperatura=" + temperatura +
                ", presion=" + presion +
                ", humedad=" + humedad +
                ", fecha=" + fecha +
                '}';
    }

}
