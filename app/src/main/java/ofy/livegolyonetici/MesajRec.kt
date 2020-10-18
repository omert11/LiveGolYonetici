package ofy.livegolyonetici

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_kupon.view.*
import kotlinx.android.synthetic.main.tek_mesaj.view.*

class MesajRec(val mesajlar: ArrayList<mesajdata>, val Contex: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<MesajRec.Kasaviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): Kasaviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_mesaj, p0, false)
        val viewholder = Kasaviewholder(teksohbet, p0)
        return viewholder

    }

    override fun getItemCount(): Int {
        return mesajlar.size
    }

    override fun onBindViewHolder(holder: Kasaviewholder, position: Int) {
        holder.atama(position)
    }

    inner class Kasaviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val tekmesaj=Viev as LinearLayout
        val gelenmesaj=tekmesaj.Gelenmesaj
        val gidenmesaj=tekmesaj.Gidenmesaj
        fun atama(position: Int) {
            if (mesajlar[position].uid=="Admin"){
                gidenmesaj.visibility=View.VISIBLE
                gelenmesaj.visibility=View.INVISIBLE
                gidenmesaj.setText(mesajlar[position].mesaj)
            }else{
                gidenmesaj.visibility=View.INVISIBLE
                gelenmesaj.visibility=View.VISIBLE
                gelenmesaj.setText(mesajlar[position].mesaj)
            }
        }

    }


}