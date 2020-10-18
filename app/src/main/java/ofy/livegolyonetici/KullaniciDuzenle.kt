package ofy.livegolyonetici

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import khronos.toString
import khronos.with
import kotlinx.android.synthetic.main.activity_kullanici_duzenle.*
import java.util.*

class KullaniciDuzenle : AppCompatActivity() {
    var uid = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kullanici_duzenle)
        uid = intent.extras.get("uid") as String
        KullaniciBilgilericek()
        buttonayarlari()
        satinalimgecmisicek()
    }

    private fun satinalimgecmisicek() {
        FirebaseDatabase.getInstance().getReference().child("KullanicilarSatinAlma").child(uid)
                .addListenerForSingleValueEvent(
                        object :ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if (p0.exists()){
                                    val bilglist=ArrayList<satinbilgdata>()
                                    for (bilg in p0.children){
                                        if (bilg.child("orderId").exists()){
                                            val orderId=bilg.child("orderId").getValue() as String
                                            val alinansey=bilg.child("productId").getValue() as String
                                            val gun=bilg.child("purchaseTime").child("date").getValue() as Long
                                            val saat=bilg.child("purchaseTime").child("hours").getValue() as Long
                                            val dakika=bilg.child("purchaseTime").child("minutes").getValue() as Long
                                            val data=satinbilgdata(alinansey,orderId,gun.toString(),saat.toString()+":"+dakika.toString())
                                            bilglist.add(data)
                                        }
                                    }
                                    recolustursatinbilg(bilglist)
                                }
                            }

                        }
                )
    }

    private fun recolustursatinbilg(bilglist: ArrayList<satinbilgdata>) {
        val adpt = SatinbilgRec(bilglist, this)
        SatinBilg_Rec.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        SatinBilg_Rec.adapter = adpt
    }

    private fun buttonayarlari() {
        mesaj_yl.setOnClickListener {
            val intt = Intent(this@KullaniciDuzenle, Mesaj::class.java)
            intt.putExtra("uid",uid)
            startActivity(intt)
        }
        ysk_kl.setOnClickListener {
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("ayarlar")
                    .child("stil").setValue("LS")
        }
        yskl.setOnClickListener {
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("ayarlar")
                    .child("stil").setValue(1)
        }
        kaydet_kisiduznle.setOnClickListener { kaydetyapilacak() }
    }

    private fun kaydetyapilacak() {
        if (abone_rd.isChecked&&(abone_ay.text.isEmpty()||abone_gun.text.isEmpty())){
            Toast.makeText(this,"Abonelik Verdiniz Fakat Gun ve Ay Girmediniz",Toast.LENGTH_SHORT).show()
        }else if(Puan.text.isEmpty()){
            Toast.makeText(this,"Puan bos birakilamaz eger 0  sa 0 yazin",Toast.LENGTH_SHORT).show()
        }else{
            val abonedurum=abone_rd.isChecked
            val puan=Puan.text.toString()
            val aboneay=abone_ay.text.toString()
            val abonegun=abone_gun.text.toString()
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("abonelik").setValue(abonedurum)
            if (abonedurum){
                val degisecek=Zaman.with(month = aboneay.toInt(),day = abonegun.toInt())
                FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("abonelikzaman").setValue(degisecek)
            }
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("coin").setValue(puan)
            Toast.makeText(this,"Kaydedildi",Toast.LENGTH_SHORT).show()
        }
    }

    private fun KullaniciBilgilericek() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val Nick = p0.child("nick").getValue() as String
                        val puan= p0.child("coin").getValue() as String
                        val abonedrm=p0.child("abonelik").getValue() as  Boolean
                        var abonegun=""
                        var aboneay=""
                        if (abonedrm){
                            abone_rd.isChecked=true
                            val date=p0.child("abonelikzaman").getValue(Date::class.java)!!
                            abonegun=date.toString("dd")
                            aboneay=date.toString("MM")
                        }else{
                            free_rd.isChecked=true
                        }
                        nick.setText(Nick)
                        Puan.setText(puan)
                        abone_ay.setText(aboneay)
                        abone_gun.setText(abonegun)
                        uid_texw.setText(uid)

                    }

                }
        )
    }
}
