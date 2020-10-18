package ofy.livegolyonetici

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.MotionEvent
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_kupon_ayrinti.*

class KuponAyrinti : AppCompatActivity() {
    var position = 0
    var uzanti = ""
    val KuponduzList=ArrayList<Kupondata> ()
    val KuponduzList2=ArrayList<Canlidata> ()
    var KUPON=Kupondata("Bos","Bos",ArrayList<macdata>())
    var Canlikupon=Canlidata("Bos","Bos",ArrayList<macdata>(),"")
    var canlimi=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kupon_ayrinti)
        buttonayarlari()
        position = intent.extras.get("position") as Int
        uzanti = intent.extras.get("uzanti") as String
        referanslar[position].addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            if (p0.child("kupondali").exists()){
                                val kupon = p0.getValue(Canlidata::class.java)
                                Canlikupon=kupon!!
                                Bilgileriduzenle(kupon!!)
                                canlimi=true
                            }else{
                                val kupon = p0.getValue(Kupondata::class.java)
                                KUPON=kupon!!
                                Bilgileriduzenle(kupon!!)
                            }

                        }
                    }

                })
    }

    private fun buttonayarlari() {
        ka_Devamediyor.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Devamediyor.elevation = 5F
                    ka_kupondurum.setText("Maç Devam Ediyor")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Devamediyor.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Devamediyor.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kaybetti.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Kaybetti.elevation = 5F
                    ka_kupondurum.setText("Kaybetti")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kaybetti.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kaybetti.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kazandi.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Kazandi.elevation = 5F
                    ka_kupondurum.setText("Kazandi")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kazandi.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kazandi.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Sil.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    referanslar[position].removeValue()
                    Toast.makeText(this,"Silindi",Toast.LENGTH_SHORT).show()
                    if (canlimi){
                        Guncelle2()
                    }else{
                    Guncelle()}
                    ka_Sil.elevation = 5F
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Sil.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Sil.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kaydet.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    kaydet()
                    ka_Kaydet.elevation = 5F
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kaydet.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kaydet.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }

    }
    private fun Guncelle2() {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        KuponduzList2.clear()
                        if (p0.exists()){
                            for (kupon in p0.children){
                                val kupodt=kupon.getValue(Canlidata::class.java)
                                KuponduzList2.add(kupodt!!)
                            }
                            Guncelkaydet2()
                        }else{
                            val intt = Intent(this@KuponAyrinti,MainActivity::class.java)
                            startActivity(intt)
                            finish()
                        }
                    }

                }
        )
    }
    private fun Guncelle() {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        KuponduzList.clear()
                        if (p0.exists()){
                            for (kupon in p0.children){
                                val kupodt=kupon.getValue(Kupondata::class.java)
                                KuponduzList.add(kupodt!!)
                            }
                            Guncelkaydet()
                        }else{
                            val intt = Intent(this@KuponAyrinti,MainActivity::class.java)
                            startActivity(intt)
                            finish()
                        }
                    }

                }
        )
    }
    private fun Guncelkaydet2() {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).setValue(KuponduzList2)
        val intt = Intent(this,MainActivity::class.java)
        startActivity(intt)
        finish()
    }
    private fun Guncelkaydet() {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).setValue(KuponduzList)
        var uz=""
        when(uzanti){
            "Basketbol"->{uz="Basket"}
            "Supriz"->{uz="Supriz"}
            "BankoKup"->{uz="Banko"}
        }
        FirebaseDatabase.getInstance().reference.child("Yorumlar").child(uz).removeValue()
        val intt = Intent(this,MainActivity::class.java)
        startActivity(intt)
        finish()
    }

    private fun kaydet() {
        if (KUPON.Kuponismi=="Bos"){
            val kuponadi=ka_kuponismi.text.toString()
            val kupondurum=ka_kupondurum.text.toString()
            val KuponDat=Canlidata(kuponadi,kupondurum,Canlikupon.maclar!!,Canlikupon.Kupondali!!)
            referanslar[position].setValue(KuponDat)
            Toast.makeText(this,"Kaydedildi",Toast.LENGTH_SHORT).show()
            val intt = Intent(this,MainActivity::class.java)
            startActivity(intt)
            finish()
        }else{
            val kuponadi=ka_kuponismi.text.toString()
            val kupondurum=ka_kupondurum.text.toString()
            val KuponDat=Kupondata(kuponadi,kupondurum,KUPON.maclar!!)
            referanslar[position].setValue(KuponDat)
            Toast.makeText(this,"Kaydedildi",Toast.LENGTH_SHORT).show()
            val intt = Intent(this,MainActivity::class.java)
            startActivity(intt)
            finish()
        }



    }

    private fun Bilgileriduzenle(kupon: Kupondata) {
        ka_kuponismi.setText(kupon.Kuponismi)
        ka_kupondurum.setText(kupon.Kupondurumu)
        val maclar = kupon.maclar!!
        val adpt = macrecw(maclar, this)
        ka_Recmac.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        ka_Recmac.adapter = adpt
    }
    private fun Bilgileriduzenle(kupon: Canlidata) {
        ka_kuponismi.setText(kupon.Kuponismi)
        ka_kupondurum.setText(kupon.Kupondurumu)
        val maclar = kupon.maclar!!
        val adpt = macrecw(maclar, this)
        ka_Recmac.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        ka_Recmac.adapter = adpt
    }
}
