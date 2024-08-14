package com.example.daisoworks.data

data class DataItemDetail1(
   // @SerializedName("OUT_RESULT")
    //상품 일반정보
    var itemNo : String = "",           // 픔번
    var barcodeNo : String = "",        // 바코드번호
    var itemStorePrice : String = "" ,  // 매장가
    var itemCategory : String = "" ,    // 상품카테고리
    var itemDesc : String = "" ,        // 상품명
    var itemGrade : String = "" ,       // 상품등급
    var itemSalesLead : String = "" ,   // 매출주도
    var itemIpsu : String = "" ,        // 입수
    var itemDetail : String = "" ,      // 상품상세
    var itemPictureUrl :  String = "",  // 상품이미지 URL


    var expand1 : Boolean = true
)
