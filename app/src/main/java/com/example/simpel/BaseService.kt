package com.example.simpel

import com.example.annotations.FieldAnno

open class BaseService {
    fun hello(){}
    @FieldAnno val anno = ""
}

open class BaseService2 {
    fun bye(){}
    class baseServiceInside
    inner class baseServiceInner
}