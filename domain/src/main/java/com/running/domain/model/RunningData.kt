package com.running.domain.model

data class RunningData (
    var id: Int,
    var dayOfWeek: String,
    var now: String,
    val time:String,
    val distance:String,
    val calorie:String,
    val step:String
)