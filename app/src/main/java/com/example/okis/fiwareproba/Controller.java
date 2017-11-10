package com.example.okis.fiwareproba;

import android.os.AsyncTask;
import com.example.okis.fiwareproba.moj_mvc.HTTP_komunikacija;
import com.example.okis.fiwareproba.moj_mvc.OsnoniModel;

import java.io.Serializable;


public class Controller   implements Serializable {

    private Model model;
    private boolean formiran_model=false;
    private transient ViewInterface viewInterface;
    final static String HOST="http://130.206.117.227";
    final static String PUTANJA_DO_ORIONA=HOST+":1026/";
    final static String PUTANJA_DO_SVIH_ATRIBUTA_GET=PUTANJA_DO_ORIONA+"v2/entities/KantaZasmece-04/";
    final static String PUTANJA_DO__ATRIBUTA_ZA_POST=PUTANJA_DO_SVIH_ATRIBUTA_GET+"attrs";
    final static String PUTANJA_DO_CEP_REAKTIVACIJE=HOST+":8080/ProtonOnWebServerAdmin/resources/instances/ProtonOnWebServer";


    public interface ViewInterface{
        void refresh_temp(Model model);
        void refresh_humi(Model model);
        void refresh_can(Model model);
        void refresh_sve(Model model);
        void prikazi_alert(String poruka);
        void refresh_coolor(Model model);
    }
    public Controller(ViewInterface viewInterface) {
        this.viewInterface = viewInterface;
        this.model= new Model();
        this.formiran_model=false;
    }
    public  void setViewInterface(ViewInterface viewInterface){
        this.viewInterface=viewInterface;
    }

    public String getCoordsValue(){
        Integer id_atributa=this.model.getIndexOfAtribut("location");
        return this.model.getAtribut(id_atributa).getValue().toString();
    }
    public String getIdValue(){
        return this.model.getId();
    }

    public boolean getModelFormiran(){return this.formiran_model;}

    //---------------------------- POST ------------------------------
    //----------------------------  ------------------------------
    public void posaljiZahtevZaPromenuVrenosti(String naziv_atributa,String nova_vrednost){
        Integer id_atributa = this.model.getIndexOfAtribut(naziv_atributa);
        OsnoniModel o = this.model.getAtribut(id_atributa);
        String body = "{\"";
        body += o.getName();
        body += "\":{\"value\":";
        if (o.getType().equals("geo:point")) {
            body += "\"";
            body += nova_vrednost;
            body += "\"";
        } else {
            body += nova_vrednost;
        }
        body += ",\"type\":\"";
        body += o.getType();
        body += "\"}}";
        new PosaljiPostZahtev().execute(body);
    }
    private class PosaljiPostZahtev extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().postMetod(params[0], PUTANJA_DO__ATRIBUTA_ZA_POST, 204);
        }
        @Override
        protected void onPostExecute(String result) {
            if (result == null)
                viewInterface.prikazi_alert("Promenjen sadrzaj");
            else
                viewInterface.prikazi_alert("Nije promenjen sadrzaj");
        }
    }
    //---------------------------- PUT ------------------------------
    //----------------------------  ------------------------------
    public void posaljiZahtevZareaktivacijuCEPa(){
        String body = "{\"action\":\"ChangeDefinitions\",\"definitions-url\":\"/ProtonOnWebServerAdmin/resources/definitions/SmartCity\"}";

        new PosaljiPutZahtev().execute(body);
    }
    private class PosaljiPutZahtev extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().putMetod(params[0], PUTANJA_DO_CEP_REAKTIVACIJE, 200);
        }
        @Override
        protected void onPostExecute(String result) {
            if (result == null)
                model.getAtribut(model.getIndexOfAtribut("garbage_can_color")).setValue(model.postaviVrednost("green"));
            else
                viewInterface.prikazi_alert("Nije CEP reaktiviran");

        }
    }
    //---------------------------- GET ------------------------------
    //---------------------------- sve ------------------------------
    public void prihvatiSve(){
        new DohvatiSve().execute(PUTANJA_DO_SVIH_ATRIBUTA_GET);
    }
    private class DohvatiSve extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().getMethod(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            model.forimirajModel(result);
            formiran_model=true;
            viewInterface.refresh_sve(model);
        }
    }
    //---------------------------- temp ------------------------------
    public void getTempValue(){
        new DohvatiTemperatutu().execute(PUTANJA_DO__ATRIBUTA_ZA_POST+"/temperature_c");
    }
    private class DohvatiTemperatutu extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().getMethod(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            model.getAtribut(model.getIndexOfAtribut("temperature_c")).setValue(model.postaviVrednost(result));
            viewInterface.refresh_temp(model);
        }
    }
    //---------------------------- humi ------------------------------
    public void getHumiValue(){
        new DohvatiVlaznost().execute(PUTANJA_DO__ATRIBUTA_ZA_POST+"/humidity_p");
    }
    private class DohvatiVlaznost extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().getMethod(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            model.getAtribut(model.getIndexOfAtribut("humidity_p")).setValue(model.postaviVrednost(result));
            viewInterface.refresh_humi(model);
        }
    }
    //---------------------------- kanta ------------------------------
    public void getCanValue(){
        new DohvatiKantu().execute(PUTANJA_DO__ATRIBUTA_ZA_POST+"/garbage_overflow");
    }
    private class DohvatiKantu extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().getMethod(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            model.getAtribut(model.getIndexOfAtribut("garbage_overflow")).setValue(model.postaviVrednost(result));
            viewInterface.refresh_can(model);
        }
    }

    //---------------------------- boja ------------------------------
    public void getCoolorValue(){
        new DohvatiBoju().execute(PUTANJA_DO__ATRIBUTA_ZA_POST+"/garbage_can_color");
    }
    private class DohvatiBoju extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return new HTTP_komunikacija().getMethod(params[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            model.getAtribut(model.getIndexOfAtribut("garbage_can_color")).setValue(model.postaviVrednost(result));
            viewInterface.refresh_coolor(model);
        }
    }
//---------------------------- GET END ------------------------------
}
