package com.example.daisoworks.data

data class DataSampleDetail1(
    var sammgno : String = "",           // 샘플번호
    var samcolym : String = "",          // 샘플등록년도
    var clntpoolno : String = "",        // 샘플업체풀번호
    var clntnmkor : String = "",         // 샘플업체명
    var samnm : String = "",             // 샘플명
    var vtlpath : String = "",           // 샘플이미지 경로
    var filesec : String = "",           // 샘플이미지 확장자
    var sammgnof : String = ""           // 샘플이미지 파일명
)
