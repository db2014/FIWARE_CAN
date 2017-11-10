package com.example.okis.fiwareproba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements Controller.ViewInterface {
    private static final String KEY_CONTROLER = "com.example.okis.fiwareproba.Controller";
    private static final String KEY_view_id_senzora = "com.example.okis.fiwareproba.view-idSenzora";
    private static final String KEY_view_id_temp= "com.example.okis.fiwareproba.view-temp";
    private static final String KEY_view_humi = "com.example.okis.fiwareproba.view-humi";
    private static final String KEY_view_can= "com.example.okis.fiwareproba.view-can";
    private Controller controller;
    private LinearLayout gornjiLayout;

    private TextView prikaz_id_senzora;
    private TextView prikaz_temperature;
    private TextView prikaz_vlaznosti;
    private TextView prikaz_kante;

    private  String nova_lakacija_senzora=null;
    private  Runnable updateTask;
    private  final Handler handler = new Handler(); //pravis novu nit, koja ce da vrsi periodicno osvezavanje


    public  boolean checkConnection() {
        final ConnectivityManager connMgr = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            return true;
        }
        return false;
    }

    private void _findViewComponents(){
        gornjiLayout = (LinearLayout) findViewById(R.id.Layout_gornja_polovina);
        prikaz_id_senzora = (TextView) findViewById(R.id.main_prikaz_Id_senzora);
        prikaz_temperature = (TextView) findViewById(R.id.main_prikaz_temp);
        prikaz_vlaznosti = (TextView) findViewById(R.id.main_prikaz_humi);
        prikaz_kante = (TextView) findViewById(R.id.main_prikaz_can);
    }
    private void _dohvati_vrednosti_koje_sePrate(){
        controller.getTempValue();
        controller.getHumiValue();
        controller.getCanValue();
        controller.getCoolorValue();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if (checkConnection()) {
            setContentView(R.layout.activity_main);
            _findViewComponents();
            controller = new Controller(this);

            if (savedInstanceState == null) { //da li postoji sacuvano stanje aplikacije
                final ProgressDialog dialog = ProgressDialog.show(this, "Prihvatanje sadrzaja", "molimo sacekajte", true);
                controller.prihvatiSve();
                updateTask = new Runnable() {
                    @Override
                    public void run() {
                        if (controller.getModelFormiran()) {
                            dialog.dismiss();
                            _dohvati_vrednosti_koje_sePrate();
                        }
                        handler.postDelayed(this, 1000);
                    }
                };
                handler.postDelayed(updateTask, 1000);
            } else {
                controller = (Controller) savedInstanceState.getSerializable(KEY_CONTROLER);
                if (controller.getModelFormiran()) {
                    CharSequence savedText = savedInstanceState.getCharSequence(KEY_view_id_senzora);
                    prikaz_id_senzora.setText(savedText);
                    savedText = savedInstanceState.getCharSequence(KEY_view_id_temp);
                    prikaz_temperature.setText(savedText);
                    savedText = savedInstanceState.getCharSequence(KEY_view_humi);
                    prikaz_vlaznosti.setText(savedText);
                    controller.setViewInterface(this);
                    updateTask = new Runnable() {
                        @Override
                        public void run() {
                            if (controller.getModelFormiran()) {
                                _dohvati_vrednosti_koje_sePrate();
                            }
                            handler.postDelayed(this, 1000);
                        }
                    };
                    handler.postDelayed(updateTask, 1000);
                } else {
                    Toast.makeText(getBaseContext(), "nema modela", Toast.LENGTH_LONG).show();
                }
            }
            nova_lakacija_senzora = getIntent().getStringExtra("nova_lokacija_senzora");//proveravam da li postoji nova lokacija senzora
        }//end_of cennecetion chack
        else {
            Toast.makeText(getBaseContext(), "nema internet konekcije", Toast.LENGTH_LONG).show();

        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTask);
    }

    public void azuriranjeSenzora(View v) {
        Intent i = new Intent(MainActivity.this, AzuriranjeEntiteta.class);
        startActivityForResult(i, 1);
        finish();
    }

    void prikazNaKarti(View v) {
        Intent i = new Intent(MainActivity.this, MapsActivity.class);
        i.putExtra("kordinate",controller.getCoordsValue());
        i.putExtra("id_senozra",controller.getIdValue());
        startActivityForResult(i, 1);
        finish();
    }
    void pokreniCep(View v) {
        controller.posaljiZahtevZareaktivacijuCEPa();
        controller.posaljiZahtevZaPromenuVrenosti("garbage_can_color","green");
    }


    @Override
    public void refresh_temp(Model model) {
        prikaz_temperature.setText("Temperatura:"+model.getAtribut(model.getIndexOfAtribut("temperature_c")).getValue().toString() + "°C");
    }

    @Override
    public void refresh_humi(Model model) {
        prikaz_vlaznosti.setText("Vlaznost:"+model.getAtribut(model.getIndexOfAtribut("humidity_p")).getValue().toString() + "%");
    }
    @Override
    public void refresh_can(Model model) {
        String vrednost=model.getAtribut(model.getIndexOfAtribut("garbage_overflow")).getValue().toString();
        if(vrednost.equals("true"))
            prikaz_kante.setText("Kanta je:PUNA");
        else
            prikaz_kante.setText("");
    }
    @Override
    public void refresh_sve(Model model) {
        prikaz_id_senzora.setText(model.getId());
        prikaz_temperature.setText("Temperatura:"+model.getAtribut(model.getIndexOfAtribut("temperature_c")).getValue().toString() + "°C");
        prikaz_vlaznosti.setText("Vlaznost:"+model.getAtribut(model.getIndexOfAtribut("humidity_p")).getValue().toString() + "%");
        if(nova_lakacija_senzora!=null) {
            controller.posaljiZahtevZaPromenuVrenosti("location",nova_lakacija_senzora);
        }
    }

    @Override
    public void prikazi_alert(String poruka){
        Toast.makeText(getBaseContext(),poruka,Toast.LENGTH_LONG).show();
    }

    @Override
    public void refresh_coolor(Model model) {
        String boja=model.getAtribut(model.getIndexOfAtribut("garbage_can_color")).getValue().toString();
        if(boja.equals("red"))
            gornjiLayout.setBackgroundColor(Color.RED);
        else
        if(boja.equals("green"))
            gornjiLayout.setBackgroundColor(Color.GREEN);
        else
        if(boja.equals("black"))
            gornjiLayout.setBackgroundColor(Color.DKGRAY);
        else
        if(boja.equals("blue"))
            gornjiLayout.setBackgroundColor(Color.BLUE);
        else
            gornjiLayout.setBackgroundColor(Color.CYAN);
    }

    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putSerializable(KEY_CONTROLER, controller);
        out.putSerializable(KEY_view_id_senzora,prikaz_id_senzora.getText().toString());
        out.putSerializable(KEY_view_id_temp,prikaz_temperature.getText().toString());
        out.putSerializable(KEY_view_humi,prikaz_vlaznosti.getText().toString());
        out.putSerializable(KEY_view_can,prikaz_kante.getText().toString());
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
