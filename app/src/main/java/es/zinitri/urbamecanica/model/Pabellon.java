package es.zinitri.urbamecanica.model;

/**
 * Created by victor on 7/11/16.
 */

public class Pabellon {
    private String nombre;
    private double latitud;
    private double longitud;

    public Pabellon() {
    }

    public Pabellon(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }
}
