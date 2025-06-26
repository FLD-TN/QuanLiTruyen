package com.sinhvien.quanlitruyen;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import com.sinhvien.quanlitruyen.model.SearchResponse;

public interface SearchAPI {
    @GET("search")
    Call<SearchResponse> search(
            @Query("q") String query,
            @Query("lr") String langRestrict, // "lang_vi"
            @Query("hl") String language,     // "vi"
            @Query("gl") String region,       // "VN"
            @Query("num") int count           // 10
    );
}
