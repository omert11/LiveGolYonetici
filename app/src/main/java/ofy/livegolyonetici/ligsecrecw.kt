package ofy.livegolyonetici

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_ulke.view.*

class ligsecrecw(val ligler:ArrayList<ligdata>, val itemClickListener: (View, Int, Int) -> Unit,val Contx : FragmentActivity) : androidx.recyclerview.widget.RecyclerView.Adapter<ligsecrecw.macviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): macviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_ulke, p0, false)
        val viewholder = macviewholder(teksohbet, p0)
        viewholder.onClick(itemClickListener)
        return viewholder

    }

    override fun getItemCount(): Int {
        return ligler.size
    }

    override fun onBindViewHolder(holder: macviewholder, position: Int) {
        holder.atama(position)
    }

    inner class macviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val teksatirkupon = Viev as LinearLayout
        val ligisim=teksatirkupon.ligisim_t
        val ligresim=teksatirkupon.ligres_t
        fun atama(position: Int) {
            ligisim.setText(ligler[position].LigIsim!!)
            ligresim.setImageDrawable(Contx.resources.getDrawable(ligler[position].LigRes!!))
        }
    }

    fun <T : androidx.recyclerview.widget.RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(it, getAdapterPosition(), getItemViewType())
        }
        return this
    }
}