package com.example.runningmate2

class Calorie {
    fun myCalorie() : Double{
        val myWeight = 70
        if(myWeight <= 50){
            return 0.17
        }else if(myWeight <= 55){
            return 0.2
        }else if(myWeight <= 60){
            return 0.22
        }else if(myWeight <= 65){
            return 0.24
        }else if(myWeight <= 70){
            return 0.26
        }else if(myWeight <= 75){
            return 0.28
        }else if(myWeight <= 80){
            return 0.3
        }else if(myWeight <= 85){
            return 0.32
        }else if(myWeight <= 90){
            return 0.34
        }else if(myWeight <= 95){
            return 0.36
        }else return 0.38
    }
}