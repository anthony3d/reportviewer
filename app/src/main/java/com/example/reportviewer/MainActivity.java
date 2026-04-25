package com.example.reportviewer;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView tv = new TextView(this);
        tv.setText("Report Viewer\n\nÎעןנאגüעו XLSX פאיכ קונוח Share/Send To");
        tv.setPadding(32, 32, 32, 32);
        tv.setTextSize(16);
        setContentView(tv);
    }
}