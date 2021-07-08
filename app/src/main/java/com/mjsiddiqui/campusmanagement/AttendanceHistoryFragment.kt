package com.mjsiddiqui.campusmanagement

import Activity.AttendanceDisplayActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog

class AttendanceHistoryFragment : Fragment() {
    private lateinit var mLayout: LinearLayout
    private lateinit var mAuth: FirebaseAuth
    private lateinit var pDialog: AlertDialog
    private val mUser = ArrayList<String>()
    private val userData = ArrayList<String>()
    private val mReference = FirebaseDatabase.getInstance().getReference("Links")
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attendance_history, container, false)
        mLayout = view.findViewById(R.id.attendance_history_view)
        mAuth = FirebaseAuth.getInstance()
        val cUser = mAuth.currentUser?.uid
        pDialog = SpotsDialog.Builder().setContext(context).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        fReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key.toString() == cUser)
                    {
                        updateHistory(cUser)
                    }
                }
                pDialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        return view
    }


    private fun updateHistory(cUser: String) {
        fReference.child("$cUser/Attendance-History").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    if (i.key.toString() != "ID") {
                        val data = i.value as Map<String, String>
                        val mDate: String = data.getValue("Date")
                        val mTime: String = data.getValue("Time")
                        val mSub: String = data.getValue("Subject")
                        val mClass: String = data.getValue("Class")
                        val mYear: String = data.getValue("Year")
                        val mID: String = data.getValue("SubID")
                        val mView = layoutInflater.inflate(R.layout.history_layout, null)
                        val dateText: TextView = mView.findViewById(R.id.attendance_history_date)
                        val timeText: TextView = mView.findViewById(R.id.attendance_history_time)
                        val subText: TextView = mView.findViewById(R.id.attendance_history_sub)
                        val clickMe: ImageView = mView.findViewById(R.id.attendance_historyLogo)
                        dateText.text = mDate
                        timeText.text = mTime
                        subText.text = "$mSub ($mClass)"
                        clickMe.setOnClickListener {
                            mUser.clear()
                            userData.clear()
                            pDialog.show()
                            mReference.child("$mYear/$mClass/Attendance/Date-wise/$mDate/$mID")
                                .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (i in snapshot.children)
                                            {
                                                mUser.add(i.key.toString())
                                                userData.add(i.value.toString())
                                            }
                                            val obj = Intent(activity, AttendanceDisplayActivity::class.java)
                                            obj.putExtra("Mj3",mUser)
                                            obj.putExtra("Mj4",userData)
                                            obj.putExtra("Mj5",mDate)
                                            obj.putExtra("Mj6",mTime)
                                            startActivity(obj)
                                            pDialog.dismiss()
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })
                        }
                        mLayout.addView(mView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}