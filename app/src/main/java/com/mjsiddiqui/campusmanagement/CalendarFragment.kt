package com.mjsiddiqui.campusmanagement


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.skyhope.eventcalenderlibrary.CalenderEvent
import com.skyhope.eventcalenderlibrary.listener.CalenderDayClickListener
import com.skyhope.eventcalenderlibrary.model.DayContainerModel
import com.skyhope.eventcalenderlibrary.model.Event


class CalendarFragment: Fragment() {
    private val dReference = FirebaseDatabase.getInstance().getReference("Events")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_calendar, container, false)
        val calender = view.findViewById<CalenderEvent>(R.id.calender_event)
        val linearView1:LinearLayout = view.findViewById(R.id.holidays)
        val linearView2 = view.findViewById<LinearLayout>(R.id.exam)

        dReference.child("Holiday").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val output = i.getValue() as Map<String, Any>
                    val name:String = output.getValue("Name").toString()
                    val date:Long = output.getValue("Value").toString().toLong()
                    val eventDate = output.getValue("Date").toString()
                    val view2 = layoutInflater.inflate(R.layout.event_activity,null)
                    view2.findViewById<TextView>(R.id.event_name).text = name
                    view2.findViewById<TextView>(R.id.event_date).text = eventDate
                    linearView1.addView(view2)
                    val event = Event(date,name,Color.RED)
                    calender.addEvent(event)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        dReference.child("Exam").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val output = i.getValue() as Map<String, Any>
                    val name:String = output.getValue("Name").toString()
                    val date:Long = output.getValue("Value").toString().toLong()
                    val eventDate = output.getValue("Date").toString()
                    val view2 = layoutInflater.inflate(R.layout.event_activity,null)
                    view2.findViewById<TextView>(R.id.event_name).text = name
                    view2.findViewById<TextView>(R.id.event_date).text = eventDate
                    linearView2.addView(view2)
                    val event = Event(date,name,Color.RED)
                    calender.addEvent(event)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        calender.initCalderItemClickCallback(object : CalenderDayClickListener {
            override fun onGetDay(dayContainerModel: DayContainerModel?) {
                if (dayContainerModel != null) {
                    if (dayContainerModel.isHaveEvent){
                        Toast.makeText(activity,dayContainerModel.event.eventText,Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })

        return view
    }
}