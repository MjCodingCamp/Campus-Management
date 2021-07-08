package com.mjsiddiqui.campusmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class TeacherAssignmentFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_teacher_assignment, container, false)
        val tab: TabLayout = view.findViewById(R.id.assignmentTab)

        val first = tab.newTab()
        first.text = "Assignment"
        tab.addTab(first,0)

        val second = tab.newTab()
        second.text = "History"
        tab.addTab(second,1,true)

        val obj = AssignmentHistoryFragment()
        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.assignment_container,obj)?.commit()

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(t: TabLayout.Tab?) {
                when(t?.position)
                {
                    0->{
                        val obj2 = AddAssignmentFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.assignment_container,obj2)?.commit()
                    }
                    1->{
                        val obj2 = AssignmentHistoryFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.assignment_container,obj2)?.commit()
                    }
                }
            }
            override fun onTabUnselected(t: TabLayout.Tab?) {}
            override fun onTabReselected(t: TabLayout.Tab?) {}
        })

        return view
    }

}