package com.mjsiddiqui.campusmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class TeacherNotesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_notes, container, false)
        val tab: TabLayout = view.findViewById(R.id.notesTab)

        val first = tab.newTab()
        first.text = "Notes"
        tab.addTab(first,0)

        val second = tab.newTab()
        second.text = "History"
        tab.addTab(second,1,true)

        val obj = NotesHistoryFragment()
        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.notes_container,obj)?.commit()

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(t: TabLayout.Tab?) {
                when(t?.position)
                {
                    0->{
                        val obj2 = AddNotesFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.notes_container,obj2)?.commit()
                    }
                    1->{
                        val obj2 = NotesHistoryFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.notes_container,obj2)?.commit()
                    }
                }
            }
            override fun onTabUnselected(t: TabLayout.Tab?) {}
            override fun onTabReselected(t: TabLayout.Tab?) {}
        })


        return view
    }

}