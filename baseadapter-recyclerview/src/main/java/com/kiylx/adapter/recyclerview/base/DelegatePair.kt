package com.kiylx.adapter.recyclerview.base

data class DelegatePair<T : Any>(val type: Int, val delegate: ItemViewDelegate<T>)