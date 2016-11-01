package es.zinitri.urbamecanica.model;

/**
 * Created by victor on 1/11/16.
 */

public class Partido {
    private String rival;
    private boolean casa;
    private int golesUM;
    private int golesRival;
    private long fecha;
    private String hora;
    private String pabellon;

    public Partido() {
    }

    public Partido(String rival, boolean casa, int golesUM, int golesRival, long fecha, String hora, String pabellon) {
        this.rival = rival;
        this.casa = casa;
        this.golesUM = golesUM;
        this.golesRival = golesRival;
        this.fecha = fecha;
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

    public long getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getPabellon() {
        return pabellon;
    }
}
