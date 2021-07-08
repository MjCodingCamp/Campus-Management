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

class StudentMarksFragment : Fragment() {
    private val mReference = FirebaseDatabase.getInstance().getReference("Login/Student")
    private val fReference = FirebaseDatabase.getInstance().getReference("Links")
    private lateinit var mAuth: FirebaseAuth
    private lateinit var pDialog: AlertDialog
    private lateinit var mLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_student_marks, container, false)
        mLayout = view.findViewById(R.id.students_marks_layout)
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
                        updateMarks(mYear,mClass,mRollNO,mUserName)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }


    private fun updateMarks(mYear: String, mClass: String, mRollNO: String, mUserName: String) {
        fReference.child("$mYear/$mClass/Marks/Student/$mRollNO-$mUserName").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children)
                    {
                        if(i.key.toString() != "ID")
                        {
                            val output = i.value as Map<String,String>
                            val examName = output.getValue("Exam-Name")
                            val teacherName = output.getValue("Teacher")
                            val mDate = output.getValue("Date")
                            val mSubject = output.getValue("Subject")
                            val obtainMark = output.getValue("Obtain")
                            val totalMarks = output.getValue("Total")

                            val percentage = (obtainMark.toFloat()/ totalMarks.toFloat())*100.toInt()
                            val mView = layoutInflater.inflate(R.layout.student_marks_layout,null)
                            val progressView: ColorfulRingProgressView = mView.findViewById(R.id.marksBar)
                            progressView.percent = percentage.toFloat()

                            val examText:TextView = mView.findViewById(R.id.marks_name)
                            val examMarks:TextView = mView.findViewById(R.id.marks_Text)
                            val teacherText:TextView = mView.findViewById(R.id.evaluateMarks)
                            val examDateText:TextView = mView.findViewById(R.id.examMark_date)

                            examText.text = "$examName ($mSubject)"
                            examMarks.text = "$obtainMark/$totalMarks"
                            teacherText.text = "Evaluated By - $teacherName"
                            examDateText.text = "Exam Date - $mDate"

                            mLayout.addView(mView)
                        }

                    }
                    pDialog.dismiss()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
            })
    }
}