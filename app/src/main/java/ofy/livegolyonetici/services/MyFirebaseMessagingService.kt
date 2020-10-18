package ofy.livegolyonetici.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import khronos.Dates
import khronos.toString
import ofy.livegolyonetici.MainActivity
import ofy.livegolyonetici.R
import java.util.*


var TOKEN: String? = null
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage) {
        val bildirimbaslik = p0.notification?.title
        val bildirimBody = p0.notification?.body
        val baslik = p0.data.get("baslik")
        val mesaj = p0.data.get("mesaj")
        val saatguncelle=p0.data.get("saat")
        if (saatguncelle!=null){
            saatgunceller(saatguncelle)

        }else{
            bildirimgonder(baslik, mesaj)
        }
    }

    private fun saatgunceller(S:String) {
        if (S=="SaatVer"){
            val olusturulan=Date(Calendar.getInstance().getTimeInMillis())
            FirebaseDatabase.getInstance().reference.child("YeniZaman").setValue(olusturulan)
            Log.e("Saat","Saat Guncellendi:"+olusturulan.toString("MM/dd HH:mm:ss"))
        }
    }


    private fun bildirimgonder(baslik: String?, mesaj: String?) {
        val bildirimid = 1
        val kanal_id = "LivegolKanal"
        val name = getString(R.string.Kanaladi)
        val notificintt= Intent(this, MainActivity::class.java)
        val contentintetnt= PendingIntent.getActivity(this,0,notificintt,0)
        val MKey="Mesaj"
        val builder = NotificationCompat.Builder(this, kanal_id)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentTitle(baslik)
                .setContentText(mesaj)
                .setColor(resources.getColor(R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setStyle(NotificationCompat.BigTextStyle().bigText(mesaj))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
        val noti=builder.build()
        noti.contentIntent=contentintetnt
        val notimanager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val onemlilik = NotificationManager.IMPORTANCE_HIGH
            val benimkanalim = NotificationChannel(kanal_id, name, onemlilik)
            notimanager.createNotificationChannel(benimkanalim)
        }
        notimanager.notify(bildirimid, noti)
    }



}