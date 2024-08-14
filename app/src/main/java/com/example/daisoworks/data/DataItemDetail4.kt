package com.example.daisoworks.data

data class DataItemDetail4(

    //샘플정보
    val sampleNewItemNo : String = "",   // 신상품관리번호(NB)
    val sampleNItemNo : String = "",     // 샘플관리번호 (NA)
    val sampleCsNoteNo : String = "",    // 상담노트관리번호(BN)
    val sampleCsNoteItemNo : String = "",// 상담노트아이템 관리번호(CN)
    val exhName : String = "",           // 전시회명
    val exhPeriod : String = "",         // 전시회기간
    val exhDetail : String = "",          // 전시회 상세내용

    var expand4 : Boolean = false
)
