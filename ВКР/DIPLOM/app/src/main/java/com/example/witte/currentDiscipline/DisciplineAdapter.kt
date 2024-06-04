package com.example.witte.currentDiscipline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.witte.R

class DisciplineAdapter(private val disciplines: List<Discipline>) : RecyclerView.Adapter<DisciplineAdapter.DisciplineViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DisciplineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_discipline, parent, false)
        return DisciplineViewHolder(view)
    }

    override fun onBindViewHolder(holder: DisciplineViewHolder, position: Int) {
        val discipline = disciplines[position]
        holder.name.text = discipline.name
        holder.info.text = discipline.info
        holder.teacher.text = discipline.teacher
        holder.examType.text = discipline.examType
    }

    override fun getItemCount(): Int {
        return disciplines.size
    }

    class DisciplineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.disciplineName)
        val info: TextView = view.findViewById(R.id.disciplineInfo)
        val teacher: TextView = view.findViewById(R.id.disciplineTeacher)
        val examType: TextView = view.findViewById(R.id.disciplineExamType)
    }
}
