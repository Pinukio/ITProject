package com.example.itproject

data class MSModel (val type : Int, val text : String = "", val text2 : String = "", val progress : Int = 0) {
    companion object {
        const val TOP = 0
        const val CARD_TYPE = 1
    }
}
