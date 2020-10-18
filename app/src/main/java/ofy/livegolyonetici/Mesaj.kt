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
import kotlinx.android.synthetic.main.activity_mesaj.*
import ofy.livegolyonetici.services.fcminterface
import ofy.livegolyonetici.services.fcmmodel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Mesaj : AppCompatActivity() {
    var uid=""
    var nick=""
    var token=""
    val mesajlarlis=ArrayList<mesajdata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mesaj)
        uid=intent.extras.get("uid") as String
        kullaniciadim()
        butoon()
        mesajlaricek()
    }
    private fun kullaniciadim() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()){
                            nick=p0.child("nick").getValue() as String
                            token=p0.child("token").getValue() as String
                            atama()

                        }
                    }

                }
        )
    }

    private fun atama() {
        nick_mesaj.setText(nick)
        sil_mesaj.setOnClickListener {
            FirebaseDatabase.getInstance().getReference().child("KullaniciMesaj").child(uid).removeValue()
            Toast.makeText(this,"Konu Kapandi,Mesajlar Silindi",Toast.LENGTH_SHORT).show()
            val intt=Intent(this,Kulannici::class.java)
            startActivity(intt)
            finish()
        }

    }

    private fun mesajlaricek() {
        FirebaseDatabase.getInstance().getReference().child("KullaniciMesaj").child(uid).addValueEventListener(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()){
                            mesajlarlis.clear()
                            for (data in p0.children){
                                val mesaj=data.getValue(mesajdata::class.java)
                                mesajlarlis.add(mesaj!!)
                            }
                            recolustur()
                        }
                    }

                }
        )
    }

    private fun recolustur() {
        var adpt = MesajRec(mesajlarlis,this)
        KonusmaRec.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        KonusmaRec.adapter = adpt
        KonusmaRec.scrollToPosition(mesajlarlis.size - 1)
    }

    private fun butoon() {
        KonusmaMesajSend.setOnClickListener {

            mesajiyolla()

        }
    }

    private fun mesajiyolla() {
        if (KonusmaMesaj.text.isNotEmpty()){
            val data=mesajdata(KonusmaMesaj.text.toString(),"Admin")
            FirebaseDatabase.getInstance().getReference().child("KullaniciMesaj").child(uid).addListenerForSingleValueEvent(
                    object: ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.exists()){
                                val sayi=p0.childrenCount
                                p0.child(sayi.toString()).ref.setValue(data)
                            }else{
                                p0.child("0").ref.setValue(data)
                            }
                            bildirimadmin("Bir Mesajiniz Var",KonusmaMesaj.text.toString(),token)
                            KonusmaMesaj.text.clear()
                        }

                    } )
        }else{
            Toast.makeText(this,"Boş mesaj yollanamaz", Toast.LENGTH_SHORT).show()
        }
    }


    private fun bildirimadmin(baslik: String, mesaj: String,token:String) {
            val headerd = HashMap<String, String>()
            headerd.put("Content-Type", "application/json")
            headerd.put("Authorization", "key=" + serverkey )
            val data = fcmmodel.Data(mesaj, baslik)
            val bildirim = fcmmodel(data, token)
            val etrofit = Retrofit.Builder().baseUrl(baseurl)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val int = etrofit.create(fcminterface::class.java)
            val istek = int.bildirimgonder(headerd, bildirim)
            istek.enqueue(object : Callback<Response<fcmmodel>> {
                override fun onFailure(call: Call<Response<fcmmodel>>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<Response<fcmmodel>>?, response: Response<Response<fcmmodel>>?) {
                    Toast.makeText(this@Mesaj,"Bildirim yollandi. Mesaj: "+mesaj,Toast.LENGTH_SHORT).show()
                }
            })

    }
}
