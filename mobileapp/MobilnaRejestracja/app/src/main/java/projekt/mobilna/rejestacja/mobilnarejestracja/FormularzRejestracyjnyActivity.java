package projekt.mobilna.rejestacja.mobilnarejestracja;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by piotr on 09.05.15.
 */
public class FormularzRejestracyjnyActivity extends Activity implements View.OnClickListener {

    private ProgressDialog pDialog;

    // Creating JSON Parser object
    private JSONParser jsonParser = new JSONParser();

    private EditText dzienWizyty;
    private EditText godzinaWizyty;
    private EditText imiePacjenta;
    private EditText nazwiskoPacjenta;
    private EditText peselPacjenta;
    private EditText nrTelPacjenta;

    private DatePickerDialog dzienDatePickerDialog;
    private SimpleDateFormat dateFormatter;

    private Spinner spinner;

    // url do zarejestrowania nowego terminu wizyty
    private static String url_dodaj_nowy_termin =
                "http://medrejestracja.byethost14.com/android_connect_rej/dodaj_nowy_termin.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_nowy_termin);

        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        dzienWizyty = (EditText) findViewById(R.id.dzienWizyty);
        dzienWizyty.setInputType(InputType.TYPE_NULL);
        setDateTimeField();

        godzinaWizyty = (EditText) findViewById(R.id.godzinaWizyty);
        imiePacjenta = (EditText) findViewById(R.id.imie);
        nazwiskoPacjenta = (EditText) findViewById(R.id.nazwisko);
        peselPacjenta = (EditText) findViewById(R.id.pesel);
        nrTelPacjenta = (EditText) findViewById(R.id.nrTel);

        spinner = (Spinner) findViewById(R.id.godzina_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.godzina, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        Button btnZarejestrujWizyte = (Button) findViewById(R.id.btnZarejestrujWizyte);

        btnZarejestrujWizyte.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // dodanie nowego terminu wizyty w background thread
                new ZarejestrujWizyte().execute();
            }
        });
    }

    // Metoda do ustawienia daty w polu EditText - dzienWizyty
    private void setDateTimeField() {
        dzienWizyty.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        dzienDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                dzienWizyty.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onClick(View view) {
        if(view == dzienWizyty) {
            dzienDatePickerDialog.show();
        }
    }

    // dwie metody do spinner'a
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        spinner.setSelection(pos);
        String godzinaWiz = (String) spinner.getSelectedItem();
        parent.getItemAtPosition(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {}

    /**
     * W tle Async Task do stworzenia nowego terminu wizyty
     * */
    class ZarejestrujWizyte extends AsyncTask<String, String, String> {

        /**
         * Przed uruchomieniem wątku w tle wyświetl Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(FormularzRejestracyjnyActivity.this);
            pDialog.setMessage("Sprawdzanie danych...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Tworzenie nowego terminu wizyty
         * */
        protected String doInBackground(String... args) {
            String dzien = dzienWizyty.getText().toString();
            String godzina = godzinaWizyty.getText().toString();
            String imie = imiePacjenta.getText().toString();
            String nazwisko = nazwiskoPacjenta.getText().toString();
            String pesel = peselPacjenta.getText().toString();
            String nrTel = nrTelPacjenta.getText().toString();

            // Tworzenie parametrów
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("dzien", dzien));
            params.add(new BasicNameValuePair("godzina", godzina));
            params.add(new BasicNameValuePair("imie", imie));
            params.add(new BasicNameValuePair("nazwisko", nazwisko));
            params.add(new BasicNameValuePair("pesel", pesel));
            params.add(new BasicNameValuePair("nrTel", nrTel));

            // getting JSON Object
            JSONObject json = jsonParser.makeHttpRequest(url_dodaj_nowy_termin, "POST", params);

            // check log cat fro response
            Log.d("Create Response", json.toString());

            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // poprawnie utworzono nowy termin wizyty
                    Intent i = new Intent(getApplicationContext(), WolneTerminyActivity.class);
                    startActivity(i);

                    // zamknięcie tego okna
                    finish();
                } else {}
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
        }

    }
}