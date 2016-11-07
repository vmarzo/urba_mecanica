package es.zinitri.urbamecanica.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Created by victor on 1/11/16.
 */

public class Partido {
    private String rival;
    private boolean casa;
    private int golesUM;
    private int golesRival;
    private Long fecha;
    private Long latitud;
    private Long longitud;
    private String hora;
    private String pabellon;

    public Partido() {
    }

    public Partido(String rival, boolean casa, int golesUM, int golesRival, Long fecha, Long latitud, Long longitud, String hora, String pabellon) {
        this.rival = rival;
        this.casa = casa;
        this.golesUM = golesUM;
        this.golesRival = golesRival;
        this.fecha = fecha;
        this.latitud = latitud;
        this.longitud = longitud;
        this.hora = hora;
        this.pabellon = pabellon;
    }

    public String getRival() {
        return rival;
    }

    public boolean isCasa() {
        return casa;
    }

    public int getGolesUM() {
        return golesUM;
    }

    public int getGolesRival() {
        return golesRival;
    }

    public String getFecha() {
        String string  =  new SimpleDateFormat("dd-MM-yyyy").format(new Timestamp(fecha*1000));
        return string;
    }

    public String getHora() {
        return hora;
    }

    public String getPabellon() {
        return pabellon;
    }
}
