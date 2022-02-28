package com.example.seascape.listener

import com.example.seascape.model.CartModel

interface ICartLoadListener
{
    fun onLoadCartSuccess(cartModelList : List<CartModel>)
    fun onLoadCartFailed(message : String?)
}