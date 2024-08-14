package com.example.daisoworks

import com.example.daisoworks.data.MovieData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("kobisopenapi/webservice/rest/boxoffice/searchDailyBoxOfficeList.json") // 아까 말했던 요청주소를 넣습니다.
    //추상메소드명안에 요청 파라미터들을 정의해서 넣어줍니다.
    fun getmovieinfo(
        @Query("key") key: String,
        @Query("targetDt") targetDt: String,  // 조회하고자 하는 날짜를 yyyymmdd 형식으로 입력합니다.
        @Query("itemPerPage") itemPerPage: String, // 결과 ROW 의 개수를 지정합니다.(default : “10”, 최대 : “10“)
        @Query("multiMovieYn") multiMovieYn: String, // “Y” : 다양성 영화 “N” : 상업영화 (default : 전체)
        @Query("repNationCd") repNationCd: String, // “K: : 한국영화 “F” : 외국영화 (default : 전체)
        @Query("wideAreaCd") wideAreaCd: String // 상영지역별로 조회할 수 있으며, 지역코드는 공통코드 조회 서비스에서 “0105000000” 로서 조회된 지역코드 입니다. (default : 전체)
    ): Call<MovieData> // 콜객체에 요청할 데이터클래스를 담아 작성해줍니다.(DTO 클래스명)
}