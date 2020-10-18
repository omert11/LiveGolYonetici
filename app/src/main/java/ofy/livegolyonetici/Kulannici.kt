package ofy.livegolyonetici

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import khronos.toString
import kotlinx.android.synthetic.main.activity_kulannici.*
import java.util.*
import kotlin.collections.ArrayList

class Kulannici : AppCompatActivity() {
    val gorunmez = View.INVISIBLE
    val gorunur = View.VISIBLE
    val nicklist=ArrayList<abonedata>()
    val   gosterilecek=ArrayList<abonedata>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kulannici)
        mesajlaricek()
        abonelericek()
        istatistikcek()
        tablaylistener()
        aramalistener()
        Nicklistolustur()
    }

    private fun Nicklistolustur() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        nicklist.clear()
                        for (uids in p0.children){
                            try {
                                val nick=uids.child("nick").getValue() as String
                                val uid=uids.key!!
                                val song=uids.child("songorulmem").getValue(Date::class.java)!!
                                val data=abonedata(nick,song.toString("dd / MM"),uid)
                                nicklist.add(data)
                            }catch(e:Exception){
                                Log.e("HATALI KUL",uids.key)
                            }

                        }
                    }

                }
        )
    }

    private fun aramalistener() {
        Kullanici_adett.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val kisiisim=s.toString().toUpperCase()
                kisilericikar(kisiisim)
            }

        })
    }

    private fun kisilericikar(kisiisim: String) {
        gosterilecek.clear()
        nicklist.forEach {
            val nick=it.abonenick!!.toUpperCase()
            if (nick.length>=kisiisim.length){
            if (nick.substring(0,kisiisim.length).equals(kisiisim)){
                gosterilecek.add(it)
            }}
        }
        recolusturaranan()
    }

    private fun recolusturaranan() {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this, KullaniciDuzenle::class.java)
            intt.putExtra("uid", gosterilecek[position].aboneuid)
            startActivity(intt)
        }
        val adpt = abonereceler(gosterilecek, this, itemOnClick)
        kullinici_rec_sng.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        kullinici_rec_sng.adapter = adpt

    }

    private fun tablaylistener() {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0!!.position) {
                    0 -> {
                        l_Istatistik.visibility = gorunur
                        l_mesajlar.visibility = gorunmez
                        l_puanlar.visibility = gorunmez
                        l_kullaniciara.visibility=gorunmez

                    }
                    1 -> {
                        l_Istatistik.visibility = gorunmez
                        l_mesajlar.visibility = gorunur
                        l_puanlar.visibility = gorunmez
                        l_kullaniciara.visibility=gorunmez

                    }
                    2 -> {
                        l_Istatistik.visibility = gorunmez
                        l_mesajlar.visibility = gorunmez
                        l_puanlar.visibility = gorunur
                        l_kullaniciara.visibility=gorunmez

                    }
                    3->{
                        l_Istatistik.visibility = gorunmez
                        l_mesajlar.visibility = gorunmez
                        l_puanlar.visibility = gorunmez
                        l_kullaniciara.visibility=gorunur
                    }
                }
            }

        })
    }


    private fun mesajlaricek() {
        FirebaseDatabase.getInstance().getReference().child("KullaniciMesaj").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val mesajlaruidler = ArrayList<String>()
                if (p0.exists()) {
                    for (uids in p0.children) {
                        val uid = uids.key!!
                        mesajlaruidler.add(uid)
                    }
                    bilgilerinicekmesaj(mesajlaruidler)
                }
            }

        })
    }

    private fun bilgilerinicekmesaj(mesajlaruidler: ArrayList<String>) {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val mesajcilist = ArrayList<abonedata>()
                        for (uid in p0.children) {
                            mesajlaruidler.forEach {
                                if (it == uid.key!!) {
                                    val nick = uid.child("nick").getValue() as String
                                    val uidabone = uid.key
                                    mesajcilist.add(abonedata(nick, uidabone!!, "mesajvar"))
                                }
                            }
                        }
                        recolusturmesajlar(mesajcilist)
                    }

                }
        )

    }

    private fun recolusturmesajlar(mesajcilar: ArrayList<abonedata>) {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this@Kulannici, Mesaj::class.java)
            intt.putExtra("uid", mesajcilar[position].abonezaman)
            startActivity(intt)
        }
        val adpt = abonereceler(mesajcilar, this, itemOnClick)
        Mesaj_Rec.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        Mesaj_Rec.adapter = adpt
    }

    private fun abonelericek() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val abonelist = ArrayList<abonedata>()
                        val puanlist = ArrayList<abonedata>()
                        for (uid in p0.children) {
                            if (uid.child("abonelik").exists() || uid.child("coin").exists()) {
                                try {
                                    val durum = uid.child("abonelik").getValue() as Boolean
                                    var puan = ""
                                    if (uid.child("coin").exists()) {
                                        puan = uid.child("coin").getValue() as String
                                    } else {
                                        uid.child("coin").ref.setValue("0")
                                        puan = "0"
                                    }
                                    val nick = uid.child("nick").getValue() as String
                                    val uidabone = uid.key
                                    if (durum) {
                                        val kullaniciabonelikzmn=uid.child("abonelikzaman").getValue(Date::class.java)!!
                                        val zaman = zamanhesapla(kullaniciabonelikzmn)
                                        if (zaman != "Bitir") {
                                            abonelist.add(abonedata(nick, zaman, uidabone!!))

                                            uid.child("reklamGun").ref.setValue(Zaman.toString("dd"))
                                        } else {
                                            uid.child("abonelik").ref.setValue(false)
                                        }

                                    }
                                    if (puan.toInt() > 100) {
                                        puanlist.add(abonedata(nick, puan, uidabone!!))
                                    }
                                } catch (e: Exception) {
                                    Log.e("HATAKULLANICI", uid.key)
                                }


                            }
                            recviewpuanolustur(puanlist)
                            recviewaboneolustur(abonelist)
                        }
                    }

                }
        )

    }

    private fun zamanhesapla(kullaniciabonelikzmn: Date): String {
        if (kullaniciabonelikzmn> Zaman){
            return kullaniciabonelikzmn.toString("dd / MM")
        }else{
           return "Bitir"
        }
    }

    private fun recviewpuanolustur(puanlist: ArrayList<abonedata>) {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this, KullaniciDuzenle::class.java)
            intt.putExtra("uid", puanlist[position].aboneuid)
            startActivity(intt)
        }
        val adpt = abonereceler(puanlist, this, itemOnClick)
        Puanlar_rec.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        Puanlar_rec.adapter = adpt
    }

    private fun recviewaboneolustur(abonelist: ArrayList<abonedata>) {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this, KullaniciDuzenle::class.java)
            intt.putExtra("uid", abonelist[position].aboneuid)
            startActivity(intt)
        }
        val adpt = abonereceler(abonelist, this, itemOnClick)
        abone_recw.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        abone_recw.adapter = adpt
    }

    private fun istatistikcek() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            var aktifkullanici = 0
                            val kullanicisayisi = p0.childrenCount.toString()
                            i_kullanicisayisi.setText(kullanicisayisi)
                            for (kullanici in p0.children) {
                                if (kullanici.child("reklamGun").exists()) {
                                    val gunk = kullanici.child("songorulmem").getValue(Date::class.java)!!
                                    if (Zaman.toString("dd") == gunk.toString("dd")) {
                                        aktifkullanici++
                                    }
                                }

                            }
                            i_gunlukkullanici.setText(aktifkullanici.toString())
                        }
                    }

                }
        )

    }

}
