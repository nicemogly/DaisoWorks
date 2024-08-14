package com.example.daisoworks.data

data class DataItemDetail3(

     //담당부서
     val ownerDeptName : String = "",     // 상품담당부서명
     val ownerName : String = "" ,        // 상품담당자명
     val ownerCompany : String = "" ,     // 회사명
     val businessOwnerDept : String = "", // 영업담당부서명
     val businessOwnerName : String = "", // 영업담당자명
     val shipmentDept : String = "",      // 출하부서명
     val shipmentOwnerName : String = "", // 출하담당자

     var expand3 : Boolean = false
)
