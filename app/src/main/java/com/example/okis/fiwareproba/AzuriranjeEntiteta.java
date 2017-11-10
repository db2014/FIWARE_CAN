package com.example.okis.fiwareproba;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.example.okis.fiwareproba.moj_mvc.OsnoniModel;


public class AzuriranjeEntiteta extends Activity implements Controller.ViewInterface{
   private static final String KEY_AZURIRANJE_ATRIBUTA_MODEL = "com.example.okis.fiwareproba.AzuriranjeEntiteta";
   private static final String KEY_AZURIRANJE_ATRIBUTA_BROJ = "com.example.okis.fiwareproba.AzuriranjeEntitetaBroj";
   private static final String KEY_CONTROLER = "com.example.okis.fiwareproba.Controller";


    private Controller controller;
    private LinearLayout linearLayout;
    private PopupWindow popup ;
    private LayoutInflater layoutInflater;
    private TextView naziv;;
    private TextView tip;
    private EditText vrednost;
    private int id_atributa;
    private final int POCETNA_VREDNOST_ZA_VRENOSTI_ATRIBUTA=100;

    private LinearLayout niz[];
    private int brojAtributa=0;
    private Model _model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_azuriranje_entiteta);

        controller = new Controller(this);
        linearLayout=(LinearLayout)findViewById(R.id.azuriranje_layout_osnovni);

        if (savedInstanceState == null) {
            final ProgressDialog dialog=ProgressDialog.show(this,"Prihvatanje sadrzaja","molimo sacekajte",true);
            new Thread(new Runnable() {
                @Override
                public void run () {
                    try {
                        controller.prihvatiSve();
                        while (!controller.getModelFormiran());
                        dialog.dismiss();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            controller = (Controller) savedInstanceState.getSerializable(KEY_CONTROLER);
            _model = (Model) savedInstanceState.getSerializable(KEY_AZURIRANJE_ATRIBUTA_MODEL);
            _refresh_sve(_model);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle out) {
        super.onSaveInstanceState(out);
        out.putSerializable(KEY_CONTROLER, controller);
        out.putSerializable(KEY_AZURIRANJE_ATRIBUTA_MODEL,_model );
    }
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    void zavrsenoAzuriranje(View v) {
        Intent intent = new Intent(AzuriranjeEntiteta.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AzuriranjeEntiteta.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void _refresh_sve(final Model model){
        brojAtributa=model.getSizeOfAtributes();
        niz=new LinearLayout[brojAtributa];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        for(Integer i=0;i<brojAtributa;i++){
            LinearLayout koreni_layout=new LinearLayout(this);
            koreni_layout.setPadding(5,5,5,5);
            koreni_layout.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout levi_layout=new LinearLayout(this);
            lp.weight=1;
            levi_layout.setLayoutParams(lp);
            TextView t= new TextView(this);
            t.setText(model.getAtribut(i).getName());
            t.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            t.setTextSize(15);
            levi_layout.addView(t);
            koreni_layout.addView(levi_layout);

            LinearLayout srednji_layout=new LinearLayout(this);
            lp.weight=2;
            srednji_layout.setLayoutParams(lp);
            TextView t2= new TextView(this);
            t2.setText(" : "+model.getAtribut(i).getValue().toString());
            t2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            t2.setId(POCETNA_VREDNOST_ZA_VRENOSTI_ATRIBUTA+i);
            t2.setTextSize(15);
            srednji_layout.addView(t2);
            koreni_layout.addView(srednji_layout);

            LinearLayout desni_layout=new LinearLayout(this);
            lp.weight=1;
            desni_layout.setLayoutParams(lp);
            Button d= new Button(this);
            d.setText("o");
            d.setId(i);
            d.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    id_atributa=v.getId();
                    OsnoniModel ceo_atribut=model.getAtribut(id_atributa);
                    layoutInflater=(LayoutInflater)getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    ViewGroup conainer=(ViewGroup)layoutInflater.inflate(R.layout.popup_layout,null);
                    naziv=(TextView)conainer.findViewById(R.id.azuiranje_naziv_atributa);
                    tip=(TextView)conainer.findViewById(R.id.azuiranje_tip_atributa);
                    vrednost=(EditText)conainer.findViewById(R.id.azuiranje_vrednost_atributa);
                    Button sacuvaj=(Button)conainer.findViewById(R.id.azuriranje_sacuvaj);
                    naziv.setText(ceo_atribut.getName());
                    tip.setText(ceo_atribut.getType());
                    vrednost.setText(ceo_atribut.getValue().toString());
                    sacuvaj.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String naziv_atributa=naziv.getText().toString();
                            String nova_vrenost_atribura=vrednost.getText().toString();
                            controller.posaljiZahtevZaPromenuVrenosti(naziv_atributa,nova_vrenost_atribura);
                            model.getAtribut(id_atributa).setValue((Object)vrednost.getText());
                            popup.dismiss();
                            TextView azurirajView=(TextView)findViewById(POCETNA_VREDNOST_ZA_VRENOSTI_ATRIBUTA+id_atributa);
                            azurirajView.setText(nova_vrenost_atribura);
                        }
                    });
                    popup = new PopupWindow(conainer,800,800,true);
                    popup.showAtLocation((LinearLayout)findViewById(R.id.azuriranje_layout_osnovni), Gravity.NO_GRAVITY,150,500);

                }
            });
            d.setGravity(Gravity.CENTER);
            desni_layout.addView(d);
            koreni_layout.addView(desni_layout);

            niz[i]=koreni_layout;
            linearLayout.addView(niz[i]);
        }

    }
    @Override
    public void refresh_temp(Model model) {

    }

    @Override
    public void refresh_humi(Model model) {

    }

    @Override
    public void refresh_can(Model model) {

    }
    @Override
    public void refresh_sve(final Model model) {
        _model=model;
       _refresh_sve(model);
    }

    @Override
    public void prikazi_alert(String poruka) {

    }

    @Override
    public void refresh_coolor(Model model) {

    }
}
