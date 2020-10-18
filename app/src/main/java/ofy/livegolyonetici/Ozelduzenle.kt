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
import kotlinx.android.synthetic.main.activity_ozelduzenle.*

class Ozelduzenle : AppCompatActivity() {
  var maclar=ArrayList<macdata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ozelduzenle)
        kuponbilgisinicek()
        buttonayarlari()
    }

    private fun kuponbilgisinicek() {
        FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").child("Kupon").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val kupon = p0.getValue(Kupondata::class.java)
                    Bilgileriduzenle(kupon!!)
                }
            }

        })
    }
    private fun Bilgileriduzenle(kupon: Kupondata) {
        ka_kuponismi_2.setText(kupon.Kuponismi)
        ka_kupondurum_2.setText(kupon.Kupondurumu)
        maclar = kupon.maclar!!
        val adpt = macrecw(maclar, this)
        ka_Recmac_2.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        ka_Recmac_2.adapter = adpt
    }
    private fun buttonayarlari() {
        ka_Devamediyor_2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Devamediyor_2.elevation = 5F
                    ka_kupondurum_2.setText("Maç Devam Ediyor")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Devamediyor_2.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Devamediyor_2.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kaybetti_2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Kaybetti_2.elevation = 5F
                    ka_kupondurum_2.setText("Kaybetti")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kaybetti_2.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kaybetti_2.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kazandi_2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    ka_Kazandi_2.elevation = 5F
                    ka_kupondurum_2.setText("Kazandi")
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kazandi_2.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kazandi_2.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Sil_2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    Toast.makeText(this, "Silindi", Toast.LENGTH_SHORT).show()
                    Sil()
                    ka_Sil_2.elevation = 5F
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Sil_2.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Sil_2.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }
        ka_Kaydet_2.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_UP -> {
                    kaydet()
                    ka_Kaydet_2.elevation = 5F
                }
                MotionEvent.ACTION_DOWN -> {
                    ka_Kaydet_2.elevation = 15F
                }
                MotionEvent.ACTION_CANCEL -> {
                    ka_Kaydet_2.elevation = 15F
                }
            }
            return@setOnTouchListener true
        }

    }

    private fun Sil() {
        FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").removeValue()
        FirebaseDatabase.getInstance().reference.child("Yorumlar").child("Ozel").removeValue()
    }

    private fun kaydet() {
        if (ka_kuponismi_2.text.isNotEmpty()){
        val kuponadi = ka_kuponismi_2.text.toString()
        val kupondurum = ka_kupondurum_2.text.toString()
        val KuponDat = Kupondata(kuponadi, kupondurum,maclar)
        FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").child("Kupon").setValue(KuponDat)
        Toast.makeText(this, "Kaydedildi", Toast.LENGTH_SHORT).show()
        val intt = Intent(this, MainActivity::class.java)
        startActivity(intt)
        finish()}
        else{
            Toast.makeText(this,"Kupon adini giriniz",Toast.LENGTH_SHORT).show()
        }

    }
}
