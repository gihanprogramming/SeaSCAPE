package com.example.seascape.sUser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.seascape.R
import com.example.seascape.adapter.MyClothAdapter
import com.example.seascape.eventbus.UpdateCartEvent
import com.example.seascape.listener.ICartLoadListener
import com.example.seascape.listener.IClothesListener
import com.example.seascape.model.CartModel
import com.example.seascape.model.ClothModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nex3z.notificationbadge.NotificationBadge
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class KidsActivity : AppCompatActivity(), IClothesListener, ICartLoadListener {
    lateinit var clothLoadListener : IClothesListener
    lateinit var cartLoadListener : ICartLoadListener
    lateinit var mainLayout : RelativeLayout
    lateinit var badge : NotificationBadge
    lateinit var btnBack : ImageView
    lateinit var ivCart : ImageView
    lateinit var recycle_clothes : RecyclerView

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
        countCartFromFirebase()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kids)
        mainLayout = findViewById(R.id.mainLayout)
        btnBack = findViewById(R.id.btnBack)
        ivCart = findViewById(R.id.ivCart)
        recycle_clothes = findViewById(R.id.recycle_clothes)
        badge = findViewById(R.id.badge)
        init()
        loadClothFromFirebase()
        countCartFromFirebase()

        btnBack.setOnClickListener {
            startActivity(Intent(this@KidsActivity, HomeActivity::class.java))
            finish()
        }
    }
    private fun loadClothFromFirebase(){
        val clothModels : MutableList<ClothModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Kids")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (clothSnapshot in snapshot.children){
                            val clothModel = clothSnapshot.getValue(ClothModel::class.java)
                            clothModel!!.key = clothSnapshot.key
                            clothModels.add(clothModel)
                        }
                        clothLoadListener.onClothLoadSuccess(clothModels)
                    }else{
                        clothLoadListener.onClothLoadFailed("Cloth items not exist")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    clothLoadListener.onClothLoadFailed(error.message)
                }

            })
    }
    private fun init()
    {
        clothLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this, 2)
        recycle_clothes.layoutManager = gridLayoutManager
        recycle_clothes.addItemDecoration(spaceItemDecoration())
        ivCart.setOnClickListener { startActivity(Intent(this, CartActivity::class.java)) }

    }

    override fun onClothLoadSuccess(clothModelList: List<ClothModel>) {
        val adapter = MyClothAdapter(this, clothModelList!!,cartLoadListener)
        recycle_clothes.adapter = adapter
    }

    override fun onClothLoadFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_SHORT).show()
    }

    override fun onLoadCartSuccess(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList!!) cartSum += cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout, message!!, Snackbar.LENGTH_LONG).show()
    }
    private fun countCartFromFirebase()
    {
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
                    cartLoadListener.onLoadCartSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
}