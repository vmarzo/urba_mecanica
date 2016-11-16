package es.zinitri.urbamecanica.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by victor on 5/11/16.
 */

public class Utils {
    public static Date fechaHoy = new Date();
    public static Long tsFechaHoy = (fechaHoy.getTime()/1000);
    //public static Long tsFechaHoy = Long.valueOf(1479501000);


    public static Long transformarFecha(String formato, String hora)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formato);
        Date parsedDate = null;
        try {
            parsedDate = dateFormat.parse(hora);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (parsedDate.getTime()/1000);
    }

}
