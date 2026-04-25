package com.example.reportviewer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ReportViewerActivity extends AppCompatActivity {
    
    private TableLayout tableLayout;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_viewer);
        
        tableLayout = findViewById(R.id.tableLayout);
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Uri fileUri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (fileUri != null) {
                processExcelFile(fileUri);
            }
        }
    }
    
    private void processExcelFile(Uri fileUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(fileUri);
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            addHeader();
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                processRow(row);
            }
            
            workbook.close();
            inputStream.close();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    
    private void addHeader() {
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Начало", "Конец", "Значение", "+4 нед", "+5 нед"};
        for (String h : headers) {
            TextView tv = new TextView(this);
            tv.setText(h);
            tv.setPadding(16, 16, 16, 16);
            tv.setBackgroundResource(android.R.drawable.editbox_background);
            tv.setTextSize(14);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            headerRow.addView(tv);
        }
        tableLayout.addView(headerRow);
    }
    
    private void processRow(Row row) {
        try {
            int lastCol = row.getLastCellNum();
            
            Cell startCell = row.getCell(1);
            Date startDate = startCell != null && startCell.getCellType() == CellType.NUMERIC ? startCell.getDateCellValue() : null;
            if (startDate == null) return;
            
            Cell endCell = row.getCell(2);
            Date endDate = endCell != null && endCell.getCellType() == CellType.NUMERIC ? endCell.getDateCellValue() : null;
            
            Cell valueCell = row.getCell(lastCol - 2);
            String value = valueCell != null ? String.valueOf(valueCell.getNumericCellValue()) : "—";
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate);
            cal.add(Calendar.WEEK_OF_YEAR, 4);
            Date plus4 = cal.getTime();
            
            cal.setTime(startDate);
            cal.add(Calendar.WEEK_OF_YEAR, 5);
            Date plus5 = cal.getTime();
            
            TableRow tableRow = new TableRow(this);
            addCell(tableRow, dateFormat.format(startDate));
            addCell(tableRow, endDate != null ? dateFormat.format(endDate) : "—");
            addCell(tableRow, value);
            addCell(tableRow, dateFormat.format(plus4));
            addCell(tableRow, dateFormat.format(plus5));
            tableLayout.addView(tableRow);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addCell(TableRow row, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 12, 12, 12);
        tv.setTextSize(13);
        row.addView(tv);
    }
}