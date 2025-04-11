package com.example.daisoworks.data

data class DataSampleDetail2(
    var sammgno   : String = "",           // 샘플번호
    var samnm   : String = "",           // 샘플명
    var supdeptcd : String = "",           // 담당부서코드
    var deptsnme  : String = "",           // 담당부서명
    var deptcde   : String = "",           // 담당팀코드
    var deptnme   : String = "",           // 담당팀명
    var vtlpath   : String = "",           // 샘플이미지경로
    var clntnmkor : String = "",           // 샘플업체명
    var adpgbn    : String = "",           // 채택여부(0:미채택,1:채택,8:보완)
    var rsnnme    : String = "",           // 진행상태코드
    var rsncde    : String = ""            // 진행상태사유
)
