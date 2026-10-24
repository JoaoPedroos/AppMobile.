package br.com.advr;

import android.os.Bundle;
import android.widget.TextView;

/** Tela inicial exibida após o login. */
public class DashboardActivity extends BaseActivity {
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_dashboard);
        findViewById(R.id.header_logout).setOnClickListener(v -> loginScreen());
        findViewById(R.id.logout).setOnClickListener(v -> loginScreen());
    }
    @Override protected void onResume() {
        super.onResume();
        long id = app().userId;
        if (id < 1) { loginScreen(); return; }
        task(() -> repo().userName(id), name -> {
            ((TextView) findViewById(R.id.welcome)).setText("Olá, " + name + "!");
        }, error -> loginScreen());
    }
}
