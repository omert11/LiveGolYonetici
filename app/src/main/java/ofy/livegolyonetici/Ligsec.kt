package ofy.livegolyonetici


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup




class Ligsec : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_ligsec, container, false)
        val recw=v.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.liglerrecwiew)
        val itemOnClick: (View, Int, Int) -> Unit = { view, position, type ->
            secilenlig= liglist[position]
            dismiss()
        }
        val adpt = ligsecrecw(liglist,itemOnClick,activity!!)
        recw.layoutManager = androidx.recyclerview.widget.GridLayoutManager(activity, 4)
        recw.adapter = adpt
        return v
    }



}
