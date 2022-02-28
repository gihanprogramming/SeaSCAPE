package com.example.seascape.listener

import com.example.seascape.model.ClothModel

interface IClothesListener
{
    fun onClothLoadSuccess(clothModelList:List<ClothModel>)
    fun onClothLoadFailed(message:String?)
}