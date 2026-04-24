package com.example.reportviewer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        TextView tv = new TextView(this);
        tv.setText("Report Viewer\n\nИспользуй Share/Send To для отправки XLSX файла");
        tv.setPadding(32, 32, 32, 32);
        tv.setTextSize(16);
        
        setContentView(tv);
    }
}