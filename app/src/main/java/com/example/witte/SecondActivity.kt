package com.example.witte

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.witte.ParsePersonalAccountData.Lesson
import com.example.witte.databinding.ActivitySecondBinding
import com.example.witte.utils.ScheduleAdapter
import com.example.witte.utils.Utils

class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val lessons = Utils.loadLessonsFromJSON(this)
        println(lessons)
        setupRecyclerView(lessons)

    }
    private fun setupRecyclerView(lessons: List<Lesson>) {
        val adapter = ScheduleAdapter(lessons)
        binding.scheduleRecyclerView.adapter = adapter
    }
}
