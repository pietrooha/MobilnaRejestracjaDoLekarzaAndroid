package projekt.mobilna.rejestacja.mobilnarejestracja;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by piotr on 09.05.15.
 */
public class WolneTerminyActivity extends ListActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private JSONParser jParser = new JSONParser();

    private ArrayList<HashMap<String, String>> listaWolnychTerminow;

    // url do pobrania listy wolnych terminów
    private static String url_wolnych_terminow = "http://medrejestracja.byethost14.com/android_connect_rej/pobierz_wolne_terminy.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_ZAREJESTROWANI = "zarejestrowani";
    private static final String TAG_PID = "pid";
    private static final String TAG_DZIEN = "dzien";

    // zarejestrowani JSONArray
    private JSONArray zarejestrowani = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wszystkie_wolne_terminy);

        // Hashmap dla ListView
        listaWolnychTerminow = new ArrayList<HashMap<String, String>>();

        // Ładowanie wolnych terminów w wątku w tle
        new ŁadujWolneTerminy().execute();

        // Wyświetl listview
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              // startowanie nowej intencji
                Intent i = new Intent(getApplicationContext(), WolneGodzinyActivity.class);
                // startowanie nowego activity i oczekiwanie na opdowiedź
                startActivity(i);
           }
        });
    }

    /**
     * W tle Async Task do ładowania wolnych terminów przez zapytanie HTTP Request
     * */
    class ŁadujWolneTerminy extends AsyncTask<String, String, String> {

        /**
         * Przed wystartowaniem wątku w tle pokaż Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(WolneTerminyActivity.this);
            pDialog.setMessage("Ładowanie wolnych terminów. Proszę czekać...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * pobranie wszystkich wolnych terminow z url
         * */
        protected String doInBackground(String... args) {
            // Tworzenie parametrów
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_wolnych_terminow, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Wszystkie wolne terminy: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);
                EditText imie;
                imie = (EditText)findViewById(R.id.imie);

                if (success == 1) {
                    // znaleziono wolne terminy
                    // Getting Array of wolne terminy
                    zarejestrowani = json.getJSONArray(TAG_ZAREJESTROWANI);

                    // pętla przez wszystkie wolne terminy
                    for (int i = 0; i < zarejestrowani.length(); i++) {
                        JSONObject c = zarejestrowani.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_PID);
                        String dzien = c.getString(TAG_DZIEN);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_PID, id);
                        map.put(TAG_DZIEN, dzien);

                        // adding HashList to ArrayList
                        listaWolnychTerminow.add(map);
                    }
                } else {
                    // nie znaleziono wolnych terminow
                    Intent i = new Intent(getApplicationContext(), FormularzRejestracyjnyActivity.class);
                    // Zamknięcie wszystkich poprzednich Activity
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * Po wykonaniu zadania w tle wyłącz Progress Dialog
         * **/
        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter(
                            WolneTerminyActivity.this, listaWolnychTerminow,
                            R.layout.list_dzien, new String[] { TAG_PID,
                            TAG_DZIEN},
                            new int[] { R.id.pid, R.id.dzien });
                    // updating listview
                    setListAdapter(adapter);
                }
            });

        }

    }
}