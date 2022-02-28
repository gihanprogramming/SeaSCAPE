package com.example.seascape.sUser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seascape.R
import com.example.seascape.adapter.MyCartAdapter
import com.example.seascape.eventbus.UpdateCartEvent
import com.example.seascape.listener.ICartLoadListener
import com.example.seascape.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class CartActivity : AppCompatActivity(), ICartLoadListener {

    lateinit var recycle_cart : RecyclerView
    lateinit var btnBack : ImageView
    lateinit var mainLayout : RelativeLayout
    lateinit var txtTotal : TextView
    var cartLoadListener : ICartLoadListener? = null

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCartEvent(event : UpdateCartEvent){
        loadCartFromFirebase()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        mainLayout = findViewById(R.id.mainLayout)
        recycle_cart = findViewById(R.id.recycle_cart)
        txtTotal = findViewById(R.id.txtTotal)
        btnBack = findViewById(R.id.btnBack)

        init()
        loadCartFromFirebase()

    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children){
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }
            })
    }

    private fun init (){
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycle_cart.layoutManager = layoutManager
        recycle_cart.addItemDecoration(DividerItemDecoration(this, layoutManager.orientation))
        btnBack.setOnClickListener { finish() }
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var sum = 0.0
        for (cartModel in cartModelList)
        {
            sum += cartModel.totalPrice
        }
        txtTotal.text = StringBuilder("Rs.").append(sum)
        val adapter = MyCartAdapter(this, cartModelList)
        recycle_cart.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
}