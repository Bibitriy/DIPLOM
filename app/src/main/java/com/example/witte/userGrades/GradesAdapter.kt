package com.example.witte.userGrades

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.witte.R

class GradesAdapter(private val scheduleDisciplines: List<UserGradesModel>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<UserGradesModel> = scheduleDisciplines


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ItemViewHolder(inflater.inflate(R.layout.item_layout2, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        holder as ItemViewHolder
        holder.name.text = items[position].predmet
        holder.ball.text = items[position].ball
    }

    override fun getItemCount(): Int = items.size


    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.name)
        val ball: TextView = view.findViewById(R.id.ball)
    }
}
