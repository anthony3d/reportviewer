package com.example.reportviewer;

import android.content.ContentResolver;
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
            } else {
                Toast.makeText(this, "Файл не найден", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    
    private void processExcelFile(Uri fileUri) {
        try {
            ContentResolver contentResolver = getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(fileUri);
            
            Workbook workbook = new XSSFWorkbook(inputStream);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Создаем заголовки
            addTableHeader();
            
            // Обрабатываем строки (начиная с первой, если в первой данные, а не заголовки)
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Пропускаем заголовок, если он есть
                
                processRow(row);
            }
            
            workbook.close();
            inputStream.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void addTableHeader() {
        TableRow headerRow = new TableRow(this);
        String[] headers = {"Начало недели", "Конец недели", "Значение", "+4 недели", "+5 недель"};
        
        for (String header : headers) {
            TextView tv = new TextView(this);
            tv.setText(header);
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
            int lastColumnNum = row.getLastCellNum();
            
            // 1. Второй столбец (индекс 1) - дата начала недели
            Cell startDateCell = row.getCell(1);
            Date startDate = getDateFromCell(startDateCell);
            
            if (startDate == null) return;
            
            // 2. Третий столбец (индекс 2) - дата конца недели
            Cell endDateCell = row.getCell(2);
            Date endDate = getDateFromCell(endDateCell);
            
            // 3. Предпоследний столбец (lastColumnNum - 2, т.к. индексация с 0)
            Cell valueCell = row.getCell(lastColumnNum - 2);
            String value = getNumericValue(valueCell);
            
            // 4. Дата +4 недели
            Date plus4Weeks = addWeeks(startDate, 4);
            
            // 5. Дата +5 недель
            Date plus5Weeks = addWeeks(startDate, 5);
            
            // Создаем строку таблицы
            TableRow tableRow = new TableRow(this);
            
            // Столбец 1: дата начала
            addCellToRow(tableRow, formatDate(startDate));
            
            // Столбец 2: дата конца (если есть)
            addCellToRow(tableRow, endDate != null ? formatDate(endDate) : "—");
            
            // Столбец 3: значение
            addCellToRow(tableRow, value);
            
            // Столбец 4: дата +4 недели
            addCellToRow(tableRow, formatDate(plus4Weeks));
            
            // Столбец 5: дата +5 недель
            addCellToRow(tableRow, formatDate(plus5Weeks));
            
            tableLayout.addView(tableRow);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addCellToRow(TableRow row, String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setPadding(12, 12, 12, 12);
        tv.setTextSize(13);
        row.addView(tv);
    }
    
    private Date getDateFromCell(Cell cell) {
        if (cell == null) return null;
        
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getDateCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                // Пробуем распарсить строку как дату
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                return sdf.parse(cell.getStringCellValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private String getNumericValue(Cell cell) {
        if (cell == null) return "—";
        
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                double val = cell.getNumericCellValue();
                if (val == (long) val) {
                    return String.valueOf((long) val);
                } else {
                    return String.valueOf(val);
                }
            } else if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            }
        } catch (Exception e) {
            return "Ошибка";
        }
        return "—";
    }
    
    private Date addWeeks(Date date, int weeks) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        return calendar.getTime();
    }
    
    private String formatDate(Date date) {
        if (date == null) return "—";
        return dateFormat.format(date);
    }
}