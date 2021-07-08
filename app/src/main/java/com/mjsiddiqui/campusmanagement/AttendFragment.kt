package com.mjsiddiqui.campusmanagement

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayout

class AttendFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attend, container, false)
        val tab:TabLayout = view.findViewById(R.id.attendanceTab)

        val first = tab.newTab()
        first.text = "Attendance"
        tab.addTab(first,0)

        val second = tab.newTab()
        second.text = "History"
        tab.addTab(second,1, true)

        val obj = AttendanceHistoryFragment()
        activity?.supportFragmentManager?.beginTransaction()?.add(R.id.attendance_container,obj)?.commit()

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(t: TabLayout.Tab?) {
                when(t?.position)
                {
                    0->{
                        val obj = AddAttendanceFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.attendance_container,obj)?.commit()
                    }
                    1->{
                        val obj = AttendanceHistoryFragment()
                        activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.attendance_container,obj)?.commit()
                    }
                }
            }
            override fun onTabUnselected(t: TabLayout.Tab?) {}
            override fun onTabReselected(t: TabLayout.Tab?) {}
        })

        return view
    }
}