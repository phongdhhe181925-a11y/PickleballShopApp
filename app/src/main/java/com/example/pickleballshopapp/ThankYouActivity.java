package com.example.pickleballshopapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ThankYouActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.topAppBar);
        ToolbarUtils.setupCommonToolbar(this, toolbar);

        TextView tvTitle = findViewById(R.id.tvThanksTitle);
        TextView tvMessage = findViewById(R.id.tvThanksMessage);
        Button btnContinue = findViewById(R.id.btnContinueShopping);

        SessionManager sessionManager = new SessionManager(this);
        String name = sessionManager.getFullName();
        tvTitle.setText("Cảm ơn, " + name + "!");

        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}



