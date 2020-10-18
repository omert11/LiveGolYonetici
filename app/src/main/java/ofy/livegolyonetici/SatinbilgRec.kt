package ofy.livegolyonetici

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_satinbilg.view.*

class SatinbilgRec(val bilg: ArrayList<satinbilgdata>, val Contex: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<SatinbilgRec.Kasaviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Kasaviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_satinbilg, p0, false)
        val viewholder = Kasaviewholder(teksohbet, p0)
        return viewholder

    }

    override fun getItemCount(): Int {
        return bilg.size
    }

    override fun onBindViewHolder(holder: Kasaviewholder, position: Int) {
        holder.atama(position)
    }

    inner class Kasaviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val tekbilg=Viev as LinearLayout
        val orderid=tekbilg.Orderid_tek
        val alinansey=tekbilg.alinansey_tek
        val gun=tekbilg.gun_tek
        val saat=tekbilg.zaman_tek
        fun atama(position: Int) {
         orderid.setText(bilg[position].orderid)
            alinansey.setText(bilg[position].alinansey)
            gun.setText(bilg[position].gun)
            saat.setText(bilg[position].zaman)
        }

    }


}