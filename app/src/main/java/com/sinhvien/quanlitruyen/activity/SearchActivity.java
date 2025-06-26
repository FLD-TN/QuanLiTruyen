package com.sinhvien.quanlitruyen.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sinhvien.quanlitruyen.R;
import com.sinhvien.quanlitruyen.SearchAPI;
import com.sinhvien.quanlitruyen.adapter.SearchResultAdapter;
import com.sinhvien.quanlitruyen.model.SearchResponse;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {

    private EditText edtSearch;
    private Button btnSearch;
    private RecyclerView recyclerResults;
    private SearchResultAdapter adapter;

    private static final String BASE_URL = "https://google-search72.p.rapidapi.com/";
    private static final String API_KEY = "eac3eeee12msh2086ce12ec3cb85p161ea9jsn020a05afd20e"; // ⚠️ Replace with your actual key
    private static final String API_HOST = "google-search72.p.rapidapi.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        edtSearch = findViewById(R.id.edtSearch);
        btnSearch = findViewById(R.id.btnSearch);
        recyclerResults = findViewById(R.id.recyclerResults);
        recyclerResults.setLayoutManager(new LinearLayoutManager(this));

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = edtSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    searchOnline(query);
                } else {
                    Toast.makeText(SearchActivity.this, "Nhập từ khóa tìm kiếm", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void searchOnline(String query) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws java.io.IOException {
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("X-RapidAPI-Key", API_KEY)
                                .addHeader("X-RapidAPI-Host", API_HOST)
                                .build();
                        return chain.proceed(newRequest);
                    }
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        SearchAPI api = retrofit.create(SearchAPI.class);

        Call<SearchResponse> call = api.search(query, "lang_vi", "vi", "VN", 10);

        call.enqueue(new Callback<SearchResponse>() {
            @Override
            public void onResponse(Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<SearchResponse.Result> results = response.body().items;
                    adapter = new SearchResultAdapter(results, SearchActivity.this);
                    recyclerResults.setAdapter(adapter);
                } else {
                    Toast.makeText(SearchActivity.this, "Không có kết quả.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SearchResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
