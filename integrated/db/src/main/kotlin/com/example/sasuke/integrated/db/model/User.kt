package com.example.sasuke.integrated.db.model

import com.example.sasuke.integrated.common.user.Authority
import com.example.sasuke.integrated.common.user.Grade

data class User(
    val id: Long = 0L,
    var name: String? = "",
    var nickname: String? = "",
    var email: String? = "",
    var grade: Grade = Grade.GREEN,
    var point: Long = 0,
    var authority: Authority = Authority.NORMAL,
)