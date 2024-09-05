package com.example.daisoworks.data

data class DataSujuDetail3(
    val buyerinfo : String= "",           //바이어정보
    val buyercif : String= "",            //Cost, Insurance, and Freight
    val buyertax : String= "",            //세금계산서
    var expand3 : Boolean = false
)
