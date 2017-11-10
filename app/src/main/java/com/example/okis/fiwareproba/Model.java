package com.example.okis.fiwareproba;
import android.util.Log;

import com.example.okis.fiwareproba.moj_mvc.OsnoniModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class Model implements Serializable {

    private String id;
    private String type;
    private OsnoniModel nizAtributa[];

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public OsnoniModel getAtribut(int id_atributa) {
        return nizAtributa[id_atributa];
    }


    private HashMap<String, Object> getHashMapFromJson(String json) throws JSONException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        JSONObject jsonObject = new JSONObject(json);
        for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
            String key = it.next();
            map.put(key, jsonObject.get(key));
        }
        return map;
    }

    public Object postaviVrednost(String value) {
        try {
            HashMap<String, Object> map = getHashMapFromJson(value);
            Object nova_vrednost = (Object) map.get("value");
            return nova_vrednost;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void forimirajModel(String stringJSON) {
        try {
            HashMap<String, Object> map = getHashMapFromJson(stringJSON);
            nizAtributa = new OsnoniModel[map.size()];
            this.id = (String) map.get("id");
            this.type = (String) map.get("type");
            map.remove("type");
            map.remove("id");
            int i = 0;
            for (String key : map.keySet()) {
                String name = key;
                JSONObject jsonObject = (JSONObject) map.get(key);
                String type = (String) jsonObject.get("type").toString();
                Object value = (Object) jsonObject.get("value");
                nizAtributa[i] = new OsnoniModel(name, type, value);
                i++;
            }
        } catch (JSONException e) {
            Log.d("JsonTest", "Failed parsing " + e);
        }
    }

    public Integer getIndexOfAtribut(String naziv_atributa) {
        Integer i;
        for (i = 0; !nizAtributa[i].getName().equals(naziv_atributa); i++) ;
        return i;
    }

    public Integer getSizeOfAtributes() {
        return nizAtributa.length - 2;
    }

}