package ofy.livegolyonetici

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.tek_mac.view.*

class macrecw(val maclar:ArrayList<macdata>,val Contex: Context) : androidx.recyclerview.widget.RecyclerView.Adapter<macrecw.macviewholder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): macviewholder {
        val intt = LayoutInflater.from(p0.context)
        val teksohbet = intt.inflate(R.layout.tek_mac, p0, false)
        val viewholder = macviewholder(teksohbet, p0)
        return viewholder

    }

    override fun getItemCount(): Int {
        return maclar.size
    }

    override fun onBindViewHolder(holder: macviewholder, position: Int) {
        holder.atama(position)
    }

    inner class macviewholder(val Viev: View, var ViewGroup: ViewGroup) : androidx.recyclerview.widget.RecyclerView.ViewHolder(Viev) {
        val teksatirmac = Viev as LinearLayout
        val macsaat = teksatirmac.Macsaat
        val macisim1 = teksatirmac.macisim1
        val macisim2 = teksatirmac.macisim2
        val macoran = teksatirmac.macoran
        val mactahmin = teksatirmac.mactahmin
        fun atama(position: Int) {
            macisim1.text = maclar[
                    position].macismi1
            macisim2.text = maclar[position].macismi2
            macoran.text = maclar[position].macoran
            mactahmin.text = maclar[position].mactahmin
            macsaat.text = maclar[position].macsaati

        }
    }


}