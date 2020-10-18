package ofy.livegolyonetici

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_girisac.*
import com.google.android.gms.ads.AdView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import khronos.Dates
import khronos.toString
import khronos.with
import kotlinx.android.synthetic.main.activity_main.*
import ofy.livegol.datas.canlimesajdata
import java.util.*
import kotlin.collections.ArrayList
import android.hardware.usb.UsbDevice.getDeviceId
import android.telephony.TelephonyManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging
import ofy.livegolyonetici.services.fcminterface
import ofy.livegolyonetici.services.fcmmodel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


lateinit var Zaman: Date
var serverkey = ""
val baseurl = "https://fcm.googleapis.com/fcm/"

class Girisac : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_girisac)
        loginle()
        button2.setOnClickListener {
            val intt = Intent(this, MainActivity::class.java)
            startActivity(intt)
        }
        button.setOnClickListener {
            val intt = Intent(this, Kulannici::class.java)
            startActivity(intt)
        }
        button3.setOnClickListener {
          kullanicitemizle()
        }
        tarihyaz()
        serverkeycek()
        silinmismesajitemizle()
    }

    private fun loginle() {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if (it.isComplete){
                Log.e("Sing","Girisvar")
                val uid=FirebaseAuth.getInstance().currentUser!!.uid
                if (uid!="Inp7bnXfEgZ2xlS6ETt9199BHCd2"){
                    tokenimicek(uid)
                }

            }else{
                Log.e("Sing","Girisyok")

            }
        }
    }

    private fun tokenimicek(uid:String) {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("h", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    } else {

                        val token = task.result?.token
                        Log.e("j", token)
                        FirebaseDatabase.getInstance().reference.child("AdminTokenS").child(uid).setValue(token)
                    }
                })

    }


    private fun silinmismesajitemizle() {
        val mesajlistcanli = ArrayList<canlimesajdata>()
        FirebaseDatabase.getInstance().reference.child("canlimesaj").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                mesajlistcanli.clear()
                if (p0.exists()) {
                    for (mesaj in p0.children) {
                        val mesajd = mesaj.getValue(canlimesajdata::class.java)
                        if (!mesajd!!.engellimi_mesaj!!) {
                            mesajlistcanli.add(mesajd)
                        }
                    }
                    p0.ref.setValue(mesajlistcanli)

                }
            }


        })
    }

    private fun kullanicitemizle() {
        FirebaseDatabase.getInstance().reference.child("Kullanicilar").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (uids in p0.children) {
                    try {
                        val Coin = uids.child("coin").getValue() as String
                        val Abonelik = uids.child("abonelik").getValue() as Boolean
                        val Rekl = uids.child("reklamGun").getValue() as String
                    } catch (e: Exception) {
                        try {
                            val abonelik = uids.child("abonelik").getValue() as Boolean
                            if (!abonelik) {
                                uids.ref.removeValue()
                            }
                        } catch (e: Exception) {
                            uids.ref.removeValue()
                        }

                    }

                }
            }

        })
    }

    private fun tarihyaz() {
        Zaman = Dates.today
        FirebaseDatabase.getInstance().getReference().child("YeniZaman").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                val kayitlizmn = p0.getValue(Date::class.java)
                if (kayitlizmn!!.date != Zaman.date) {
                    mesajlarisil()
                    ReklamGunata()
                }
                FirebaseDatabase.getInstance().getReference().child("YeniZaman").setValue(Zaman)
            }



        })


    }
    private fun ReklamGunata() {
        FirebaseDatabase.getInstance().reference.child("Kullanicilar").addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                for (uids in p0.children){
                    uids.child("reklamGun").ref.setValue(Zaman.toString("dd"))
                    uids.child("reklamkalan").ref.setValue("10")
                }
            }

        })
    }
    private fun mesajlarisil() {
        FirebaseDatabase.getInstance().reference.child("canlimesaj").removeValue()
    }


    private fun serverkeycek() {
        FirebaseDatabase.getInstance().getReference().child("ServerKey").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            serverkey = p0.getValue() as String
                        }
                    }

                }
        )
    }

}
