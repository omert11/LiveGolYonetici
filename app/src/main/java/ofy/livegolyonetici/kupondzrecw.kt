package ofy.livegolyonetici

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_kupon.view.*
import kotlinx.android.synthetic.main.tek_mac.view.*

class kupondzrecw(val Kuponlar:ArrayList<Kupondata>, val Contex: Context,val itemClickListener: (View, Int, Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<kupondzrecw.macviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): macviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_kupon, p0, false)
        val viewholder = macviewholder(teksohbet, p0)
        viewholder.onClick(itemClickListener)
        return viewholder

    }

    override fun getItemCount(): Int {
        return Kuponlar.size
    }

    override fun onBindViewHolder(holder: macviewholder, position: Int) {
        holder.atama(position)
    }

    inner class macviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val teksatirkupon = Viev as LinearLayout
        val kuponisim = teksatirkupon.Kuponadi_tek
        val macsayisi = teksatirkupon.Macsayisi_tek

        fun atama(position: Int) {
            kuponisim.text=Kuponlar[position].Kuponismi
            macsayisi.text=Kuponlar[position].maclar!!.size.toString()
        }
    }

    fun <T : androidx.recyclerview.widget.RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(it, getAdapterPosition(), getItemViewType())
        }
        return this
    }
}