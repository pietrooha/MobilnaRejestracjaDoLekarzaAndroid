package projekt.mobilna.rejestacja.mobilnarejestracja;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

    private Button btnWyswietlWolneTerminy;
    private Button btnPrzejdzDoRejestracji;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWyswietlWolneTerminy = (Button) findViewById(R.id.btnWyswietlWolneTerminy);
        btnPrzejdzDoRejestracji = (Button) findViewById(R.id.btnPrzejdzDoRejestracji);

        // po kliknięciu wyświetl wolne terminy
        btnWyswietlWolneTerminy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Uruchomienie Activity z wolnymi terminami (dniami)
                Intent i = new Intent(getApplicationContext(), WolneTerminyActivity.class);
                startActivity(i);

            }
        });

        // po kliknięciu wyświetl formularz rejestracyjny
        btnPrzejdzDoRejestracji.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Uruchomienie Activity z formularzem rejestracyjnym na wizyte do lekarza
                Intent i = new Intent(getApplicationContext(), FormularzRejestracyjnyActivity.class);
                startActivity(i);

            }
        });
    }
}