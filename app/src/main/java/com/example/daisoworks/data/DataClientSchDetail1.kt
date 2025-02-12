package com.example.daisoworks.data

data class DataClientSchDetail1(
    var clientNoP : String = "",     //거래처번호(생산기준)
    var clientPreNoP : String = "",  //거래처풀번호(생산기준)
    var clientBizNoP : String = "" , //거래처풀번호(생산기준)
    var clientBizMNoP : String = "" , //사업자관리번호
    var clientBizNameK : String = "", //거래처명(한글)
    var clientBizAddrK : String = "", //거래처주소(한글)
    var clientBizCeoK : String = "", //거래처대표자(한글)
    var clientBizNameE : String = "", //거래처명(영어)
    var clientBizAddrE : String = "", //거래처주소(영어)
    var clientBizCeoE : String = "", //거래처대표자(영어)
    var clientBizNameC : String = "", //거래처명(중국어)
    var clientBizAddrC : String = "", //거래처주소(중국어)
    var clientBizCeoC : String = "", //거래처대표자(중국어)
    var clientBizCountry : String = "", //거래처국가
    var clientBizKind : String = "",   //거래처유형
    var clientBizTel : String = "",   //거래처 연락처
    var clientBizHomepage : String = "", //거래처홈페이지
    var clientBizEmail : String = "", //거래처전자우편
    var expand1 : Boolean = true

)
