package ofy.livegolyonetici

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.ParsedRequestListener
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import khronos.*
import kotlinx.android.synthetic.main.activity_main.*
import ofy.livegolyonetici.services.*
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import kotlin.collections.ArrayList

var secilenlig = ligdata(R.drawable.boslig, "Bos")
var liglist = ArrayList<ligdata>()
var referanslar = ArrayList<DatabaseReference>()
class MainActivity : AppCompatActivity() {
    val maclar = ArrayList<macdata>()
    val KuponduzList = ArrayList<Kupondata>()
    val Guncellekupon = ArrayList<Kupondata>()
    val Guncellekupon2 = ArrayList<Canlidata>()
    val tokenler = ArrayList<String>()
    val gorunmez = View.INVISIBLE
    val gorunur = View.VISIBLE
    var bildirimsayac = 1F
    var oran = 0F
    var bildirimstarter = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(getApplicationContext())
        setContentView(R.layout.activity_main)
        ilkacilis()
        liglistolustur()
        kupon_alnspinner.adapter = ArrayAdapter.createFromResource(this, R.array.Katagoriler, android.R.layout.simple_spinner_dropdown_item)
        kuponuyollabtn.setOnClickListener { kuponuyollabutonyplc() }
        maceklebtnn.setOnClickListener { macekleyapilacak() }
        ligsecbttn.setOnClickListener {
            val ligdialog = Ligsec()
            ligdialog.show(supportFragmentManager, "Lig")
        }
        tablaylistener()
        kuponduzenlechecklistener()
        bildirimyollarayarlari()
        kuponlaritemizlebtn()
        sentahminetayarlari()
        resimyukle()
       // internettenmaccekbakalim()

    }



    private fun resimyukle() {
        ekle_mac1log.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1) }
        ekle_mac2log.setOnClickListener{
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                bekle.visibility = gorunur
                val uri = data!!.getData() as Uri
                Picasso.get().load(uri).into(Mac1Log)
                FirebaseStorage.getInstance().getReference().child("Mac1.png").putFile(uri).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(p0: Task<UploadTask.TaskSnapshot>) {
                        bekle.visibility = gorunmez
                    }

                })

            } else if (requestCode == 2) {
                bekle.visibility = gorunur
                val uri = data!!.getData() as Uri
                Picasso.get().load(uri).into(Mac2Log)
                FirebaseStorage.getInstance().getReference().child("Mac2.png").putFile(uri).addOnCompleteListener {
                    if (it.isComplete) {
                        bekle.visibility = gorunmez
                    } else {
                        Toast.makeText(this, "Yuklemede hata", Toast.LENGTH_SHORT).show()
                    }
                }

            }

        }
    }


    private fun sentahminetayarlari() {
        yolla_ste.setOnClickListener {
            if (ev_ste.text.isEmpty() || deplas_ste.text.isEmpty() || senthminet_macsaati.text.isEmpty()) {
                Toast.makeText(this, "Ev ve Deplasman Takimlari veya Tarih Bos Birakilamaz", Toast.LENGTH_SHORT).show()
            } else {
                val mac1 = ev_ste.text.toString()
                val mac2 = dep_ste.text.toString()
                kontrolvemackayit(mac1, mac2)
            }
        }
        bloke_btn.setOnClickListener { FirebaseDatabase.getInstance().getReference("GununMaci").child("Bloke").setValue(true)  }
        acik_btn_tah.setOnClickListener { FirebaseDatabase.getInstance().getReference("GununMaci").child("Bloke").setValue(false)  }
        evkznd_ste.setOnClickListener {
            kontrolvemacguncel("tahmin1")
        }
        berabere_ste.setOnClickListener {
            kontrolvemacguncel("tahmin2")
        }
        deplas_ste.setOnClickListener {
            kontrolvemacguncel("tahmin3")
        }
    }

    private fun kontrolvemacguncel(Kazanan: String) {
        FirebaseDatabase.getInstance().getReference("GununMaci").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            kazanankaybedenbelirle(Kazanan)
                        } else {
                            Toast.makeText(this@MainActivity, "Suan Guncellenecek Bir Mac Yok", Toast.LENGTH_SHORT).show()
                        }
                    }

                }
        )
    }

    private fun kazanankaybedenbelirle(kazanan: String) {
        FirebaseDatabase.getInstance().getReference("GununMaci").child("GununTahminleri").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val kazananlist = ArrayList<String>()
                        val kaybedenlist = ArrayList<String>()
                        if (p0.exists()) {
                            if ("tahmin1" == kazanan) {
                                for (kisi in p0.child(kazanan).children) {
                                    kazananlist.add(kisi.getValue() as String)
                                }
                            } else {
                                for (kisi in p0.child("tahmin1").children) {
                                    kaybedenlist.add(kisi.getValue() as String)
                                }
                            }
                            if ("tahmin2" == kazanan) {
                                for (kisi in p0.child(kazanan).children) {
                                    kazananlist.add(kisi.getValue() as String)
                                }
                            } else {
                                for (kisi in p0.child("tahmin2").children) {
                                    kaybedenlist.add(kisi.getValue() as String)
                                }
                            }

                            if ("tahmin3" == kazanan) {
                                for (kisi in p0.child(kazanan).children) {
                                    kazananlist.add(kisi.getValue() as String)
                                }
                            } else {
                                for (kisi in p0.child("tahmin3").children) {
                                    kaybedenlist.add(kisi.getValue() as String)
                                }
                            }
                            oran = 100F / (kaybedenlist.size.toFloat() + kazananlist.size.toFloat())
                            bildirimyollakazanan(kazananlist)
                            bildirimyollakaybeden(kaybedenlist)
                            oylaritemizle()
                            FirebaseDatabase.getInstance().getReference().child("GununMaci").removeValue()
                        }
                    }

                }
        )
    }

    private fun bildirimyollakaybeden(kaybedenlist: ArrayList<String>) {
        for (uid in kaybedenlist) {
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).child("token").addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val token = p0.getValue() as String
                            bildirimyollasade("Tutmadı", "Malesef Bu Gun Yaptığınız Tahmin Tutmadı", token)
                        }

                    }
            )
        }
    }

    private fun bildirimyollakazanan(kazananlist: ArrayList<String>) {
        for (uid in kazananlist) {
            FirebaseDatabase.getInstance().getReference().child("Kullanicilar").child(uid).addListenerForSingleValueEvent(
                    object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val token = p0.child("token").getValue() as String
                            bildirimyollasade("Tebrikler", "Tebrikler Bu Gun Yaptığınız Tahmin Tuttu,Puanınız aktarıldı.", token)
                            if (p0.child("coin").exists()) {
                                val coinsuan = p0.child("coin").getValue() as String
                                val degisececoin = coinsuan.toInt() + 5
                                p0.child("coin").ref.setValue(degisececoin.toString())
                            } else {
                                p0.child("coin").ref.setValue("0")
                            }
                        }

                    }
            )
        }
    }


    private fun kontrolvemackayit(mac1: String, mac2: String) {
        FirebaseDatabase.getInstance().getReference().child("GununMaci")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            p0.ref.removeValue().addOnCompleteListener {
                                if (it.isComplete) {
                                    p0.child("mac1").ref.setValue(mac1)
                                    p0.child("mac2").ref.setValue(mac2)
                                    p0.child("Bloke").ref.setValue(false)
                                    p0.child("Zaman").ref.setValue(senthminet_macsaati.text.toString())
                                    p0.child("tahmin1").ref.setValue("0")
                                    p0.child("tahmin2").ref.setValue("0")
                                    p0.child("tahmin3").ref.setValue("0")
                                    bildirimyollasentahminet()
                                    oylaritemizle()
                                }
                            }
                        } else {
                            p0.child("mac1").ref.setValue(mac1)
                            p0.child("mac2").ref.setValue(mac2)
                            p0.child("Bloke").ref.setValue(false)
                            p0.child("Zaman").ref.setValue(senthminet_macsaati.text.toString())
                            p0.child("tahmin1").ref.setValue("0")
                            p0.child("tahmin2").ref.setValue("0")
                            p0.child("tahmin3").ref.setValue("0")
                            bildirimyollasentahminet()
                            oylaritemizle()
                        }
                    }
                })
    }

    private fun oylaritemizle() {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (uid in p0.children) {
                            if (uid.child("oydurum").exists()) {
                                uid.child("oydurum").child("durum").ref.setValue(false)
                            }
                        }
                    }

                }
        )
    }

    private fun bildirimyollasentahminet() {
        herkesebildirimyolla("Kupon Eklendi", "Sizin Tahmin Etmeniz için bir kupon eklendi.")
    }

    private fun ilkacilis() {
        ilkaciliskontrol("BankoKup")
        ilkaciliskontrol("Basketbol")
        ilkaciliskontrol("KasaKup")
        ilkaciliskontrol("Supriz")
        ilkaciliskontrol("Canli")
    }

    private fun ilkaciliskontrol(Uzanti: String) {
        val ref = FirebaseDatabase.getInstance().getReference().child("KuponlarV")
        ref.child(Uzanti).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    for (kupon in p0.children) {
                        if (Uzanti != "Canli") {
                            val k = kupon.getValue(Kupondata::class.java)
                            if (k!!.Kuponismi == null) {
                                kupon.child("kuponismi").ref.setValue("Kupon")
                            } else if (k.Kupondurumu == null) {
                                kupon.child("kupondurumu").ref.setValue("Maç Devam Ediyor")
                            } else if (k.maclar == null) {
                                kupon.ref.removeValue()
                                tekrardiz(Uzanti)
                            }
                        } else {
                            val k = kupon.getValue(Canlidata::class.java)
                            if (k!!.Kuponismi == null) {
                                kupon.child("kuponismi").ref.setValue("Kupon")
                            } else if (k.Kupondurumu == null) {
                                kupon.child("kupondurumu").ref.setValue("Maç Devam Ediyor")
                            } else if (k.Kupondali == null) {
                                kupon.child("kupondali").ref.setValue("Futbol")
                            } else if (k.maclar == null) {
                                kupon.ref.removeValue()
                                tekrardiz2(Uzanti)
                            }
                        }
                    }
                }
            }

        })
    }

    private fun tekrardiz2(uzanti: String) {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Guncellekupon2.clear()
                        if (p0.exists()) {
                            for (kupon in p0.children) {
                                val kupodt = kupon.getValue(Canlidata::class.java)
                                Guncellekupon2.add(kupodt!!)
                            }
                            FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).setValue(Guncellekupon2)
                        }
                    }

                }
        )
    }

    private fun tekrardiz(uzanti: String) {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        Guncellekupon.clear()
                        if (p0.exists()) {
                            for (kupon in p0.children) {
                                val kupodt = kupon.getValue(Kupondata::class.java)
                                Guncellekupon.add(kupodt!!)
                            }
                            FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).setValue(Guncellekupon)
                        }
                    }

                }
        )
    }

    private fun machatayakalayici(ref: DatabaseReference, katagori: String) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    when (katagori) {
                        "Canli" -> {
                            try {
                                val a = p0.child("kupondurumu").getValue() as String
                                val e = p0.child("kupondali").getValue() as String
                                val b = p0.child("kuponismi").getValue() as String
                                val c = p0.child("maclar").child("0").child("macoran").getValue() as String
                                val d = p0.child("maclar").child("0").child("maculke").getValue() as String
                            } catch (e: Exception) {
                                ref.removeValue()
                                Toast.makeText(this@MainActivity, "Hata Yakalandi Ve Silindi Hata Kodu :" + e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                        else -> {
                            try {
                                val a = p0.child("kupondurumu").getValue() as String
                                val b = p0.child("kuponismi").getValue() as String
                                val c = p0.child("maclar").child("0").child("macoran").getValue() as String
                                val d = p0.child("maclar").child("0").child("maculke").getValue() as String
                            } catch (e: Exception) {
                                ref.removeValue()
                                Toast.makeText(this@MainActivity, "Hata Yakalandi Ve Silindi Hata Kodu :" + e.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }

        })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        ligresim.setImageDrawable(resources.getDrawable(secilenlig.LigRes!!))
        if (kupon_alnspinner.selectedItem == "Canli") {
            canli_secenek.visibility = View.VISIBLE
            kuponadi_et.visibility = View.GONE
        } else if (kupon_alnspinner.selectedItem == "Katagori Seç") {
            canli_secenek.visibility = View.GONE
            kuponadi_et.visibility = View.GONE
        } else {
            canli_secenek.visibility = View.GONE
            kuponadi_et.visibility = View.VISIBLE
        }
    }

    private fun kuponlaritemizlebtn() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("Live Gol")
        builder.setMessage("Bütün kuponlar silincek eminmisiniz?")
        builder.setPositiveButton("Sil", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                katagorikontrolvekuponsil()
            }

        })
        builder.setNegativeButton("İptal", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                Toast.makeText(this@MainActivity, "Kuponlar Silinmedi iptal ettiniz", Toast.LENGTH_SHORT).show()
            }

        })
        hepsinisil_btn.setOnClickListener {
            builder.show()
        }
    }

    private fun katagorikontrolvekuponsil() {
        when (RadioGroups.checkedRadioButtonId) {
            r_kasa.id -> {
                kuponlaritemizle("KasaKup")
            }
            r_basketbol.id -> {
                kuponlaritemizle("Basketbol")
            }
            r_banko.id -> {
                kuponlaritemizle("BankoKup")
            }
            r_supriz.id -> {
                kuponlaritemizle("Supriz")
            }
            r_canli.id -> {
                kuponlaritemizle("Canli")
            }
            r_ozel.id -> {
                FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").removeValue()
            }
            else -> {
                Toast.makeText(this@MainActivity, "Bir Kupon Grubu Seçiniz", Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun istetistikcekkupdz(uzanti: String) {
        var kazanan = 0
        var kaybeden = 0
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            for (kupon in p0.children) {
                                val kupondurum = kupon.child("kupondurumu").getValue() as String
                                if (kupondurum == "Kazandi") {
                                    kazanan++
                                } else if (kupondurum == "Kaybetti") {
                                    kaybeden++
                                }
                            }
                            kazanan_kup.setText(kazanan.toString())
                            kaybeden_kup.setText(kaybeden.toString())
                        }
                    }

                }
        )
    }

    private fun kuponlaritemizle(uzanti: String) {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).removeValue()
        Toast.makeText(this@MainActivity, "Kuponlar Silindi", Toast.LENGTH_SHORT).show()
        var uz=""
        when(uzanti){
            "Basketbol"->{uz="Basket"}
            "Supriz"->{uz="Supriz"}
            "BankoKup"->{uz="Banko"}
        }
        FirebaseDatabase.getInstance().reference.child("Yorumlar").child(uz).removeValue()
    }




    private fun bildirimyollarayarlari() {
        bl_gonder.setOnClickListener {
            if (bl_baslik.text.isEmpty()) {
                Toast.makeText(this@MainActivity, "Başlık alanı boş bırakılamaz.", Toast.LENGTH_SHORT).show()
            } else if (bl_mesaj.text.isEmpty()) {
                Toast.makeText(this@MainActivity, "Mesaj alanı boş bırakılamaz.", Toast.LENGTH_SHORT).show()
            } else {
                val baslik = bl_baslik.text.toString()
                val mesaj = bl_mesaj.text.toString()
                herkesebildirimyolla(baslik, mesaj)
                Toast.makeText(this@MainActivity, "Bildirim Yollandi", Toast.LENGTH_SHORT).show()
                bl_baslik.setText("")
                bl_mesaj.setText("")
            }
        }
    }

    private fun kuponduzenlechecklistener() {
        RadioGroups.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                r_kasa.id -> {
                    kuponcekKd("KasaKup")
                    istetistikcekkupdz("KasaKup")

                }
                r_basketbol.id -> {
                    kuponcekKd("Basketbol")
                    istetistikcekkupdz("Basketbol")

                }
                r_banko.id -> {
                    kuponcekKd("BankoKup")
                    istetistikcekkupdz("BankoKup")

                }
                r_supriz.id -> {
                    kuponcekKd("Supriz")
                    istetistikcekkupdz("Supriz")

                }
                r_canli.id -> {
                    kuponcekKd("Canli")
                    istetistikcekkupdz("Canli")

                }
                r_ozel.id -> {
                    kuponcekzel()
                }
            }
        }
    }

    private fun kuponcekzel() {
        FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").child("Kupon").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val kupondata = p0.getValue(Kupondata::class.java)
                            val list = ArrayList<Kupondata>()
                            list.add(kupondata!!)
                            recolusturozel(list)
                        }
                    }

                }
        )
    }

    private fun kuponcekKd(uzanti: String) {
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(uzanti).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        referanslar.clear()
                        KuponduzList.clear()
                        if (p0.exists()) {
                            for (kupon in p0.children) {
                                val kupodt = kupon.getValue(Kupondata::class.java)
                                KuponduzList.add(kupodt!!)
                                val refar = kupon.ref
                                referanslar.add(refar)
                            }
                            recolustrKd(uzanti)
                        } else {
                            recolustrKd(uzanti)
                        }
                    }

                }
        )
    }

    private fun recolusturozel(list: ArrayList<Kupondata>) {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this@MainActivity, Ozelduzenle::class.java)
            startActivity(intt)
        }
        val adpt = kupondzrecw(list, this, itemOnClick)
        KD_Recw.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        KD_Recw.adapter = adpt
    }

    private fun recolustrKd(uzanti: String) {
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            val intt = Intent(this@MainActivity, KuponAyrinti::class.java)
            intt.putExtra("position", position)
            intt.putExtra("uzanti", uzanti)
            startActivity(intt)
        }
        val adpt = kupondzrecw(KuponduzList, this, itemOnClick)
        KD_Recw.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        KD_Recw.adapter = adpt
    }

    private fun tablaylistener() {
        Tablayyy.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                when (p0!!.position) {
                    0 -> {
                        supportActionBar!!.title="Kupon Yolla"
                        l_kuponekle.visibility = gorunur
                        l_bildirimyolla.visibility = gorunmez
                        l_macduzenle.visibility = gorunmez
                        l_sentahminet.visibility = gorunmez

                    }
                    1 -> {
                        supportActionBar!!.title="Kupon Duzenle"
                        l_kuponekle.visibility = gorunmez
                        l_bildirimyolla.visibility = gorunmez
                        l_macduzenle.visibility = gorunur
                        l_sentahminet.visibility = gorunmez


                    }
                    2 -> {
                        supportActionBar!!.title="Bildirim Yolla"
                        l_kuponekle.visibility = gorunmez
                        l_bildirimyolla.visibility = gorunur
                        l_macduzenle.visibility = gorunmez
                        l_sentahminet.visibility = gorunmez


                    }
                    3 -> {
                        supportActionBar!!.title="Gunun Macini Yolla"
                        l_kuponekle.visibility = gorunmez
                        l_bildirimyolla.visibility = gorunmez
                        l_macduzenle.visibility = gorunmez
                        l_sentahminet.visibility = gorunur
                    }


                }
            }

        })
    }





    private fun macekleyapilacak() {
        if (ev_et.text.isEmpty() || dep_et.text.isEmpty() || saat_et.text.isEmpty() || tahmin_et.text.isEmpty()) {
            Toast.makeText(this, "Alanlar Bos Birakilamaz", Toast.LENGTH_SHORT).show()
        } else if (secilenlig.LigIsim == "Bos") {
            Toast.makeText(this, "Bir Lig Secmediniz", Toast.LENGTH_SHORT).show()
        } else {
            val taraf1 = ev_et.text.toString()
            val taraf2 = dep_et.text.toString()
            val saat = saat_et.text.toString()
            val tahmin = tahmin_et.text.toString()
            val oran = oran_et.text.toString()
            val ulke = secilenlig.LigIsim!!
            val eklenecekdata = macdata(taraf1, taraf2, saat, oran, tahmin, ulke)
            maclar.add(eklenecekdata)
            recviewolustur()
            ev_et.setText("")
            dep_et.setText("")
            saat_et.setText("")
            tahmin_et.setText("")
            oran_et.setText("")
            secilenlig = ligdata(R.drawable.boslig, "Bos")
            ligresim.setImageDrawable(resources.getDrawable(R.drawable.boslig))
            macsayisi.text = "Mac " + (maclar.size + 1).toString()
        }
    }

    private fun recviewolustur() {

        val adpt = macrecw(maclar, this)
        Maclarrec.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.VERTICAL, false)
        Maclarrec.adapter = adpt
    }

    private fun kuponuyollabutonyplc() {
        if (maclar.isEmpty()) {
            Toast.makeText(this, "Hic Mac Eklemediniz", Toast.LENGTH_SHORT).show()
        } else if (kupon_alnspinner.selectedItem as String == "Ozel") {
            if (kuponadi_et.text.isNotEmpty()) {
                FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").child("KuponKodu").setValue(kuponadi_et.text.toString().hashCode().toString()+ Zaman.toString("dd"))
                FirebaseDatabase.getInstance().getReference().child("Ozelmaclar").child("Kupon").setValue(Kupondata(kuponadi_et.text.toString(), "Başlamadı", maclar))
                Toast.makeText(this, "Kupon Yollandi", Toast.LENGTH_SHORT).show()
                maclar.clear()
                recviewolustur()


            }
        } else {
            val kuponadi = kuponadi_et.text.toString()
            val int = kupon_alnspinner.selectedItem as String
            bildirimstarter = true
            bildirimyollaniyor(0F)
            when (int) {
                "Kasa Kuponu" -> {
                    if (kuponadi_et.text.isNotEmpty()) {
                        kuponyollamadanoncesaysicek("KasaKup", kuponadi, "Kasa Kuponu")
                    } else {
                        Toast.makeText(this@MainActivity, "Kupon Adi Giriniz", Toast.LENGTH_SHORT).show()
                    }

                }
                "Basketbol" -> {
                    if (kuponadi_et.text.isNotEmpty()) {
                        kuponyollamadanoncesaysicek("Basketbol", kuponadi, "Basketbol")
                    } else {
                        Toast.makeText(this@MainActivity, "Kupon Adi Giriniz", Toast.LENGTH_SHORT).show()
                    }

                }
                "Banko Kuponlar" -> {
                    if (kuponadi_et.text.isNotEmpty()) {
                        kuponyollamadanoncesaysicek("BankoKup", kuponadi, "Banko Kupon")
                    } else {
                        Toast.makeText(this@MainActivity, "Kupon Adi Giriniz", Toast.LENGTH_SHORT).show()
                    }

                }
                "Canli" -> {

                    if (radioButton.isChecked || radioButton2.isChecked) {
                        if (radioButton.isChecked) {
                            kuponyollamadanoncesaysicek("Canli", maclar[0].macismi1 + "-" + maclar[0].macismi2, "Canlı", "Futbol")

                        } else {
                            kuponyollamadanoncesaysicek("Canli", maclar[0].macismi1 + "-" + maclar[0].macismi2, "Canlı", "Basketbol")

                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Canli Mac Hangi Spor Dalinda Seciniz", Toast.LENGTH_SHORT).show()
                    }

                }
                "Supriz" -> {
                    if (kuponadi_et.text.isNotEmpty()) {
                        kuponyollamadanoncesaysicek("Supriz", kuponadi, "Süpriz")
                    } else {
                        Toast.makeText(this@MainActivity, "Kupon Adi Giriniz", Toast.LENGTH_SHORT).show()
                    }

                }
                else -> {
                    Toast.makeText(this@MainActivity, "Katagori Seçmediniz", Toast.LENGTH_SHORT).show()
                }
            }

        }

    }

    private fun kuponyollamadanoncesaysicek(katagori: String, kuponadi: String, katagori2: String) {
        val yazilacakdata = Kupondata(kuponadi, "Başlamadı", maclar)
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(katagori).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val sayi = p0.childrenCount.toString()
                            kaydet(sayi, yazilacakdata, katagori, katagori2)
                        } else {
                            val sayi = "0"
                            kaydet(sayi, yazilacakdata, katagori, katagori2)
                        }
                    }

                }
        )
    }

    private fun kuponyollamadanoncesaysicek(katagori: String, kuponadi: String, katagori2: String, spordali: String) {

        val yazilacakdata = Canlidata(kuponadi, "Başlamadı", maclar, spordali)
        FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(katagori).addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            val sayi = p0.childrenCount.toString()
                            kaydet(sayi, yazilacakdata, katagori, katagori2)
                        } else {
                            val sayi = "0"
                            kaydet(sayi, yazilacakdata, katagori, katagori2)
                        }
                    }

                }
        )
    }

    private fun kaydet(sayi: String, yazilacakdata: Kupondata, katagori: String, katagori2: String) {

        val ref = FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(katagori).child(sayi)
        ref.setValue(yazilacakdata).addOnCompleteListener {
            if (it.isComplete) {
                maclar.clear()
                recviewolustur()
                machatayakalayici(ref, katagori)
                herkesebildirimyolla(yazilacakdata.Kuponismi!!, "Bir Kupon Eklendi", katagori2)

            }
        }


    }


    private fun kaydet(sayi: String, yazilacakdata: Canlidata, katagori: String, katagori2: String) {
        val ref = FirebaseDatabase.getInstance().getReference().child("KuponlarV").child(katagori).child(sayi)
        ref.setValue(yazilacakdata).addOnCompleteListener {
            if (it.isComplete) {
                maclar.clear()
                recviewolustur()
                machatayakalayici(ref, katagori)
                herkesebildirimyolla(yazilacakdata.Kuponismi!!, "Canli maç eklendi", katagori2)
            }
        }


    }

    private fun herkesebildirimyolla(baslik: String, mesaj: String) {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            for (kullanici in p0.children) {
                                if (kullanici.child("token").exists() && kullanici.child("reklamGun").exists()) {
                                    val songorulme=kullanici.child("songorulmem").getValue(Date::class.java)
                                        if (songorulme!!>Dates.today-10.day) {
                                            val token = kullanici.child("token").getValue() as String
                                            tokenler.add(token)
                                        }else{
                                            val abonelik=kullanici.child("abonelik").getValue() as Boolean
                                            if (!abonelik){
                                                kullanici.ref.removeValue()
                                            }
                                        }
                                    }

                                }

                            }
                            oran = 100F / tokenler.size.toFloat()
                            bildirimyolla(baslik, mesaj)
                        }
                    }


        )
    }

    private fun herkesebildirimyolla(baslik: String, mesaj: String, katagori: String) {
        FirebaseDatabase.getInstance().getReference().child("Kullanicilar").addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0.exists()) {
                            for (kullanici in p0.children) {
                                if (kullanici.child("token").exists() && kullanici.child("reklamGun").exists()) {
                                    val songorulme=kullanici.child("songorulmem").getValue(Date::class.java)
                                    if (songorulme!!>Dates.today-10.day) {
                                        val token = kullanici.child("token").getValue() as String
                                        tokenler.add(token)
                                    }else{
                                        val abonelik=kullanici.child("abonelik").getValue() as Boolean
                                        if (!abonelik){
                                            kullanici.ref.removeValue()
                                        }
                                    }

                                }

                            }
                            oran = 100F / tokenler.size.toFloat()
                            bildirimyolla(baslik, mesaj, katagori)
                        }
                    }

                }
        )
    }

    private fun bildirimyolla(baslik: String, mesaj: String) {
        for (token in tokenler) {
            val headerd = HashMap<String, String>()
            headerd.put("Content-Type", "application/json")
            headerd.put("Authorization", "key=" + serverkey)
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
                    bildirimsayac = bildirimsayac + oran
                    bildirimyollaniyor(bildirimsayac)

                }
            })

        }
        tokenler.clear()

    }

    private fun bildirimyollasade(baslik: String, mesaj: String, token: String) {
        val headerd = HashMap<String, String>()
        headerd.put("Content-Type", "application/json")
        headerd.put("Authorization", "key=" + serverkey)
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
                bildirimsayac = bildirimsayac + oran
                bildirimyollaniyor(bildirimsayac)
            }
        })


    }

    private fun bildirimyolla(baslik: String, mesaj: String, katagori: String) {
        for (token in tokenler) {
            val headerd = HashMap<String, String>()
            headerd.put("Content-Type", "application/json")
            headerd.put("Authorization", "key=" + serverkey)
            val data = fcmmodel.Data(mesaj, baslik, katagori)
            val bildirim = fcmmodel(data, token)
            val etrofit = Retrofit.Builder().baseUrl(baseurl)
                    .addConverterFactory(GsonConverterFactory.create()).build()
            val int = etrofit.create(fcminterface::class.java)
            val istek = int.bildirimgonder(headerd, bildirim)
            istek.enqueue(object : Callback<Response<fcmmodel>> {
                override fun onFailure(call: Call<Response<fcmmodel>>?, t: Throwable?) {

                }

                override fun onResponse(call: Call<Response<fcmmodel>>?, response: Response<Response<fcmmodel>>?) {
                    bildirimsayac = bildirimsayac + oran
                    bildirimyollaniyor(bildirimsayac)
                }
            })
        }
        tokenler.clear()

    }

    private fun bildirimyollaniyor(Durum: Float) {
        if (bildirimstarter) {
            if (Durum < 99F) {
                bildirimbekleme.visibility = View.VISIBLE
                progressBar2.setProgress(Durum.toInt())

            } else {
                Toast.makeText(this, "Kupon Yollandi", Toast.LENGTH_SHORT).show()
                bildirimsayac = 1F
                oran = 0F
                bildirimstarter = false
                bildirimbekleme.visibility = View.INVISIBLE
            }
        }

    }
   /* private fun internettenmaccekbakalim() {
        val headerd = HashMap<String, String>()
        headerd.put("X-RapidAPI-Key","55571e88f4mshb3aeeddb4d6e204p10d752jsn1e8e1518b1fa")
        headerd.put("Accept","application/json")
        AndroidNetworking.get("https://api-football-v1.p.rapidapi.com/v2/leagues")
                .addHeaders(headerd)
                .setTag(this)
                .setPriority(Priority.LOW)
                .build()
               /* .getAsJSONArray(object :JSONArrayRequestListener{
                    override fun onResponse(response: JSONArray) {
                        Log.e("Res",response.toString())
                        for (ligs in response){

                        }

                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Leg",anError!!.localizedMessage)

                    }

                })*/
                .getAsObject(apisoccer::class.java,object :ParsedRequestListener<apisoccer>{
                    override fun onResponse(response: apisoccer) {
                        Log.e("Leg",response.api!!.results.toString())
                        for (ligs in response.api!!.leagues!!){
                            Log.e("Leg",ligs.name)

                        }
                    }

                    override fun onError(anError: ANError?) {
                        Log.e("Leg",anError!!.localizedMessage)
                    }

                })
        Log.e("Leg","Girdi")

         val headerd = HashMap<String, String>()
         headerd.put("Content-Type", "application/json")
         headerd.put("Authorization", "key=" + serverkey)
         val retrofi=Retrofit.Builder()
                 .baseUrl(" https://api-football-v1.p.rapidapi.com/v2/")
                 .addConverterFactory(GsonConverterFactory.create())
                 .build()
         val apisocr=retrofi.create(soccerintface::class.java)
         val callpost:Call<List<soccerlivemodel>>
         callpost=apisocr.cek()
         callpost.enqueue(object :Callback<List<soccerlivemodel>>{
             override fun onFailure(call: Call<List<soccerlivemodel>>, t: Throwable) {
                 Log.e("respons",t.localizedMessage)
             }

             override fun onResponse(call: Call<List<soccerlivemodel>>, response: Response<List<soccerlivemodel>>) {
                 if (response.isSuccessful){
                     Log.e("Mac",response.body()!!.get(0).name)
                 }else{

                 }
             }

         })
    }*/

    private fun liglistolustur() {
        liglist.add(ligdata(R.drawable.boslig, "Diger"))
        liglist.add(ligdata(R.drawable.championsleague, "Sampiyonlar Ligi"))
        liglist.add(ligdata(R.drawable.uefaeuropaleague, "Uefa"))
        liglist.add(ligdata(R.drawable.euroleague, "Euro Lig"))
        liglist.add(ligdata(R.drawable.turkiye, "Turkiye"))
        liglist.add(ligdata(R.drawable.almanya, "Almanya"))
        liglist.add(ligdata(R.drawable.amerika, "Amerika"))
        liglist.add(ligdata(R.drawable.arjantin, "Arjantin"))
        liglist.add(ligdata(R.drawable.arnavutluk, "Arnavutluk"))
        liglist.add(ligdata(R.drawable.avusturalya, "Avusturalya"))
        liglist.add(ligdata(R.drawable.avusturya, "Avusturya"))
        liglist.add(ligdata(R.drawable.azerbaycan, "Azerbaycan"))
        liglist.add(ligdata(R.drawable.belarus, "Belarus"))
        liglist.add(ligdata(R.drawable.belcika, "Belcika"))
        liglist.add(ligdata(R.drawable.bolivya, "Bolivya"))
        liglist.add(ligdata(R.drawable.brezilya, "Brezilya"))
        liglist.add(ligdata(R.drawable.cek, "Cek Cum"))
        liglist.add(ligdata(R.drawable.cezayir, "Cezayir"))
        liglist.add(ligdata(R.drawable.cin, "Cin Cum"))
        liglist.add(ligdata(R.drawable.danimarka, "Danimarka"))
        liglist.add(ligdata(R.drawable.endonezya, "Endonezya"))
        liglist.add(ligdata(R.drawable.estonya, "Estonya"))
        liglist.add(ligdata(R.drawable.fas, "Fas"))
        liglist.add(ligdata(R.drawable.finlandiya, "Finlandiya"))
        liglist.add(ligdata(R.drawable.fransa, "Fransa"))
        liglist.add(ligdata(R.drawable.galler, "Galler"))
        liglist.add(ligdata(R.drawable.hindistan, "Hindistan"))
        liglist.add(ligdata(R.drawable.hirvatistan, "Hirvatistan"))
        liglist.add(ligdata(R.drawable.hollanda, "Hollanda"))
        liglist.add(ligdata(R.drawable.honkong, "Honkong"))
        liglist.add(ligdata(R.drawable.ingiltere, "Ingiltere"))
        liglist.add(ligdata(R.drawable.iran, "Iran"))
        liglist.add(ligdata(R.drawable.iskocya, "Iskocya"))
        liglist.add(ligdata(R.drawable.ispanya, "Ispanya"))
        liglist.add(ligdata(R.drawable.israil, "Israil"))
        liglist.add(ligdata(R.drawable.isvec, "Isvicre"))
        liglist.add(ligdata(R.drawable.italya, "Italya"))
        liglist.add(ligdata(R.drawable.izlanda, "Izlanda"))
        liglist.add(ligdata(R.drawable.japonya, "Japonya"))
        liglist.add(ligdata(R.drawable.katar, "Katar"))
        liglist.add(ligdata(R.drawable.kazakistan, "Kazakistan"))
        liglist.add(ligdata(R.drawable.kibris, "Kibris"))
        liglist.add(ligdata(R.drawable.kolombiya, "Kolombiya"))
        liglist.add(ligdata(R.drawable.kore, "Kore"))
        liglist.add(ligdata(R.drawable.kuzeyirlanda, "Kuzeyirlanda"))
        liglist.add(ligdata(R.drawable.litvanya, "Litvanya"))
        liglist.add(ligdata(R.drawable.macaristan, "Macaristan"))
        liglist.add(ligdata(R.drawable.makedonya, "Makedonya"))
        liglist.add(ligdata(R.drawable.meksika, "Meksika"))
        liglist.add(ligdata(R.drawable.norvec, "Norvec"))
        liglist.add(ligdata(R.drawable.ozbekistan, "Ozbekistan"))
        liglist.add(ligdata(R.drawable.paraguay, "Paraguay"))
        liglist.add(ligdata(R.drawable.peru, "Peru"))
        liglist.add(ligdata(R.drawable.polonya, "Polonya"))
        liglist.add(ligdata(R.drawable.portekiz, "Portekiz"))
        liglist.add(ligdata(R.drawable.romanya, "Romanya"))
        liglist.add(ligdata(R.drawable.rusya, "Rusya"))
        liglist.add(ligdata(R.drawable.sili, "Sili"))
        liglist.add(ligdata(R.drawable.sirbistan, "Sirbistan"))
        liglist.add(ligdata(R.drawable.slovakya, "Slovakya"))
        liglist.add(ligdata(R.drawable.suudiarabistan, "Arabistan"))
        liglist.add(ligdata(R.drawable.tunus, "Tunus"))
        liglist.add(ligdata(R.drawable.ukrayna, "Ukrayna"))
        liglist.add(ligdata(R.drawable.uruguay, "Uruguay"))
        liglist.add(ligdata(R.drawable.venezuella, "Venezuella"))
        liglist.add(ligdata(R.drawable.yunanistan, "Yunanistan"))

    }
}
