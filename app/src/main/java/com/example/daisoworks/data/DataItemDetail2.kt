package com.example.daisoworks.data

data class DataItemDetail2(

    //거래처정보(생산:P , 발주:B, 송금:S)
    val clientNoP : String = "" ,        // 거래처번호(생산)
    val clientPreNoP : String = "" ,     // 거래처풀번호(생산)
    val clientNameP : String = "",       // 거래처명(생산)
    val clientNoB : String = "" ,        // 거래처번호(발주)
    val clientPreNoB : String = "" ,     // 거래처풀번호(발주)
    val clientNameB : String = "",       // 거래처명(발주)
    val clientNoS : String = "" ,        // 거래처번호(송금)
    val clientPreNoS : String = "" ,     // 거래처풀번호(송금)
    val clientNameS : String = "",       // 거래처명(송금)

    var expand2 : Boolean = false
)
