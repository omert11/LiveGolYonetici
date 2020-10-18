package ofy.livegolyonetici

import android.content.Context
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_abone.view.*
import kotlinx.android.synthetic.main.tek_kupon.view.*
import kotlinx.android.synthetic.main.tek_mac.view.*

class puanreceler(val Aboneler:ArrayList<abonedata>, val Contex: Context, val itemClickListener: (View, Int, Int) -> Unit) : androidx.recyclerview.widget.RecyclerView.Adapter<puanreceler.macviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): macviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_abone, p0, false)
        val viewholder = macviewholder(teksohbet, p0)
        viewholder.onClick(itemClickListener)
        return viewholder

    }

    override fun getItemCount(): Int {
        return Aboneler.size
    }

    override fun onBindViewHolder(holder: macviewholder, position: Int) {
        holder.atama(position)
    }

    inner class macviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val tekabone = Viev as ConstraintLayout
        val aboneadi = tekabone.abonenick_tek
        val abonekalansure = tekabone.abonelikzmn_tek

        fun atama(position: Int) {
            aboneadi.setText(Aboneler[position].abonenick)
            abonekalansure.setText(Aboneler[position].abonezaman)
        }
    }
    fun <T : androidx.recyclerview.widget.RecyclerView.ViewHolder> T.onClick(event: (view: View, position: Int, type: Int) -> Unit): T {
        itemView.setOnClickListener {
            event.invoke(it, getAdapterPosition(), getItemViewType())
        }
        return this
    }

}