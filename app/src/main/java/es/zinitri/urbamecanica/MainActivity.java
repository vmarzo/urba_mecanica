package es.zinitri.urbamecanica;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.zinitri.urbamecanica.model.Pabellon;
import es.zinitri.urbamecanica.model.Partido;
import es.zinitri.urbamecanica.utils.Constants;
import es.zinitri.urbamecanica.utils.Utils;

public class MainActivity extends AppCompatActivity {
    private TextView TxtProxPartido;
    private TextView TxtFecha;
    private TextView TxtHora;
    private TextView TxtPabellon;
    private Button btnMapa;
    private String IDpabellon;
    private Double latPabellon;
    private Double longPabellon;
    private String nombrePabellon;
    private FloatingActionButton fab;
    private int hora;
    private int minutos;
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TxtProxPartido=(TextView)findViewById(R.id.txt_prox_partido);
        TxtFecha=(TextView)findViewById(R.id.txt_fecha);
        TxtHora=(TextView)findViewById(R.id.txt_hora);
        btnMapa=(Button) findViewById(R.id.btn_mapa);

        //Boton para poner alarma antes del partido. Por defecto esta oculto
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, TxtProxPartido.getText())
                        // La alarma sonara x horas antes del partido
                        .putExtra(AlarmClock.EXTRA_HOUR, (hora-Constants.ALARMA_ANTES_PARTIDO))
                        .putExtra(AlarmClock.EXTRA_MINUTES, minutos);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        //Conexion a los partidos del calendario
        DatabaseReference RefPartido = database.getReference(Constants.FIREBASE_LOCATION_CALENDARIO);
        //Consulta para recuperar el proximo partido del partido. Se resta un dia al dia de hoy para que el mismo dia del partido siga apareciendo
        Query queryRef = RefPartido.orderByChild("fecha").startAt(Utils.tsFechaHoy-Constants.TIMESTAMP_UN_DIA).limitToFirst(1);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
                // Get Post object and use the values to update the UI
                Partido partido = dataSnapshot.getValue(Partido.class);
                //Se comprueba si el partido es en casa.
                if(partido.isCasa())
                {
                    TxtProxPartido.setText(Constants.EQUIPO + " - " + partido.getRival());
                }
                else
                {
                    TxtProxPartido.setText(partido.getRival() + " - " + Constants.EQUIPO);
                }
                //FECHA DEL PARTIDO
                TxtFecha.setText(partido.getFecha());
                //HORA DEL PARTIDO
                TxtHora.setText(partido.getHora());
                //Se comprueba que hay hora del partido para que no haya errores en las siguiente operaciones
                if(!partido.getHora().equals("")) {
                    //Dividir la cadena de la hora en horas y minutos para añadirlo a la alarma
                    String[] parts = partido.getHora().split(":");
                    hora=Integer.parseInt(parts[0]);
                    minutos=Integer.parseInt(parts[1]);
                    //Sumar la fecha del partido mas la hora del partido
                    Long fechaPartido=(Utils.transformarFecha("hh:mm",partido.getHora())+Utils.transformarFecha("dd-MM-yyyy",partido.getFecha()));
                    //Activar el boton de alarma cuando estemos a menos de 24horas del partido
                    if(Utils.tsFechaHoy>(fechaPartido-Constants.TIMESTAMP_UN_DIA) && Utils.tsFechaHoy<fechaPartido) {
                        fab.setVisibility(View.VISIBLE);
                    }
                }
                //Se comprueba que se haya asignado un pabellon para que no haya errores en las siguientes operaciones
                if(!partido.getPabellon().equals("")){
                    IDpabellon=partido.getPabellon();
                    //Buscar las cordenadas y el nombre del pabellon con la id del pabellon
                    buscarPabellon(IDpabellon);
                    //Activar boton de la direccion del pabellon cuando este aun no haya sido asignado a un partido. Por defecto esta desactivado
                    btnMapa.setEnabled(true);
                }

            }

            //Se actualizan en vivo los posibles campos que pueden cambiar. Hora y pabellon
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Get Post object and use the values to update the UI
                Partido partido = dataSnapshot.getValue(Partido.class);
                TxtHora.setText(partido.getHora());
                IDpabellon=partido.getPabellon();
                buscarPabellon(IDpabellon);
                //Activar o desactivar el boton de la direccion del pabellon segun si se haya asignado o no un pabellon al partido. Por defecto esta desactivado
                if(partido.getPabellon().equals("")){
                    btnMapa.setEnabled(false);
                }else{
                    btnMapa.setEnabled(true);
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Boton para mostrar en un mapa la direccion del pabellon
        btnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("geo:0,0?q="+latPabellon+","+longPabellon+"("+nombrePabellon+")"));

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });


    }
    //Metodo para buscar el nombre completo y las cordenadas del pabellon en la base de datos
    public void buscarPabellon(String idPabellon)
    {
        TxtPabellon=(TextView)findViewById(R.id.txt_pabellon);
        database.getReference(Constants.FIREBASE_LOCATION_PABELLON).child(idPabellon).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pabellon pabellon = dataSnapshot.getValue(Pabellon.class);
                latPabellon=pabellon.getLatitud();
                longPabellon=pabellon.getLongitud();
                nombrePabellon=pabellon.getNombre();
                TxtPabellon.setText(nombrePabellon);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("getUser:onCancelled", databaseError.toException());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Acceder a al correo para poder un correo a la organizacion de la liga
        if (id == R.id.action_correo) {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "organizacion@mlasport.com" });
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

}
