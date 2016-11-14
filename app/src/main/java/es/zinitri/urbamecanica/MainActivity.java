package es.zinitri.urbamecanica;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String part=(String)TxtHora.getText();
                String[] parts = part.split(":");
                Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                        .putExtra(AlarmClock.EXTRA_MESSAGE, TxtProxPartido.getText())
                        .putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(parts[0])-1)
                        .putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(parts[1]));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
        /*Long iniSemanaTs = Utils.calcularIniFinSemana(fechaHoy,Constants.INICIO_SEMANA-Utils.getDayOfTheWeek(fechaHoy));
        Long finSemanaTs = Utils.calcularIniFinSemana(fechaHoy,Utils.getDayOfTheWeek(fechaHoy)-Constants.FIN_SEMANA);
        Log.e("lalala",tsFechaHoy.toString());
        Log.e("lalala",finSemanaTs.toString());*/
        DatabaseReference RefPartido = database.getReference(Constants.FIREBASE_LOCATION_CALENDARIO);
        Query queryRef = RefPartido.orderByChild("fecha").startAt(Utils.tsFechaHoy-Constants.TIMESTAMP_UN_DIA).limitToFirst(1);
        System.out.println(Utils.tsFechaHoy-Constants.TIMESTAMP_UN_DIA);
        queryRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChild) {
                // Get Post object and use the values to update the UI
                Partido partido = dataSnapshot.getValue(Partido.class);
                if(partido.isCasa())
                {
                    TxtProxPartido.setText(Constants.EQUIPO + " - " + partido.getRival());
                }
                else
                {
                    TxtProxPartido.setText(partido.getRival() + " - " + Constants.EQUIPO);
                }
                TxtFecha.setText(partido.getFecha());
                TxtHora.setText(partido.getHora());
                IDpabellon=partido.getPabellon();
                buscarPabellon(IDpabellon);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                // Get Post object and use the values to update the UI
                Partido partido = dataSnapshot.getValue(Partido.class);
                TxtHora.setText(partido.getHora());
                IDpabellon=partido.getPabellon();
                buscarPabellon(IDpabellon);

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_correo) {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setType("plain/text");
            sendIntent.setData(Uri.parse("organizacion@mlasport.com"));
            sendIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
            startActivity(sendIntent);
        }

        return super.onOptionsItemSelected(item);
    }

}
