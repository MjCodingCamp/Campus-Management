package com.mjsiddiqui.campusmanagement

import Adapter.FirstAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SyllabusFragment : Fragment() {
    private val dReference = FirebaseDatabase.getInstance().getReference("Syllabus")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_syllabus, container, false)
        val recyclerView1 = view.findViewById<RecyclerView>(R.id.year1)
        val recyclerView2 = view.findViewById<RecyclerView>(R.id.year2)
        val recyclerView3 = view.findViewById<RecyclerView>(R.id.year3)
        val recyclerView4 = view.findViewById<RecyclerView>(R.id.year4)

        loadIcon("First_Year",recyclerView1)
        loadIcon("Second_Year",recyclerView2)
        loadIcon("Third_Year",recyclerView3)
        loadIcon("Fourth_Year",recyclerView4)
        return view
    }

    private fun loadIcon(s: String, recyclerView: RecyclerView) {
        val mYear = mutableListOf<String>()
        val mLink = mutableListOf<String>()

        dReference.child("Syllabus_Icon").child(s).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val output = i.value.toString()
                    mYear.add(output)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check your internet connection",Toast.LENGTH_SHORT).show()
            }
        })

        dReference.child("Syllabus_Link").child(s).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val out =  i.value.toString()
                    mLink.add(out)
                }
                val layout1 = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)
                recyclerView.layoutManager = layout1
                recyclerView.adapter = context?.let { FirstAdapter(it,mYear,mLink) }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}