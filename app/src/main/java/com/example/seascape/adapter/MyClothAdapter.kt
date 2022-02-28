package com.example.seascape.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.seascape.R
import com.example.seascape.eventbus.UpdateCartEvent
import com.example.seascape.listener.ICartLoadListener
import com.example.seascape.listener.IRecycleClickListener
import com.example.seascape.model.CartModel
import com.example.seascape.model.ClothModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyClothAdapter(
    private val context : Context,
    private val list : List<ClothModel>,
    private val cartListener : ICartLoadListener
) : RecyclerView.Adapter<MyClothAdapter.MyClothViewHolder>()
{
    class MyClothViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imageView : ImageView? = null
        var txtName : TextView? = null
        var txtPrice : TextView? = null

        private var clickListener : IRecycleClickListener? = null

        fun setClickListener(clickListener: IRecycleClickListener)
        {
            this.clickListener = clickListener
        }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            itemView.setOnClickListener(this)
        }

        override fun onClick(v : View?) {
            clickListener!!.onItemClickListener(v, adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyClothViewHolder {
        return MyClothViewHolder(LayoutInflater.from(context)
            .inflate(R.layout.layouy_cloth_item, parent, false))
    }

    override fun onBindViewHolder(holder: MyClothViewHolder, position: Int) {
        Glide.with(context)
            .load(list[position].image)
            .into(holder.imageView!!)
        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("Rs.").append(list[position].price)
        holder.setClickListener(object : IRecycleClickListener{
            override fun onItemClickListener(view: View?, position: Int) {
                addToCart(list[position])
            }
        })
    }

    private fun addToCart(clothModel: ClothModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(clothModel.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData : MutableMap<String, Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity + 1
                        updateData["quantity"] = cartModel!!.quantity + 1
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()

                        userCart.child(clothModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener { e-> cartListener.onLoadCartFailed(e.message) }
                    }else
                    {
                        val cartModel = CartModel()
                        cartModel.key = clothModel.key
                        cartModel.name = clothModel.name
                        cartModel.image = clothModel.image
                        cartModel.price = clothModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = clothModel.price!!.toFloat()

                        userCart.child(clothModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                org.greenrobot.eventbus.EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed("Success add to cart")
                            }
                            .addOnFailureListener { e-> cartListener.onLoadCartFailed(e.message) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })
    }

    override fun getItemCount(): Int {
        return list.size
    }
}