package es.zinitri.urbamecanica.utils;



import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by victor on 5/11/16.
 */

public class Utils {
    public static Date fechaHoy = new Date();
    public static Long tsFechaHoy = (fechaHoy.getTime()/1000);

    public static int getDayOfTheWeek(Date d){
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(d);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Long calcularIniFinSemana(Date fecha, int dias)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTimeInMillis()/1000; // Devuelve el objeto Date con los nuevos días añadidos
    }

}
