package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.sinhvien.quanlitruyen.DatabaseHelper;
import com.sinhvien.quanlitruyen.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class StatisticsActivity extends AppCompatActivity {
    private WebView webView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        dbHelper = new DatabaseHelper(this);
        webView = findViewById(R.id.webViewStats);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadStatistics();
    }

    private void loadStatistics() {
        try {
            JSONObject statsData = new JSONObject();

            // Daily stats
            Map<String, Integer> dailyStats = dbHelper.getReadingStatsByPeriod("daily");
            JSONArray dailyLabels = new JSONArray();
            JSONArray dailyData = new JSONArray();
            for (Map.Entry<String, Integer> entry : dailyStats.entrySet()) {
                dailyLabels.put(entry.getKey());
                dailyData.put(entry.getValue());
            }
            statsData.put("dailyLabels", dailyLabels);
            statsData.put("dailyData", dailyData);

            // Weekly stats
            Map<String, Integer> weeklyStats = dbHelper.getReadingStatsByPeriod("weekly");
            JSONArray weeklyLabels = new JSONArray();
            JSONArray weeklyData = new JSONArray();
            for (Map.Entry<String, Integer> entry : weeklyStats.entrySet()) {
                weeklyLabels.put("Tuần " + entry.getKey());
                weeklyData.put(entry.getValue());
            }
            statsData.put("weeklyLabels", weeklyLabels);
            statsData.put("weeklyData", weeklyData);

            // Monthly stats
            Map<String, Integer> monthlyStats = dbHelper.getReadingStatsByPeriod("monthly");
            JSONArray monthlyLabels = new JSONArray();
            JSONArray monthlyData = new JSONArray();
            for (Map.Entry<String, Integer> entry : monthlyStats.entrySet()) {
                monthlyLabels.put(entry.getKey());
                monthlyData.put(entry.getValue());
            }
            statsData.put("monthlyLabels", monthlyLabels);
            statsData.put("monthlyData", monthlyData);

            // Most read stories
            Map<String, Integer> mostReadStories = dbHelper.getMostReadStories();
            JSONArray storiesLabels = new JSONArray();
            JSONArray storiesData = new JSONArray();
            for (Map.Entry<String, Integer> entry : mostReadStories.entrySet()) {
                storiesLabels.put(entry.getKey());
                storiesData.put(entry.getValue());
            }
            statsData.put("storiesLabels", storiesLabels);
            statsData.put("storiesData", storiesData);

            // Most read genres
            Map<String, Integer> mostReadGenres = dbHelper.getMostReadGenres();
            JSONArray genresLabels = new JSONArray();
            JSONArray genresData = new JSONArray();
            for (Map.Entry<String, Integer> entry : mostReadGenres.entrySet()) {
                genresLabels.put(entry.getKey());
                genresData.put(entry.getValue());
            }
            statsData.put("genresLabels", genresLabels);
            statsData.put("genresData", genresData);

            String htmlContent = generateHtmlContent(statsData.toString());
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateHtmlContent(String jsonData) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<script src='https://cdn.jsdelivr.net/npm/chart.js'></script>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; padding: 20px; background: #F5F5F5; }" +
                "canvas { margin: 20px 0; }" +
                "h2 { color: #333333; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Thống kê đọc</h2>" +
                "<h3>Số lần đọc theo ngày</h3>" +
                "<canvas id='dailyChart'></canvas>" +
                "<h3>Số lần đọc theo tuần</h3>" +
                "<canvas id='weeklyChart'></canvas>" +
                "<h3>Số lần đọc theo tháng</h3>" +
                "<canvas id='monthlyChart'></canvas>" +
                "<h3>Truyện đọc nhiều nhất</h3>" +
                "<canvas id='storiesChart'></canvas>" +
                "<h3>Thể loại đọc nhiều nhất</h3>" +
                "<canvas id='genresChart'></canvas>" +
                "<script>" +
                "const data = " + jsonData + ";" +
                "new Chart(document.getElementById('dailyChart'), {" +
                "  type: 'bar'," +
                "  data: { labels: data.dailyLabels, datasets: [{ label: 'Số lần đọc', data: data.dailyData, backgroundColor: '#2196F3' }] }," +
                "  options: { scales: { y: { beginAtZero: true } } }" +
                "});" +
                "new Chart(document.getElementById('weeklyChart'), {" +
                "  type: 'bar'," +
                "  data: { labels: data.weeklyLabels, datasets: [{ label: 'Số lần đọc', data: data.weeklyData, backgroundColor: '#4CAF50' }] }," +
                "  options: { scales: { y: { beginAtZero: true } } }" +
                "});" +
                "new Chart(document.getElementById('monthlyChart'), {" +
                "  type: 'bar'," +
                "  data: { labels: data.monthlyLabels, datasets: [{ label: 'Số lần đọc', data: data.monthlyData, backgroundColor: '#FF9800' }] }," +
                "  options: { scales: { y: { beginAtZero: true } } }" +
                "});" +
                "new Chart(document.getElementById('storiesChart'), {" +
                "  type: 'bar'," +
                "  data: { labels: data.storiesLabels, datasets: [{ label: 'Số lần đọc', data: data.storiesData, backgroundColor: '#F44336' }] }," +
                "  options: { scales: { y: { beginAtZero: true } } }" +
                "});" +
                "new Chart(document.getElementById('genresChart'), {" +
                "  type: 'bar'," +
                "  data: { labels: data.genresLabels, datasets: [{ label: 'Số lần đọc', data: data.genresData, backgroundColor: '#9C27B0' }] }," +
                "  options: { scales: { y: { beginAtZero: true } } }" +
                "});" +
                "</script>" +
                "</body>" +
                "</html>";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}