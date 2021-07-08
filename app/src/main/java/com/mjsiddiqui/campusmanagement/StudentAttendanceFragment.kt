package com.mjsiddiqui.campusmanagement

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.timqi.sectorprogressview.ColorfulRingProgressView
import dmax.dialog.SpotsDialog

class StudentAttendanceFragment : Fragment() {
    private val mReference = FirebaseDatabase.getInstance().getReference("Login/Student")
    private val fReference = FirebaseDatabase.getInstance().getReference("Links")
    private lateinit var mAuth:FirebaseAuth
    private lateinit var pDialog:AlertDialog
    private lateinit var mLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_student_attendance, container, false)
        mLayout = view.findViewById(R.id.students_attendance_layout)
        pDialog = SpotsDialog.Builder().setContext(activity).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()
        mAuth = FirebaseAuth.getInstance()
        val currentUID = mAuth.uid

        mReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if(i.key.toString() == currentUID)
                    {
                        val output = i.value as Map<String, String>
                        val mYear = output.getValue("Year")
                        val mClass = output.getValue("Class")
                        val mRollNO = output.getValue("Roll No")
                        val mUserName = output.getValue("Name")
                        updateAttendance(mYear,mClass,mRollNO,mUserName)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }

    private fun updateAttendance(mYear: String, mClass: String, mRollNO: String, mUserName: String) {
        fReference.child("$mYear/$mClass/Attendance/Student/$mRollNO-$mUserName").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    val output = i.value as Map<String,Float>
                    val total = output.getValue("Total")
                    val attend = output.getValue("Attend")
                    val absent = total-attend
                    if(total != 0f)
                    {
                        val subject:String = i.key.toString()
                        val percent = ((attend/total)*100).toInt()
                        val mView = layoutInflater.inflate(R.layout.student_attendance_layout,null)
                        val progressView:ColorfulRingProgressView = mView.findViewById(R.id.percentage_bar)
                        progressView.percent = percent.toFloat()

                        val percentText:TextView = mView.findViewById(R.id.percentage_Text)
                        val subText:TextView = mView.findViewById(R.id.percentage_Sub)
                        val presentText:TextView = mView.findViewById(R.id.percentage_bar_presents)
                        val absentText:TextView = mView.findViewById(R.id.percentage_bar_absents)
                        val totalText:TextView = mView.findViewById(R.id.percentage_bar_total)
                        percentText.text = "$percent%"
                        subText.text = "$subject  ($mClass)"
                        presentText.text = attend.toString()
                        absentText.text = absent.toString()
                        totalText.text = total.toString()
                        mLayout.addView(mView)

                    }

                }
                pDialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}