package com.example.witte.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.witte.ParsePersonalAccountData.Lesson
import com.example.witte.R
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

class ScheduleAdapter(private val lessons: List<Lesson>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<Any> = prepareData(lessons)

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is LocalDate -> TYPE_HEADER
            is Lesson -> TYPE_ITEM
            else -> throw IllegalArgumentException("Unknown type of item at position $position")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_HEADER -> HeaderViewHolder(inflater.inflate(R.layout.header_layout, parent, false))
            TYPE_ITEM -> ItemViewHolder(inflater.inflate(R.layout.item_layout, parent, false))
            else -> throw IllegalArgumentException("Unknown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is LocalDate -> (holder as HeaderViewHolder).headerTitle.text = item.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            is Lesson -> {
                holder as ItemViewHolder
                holder.time.text = "${item.startTime} - ${item.endTime}"
                holder.name.text = item.name
                holder.roomAndType.text = "${item.type}, ауд. ${item.room}"
                holder.teacher.text = item.teacher
            }
        }
    }

    override fun getItemCount(): Int = items.size

    private fun prepareData(lessons: List<Lesson>): List<Any> {
        val sortedLessons = lessons.sortedWith(compareBy<Lesson> { it.date }.thenBy { it.startTime })
        val groupedLessons = sortedLessons.groupBy { it.date }

        val result = mutableListOf<Any>()
        groupedLessons.forEach { (date, lessons) ->
            result.add(date)
            result.addAll(lessons)
        }
        return result
    }

    companion object {
        const val TYPE_HEADER = 0
        const val TYPE_ITEM = 1
    }

    class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val headerTitle: TextView = view.findViewById(R.id.headerTitle)
    }
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val time: TextView = view.findViewById(R.id.Time)
        val name: TextView = view.findViewById(R.id.name)
        val roomAndType: TextView = view.findViewById(R.id.roomAndType)
        val teacher: TextView = view.findViewById(R.id.teacher)
    }
}
