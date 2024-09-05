package com.example.daisoworks.data

data class DataSujuDetail1(
    val sujumginitno : String= "",           //수주관리번호별칭
    val sujumgno : String= "",               //수주관리번호
    val sujudate : String= "",               //수주일
    val sujuamt : String= "",                //매장가
    val sujuipsu : String= "",               //입수
    val sujuper : String=" " ,               //개별수량
    val sujutcategory : String= "",          //통합분류카테고리
    val sujuitemcategory : String= "",       //상품분류카테고리
    val sujuitemdesc : String= "",           //품명
    val sujuitemno : String = "",            //품번
    val sujubarcode : String= "",            //바코드
    val sujudelicondition : String= "",      //운송
    val sujumadein : String= "",             //원산지
    var expand1 : Boolean = true
)

