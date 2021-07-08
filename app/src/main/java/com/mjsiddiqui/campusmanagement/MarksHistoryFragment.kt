package com.mjsiddiqui.campusmanagement

import Activity.MarksDisplayActivity
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
import kotlin.collections.ArrayList

class MarksHistoryFragment : Fragment() {
    private lateinit var mLayout: LinearLayout
    private lateinit var pDialog: AlertDialog
    private val mUser = ArrayList<String>()
    private val userData = ArrayList<String>()
    private val mAuth = FirebaseAuth.getInstance()
    private val mReference = FirebaseDatabase.getInstance().getReference("Links")
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_marks_history, container, false)
        mLayout = view.findViewById(R.id.marks_history_view)
        val currentUID = mAuth.uid.toString()
        pDialog = SpotsDialog.Builder().setContext(context).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        fReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    val uid = i.key.toString()
                    if (currentUID == uid)
                    {
                        updateHistory(uid)
                    }
                }
                pDialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        return view
    }


    private fun updateHistory(uid: String) {
        fReference.child("$uid/Marks-History").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key.toString() != "ID")
                    {
                        val output = i.value as Map<String, String>
                        val mDate = output.getValue("Date")
                        val mYear = output.getValue("Year")
                        val mClass = output.getValue("Class")
                        val mSubID = output.getValue("SubID")
                        val mView = layoutInflater.inflate(R.layout.history_layout, null)
                        val dateText: TextView = mView.findViewById(R.id.attendance_history_date)
                        val timeText:TextView = mView.findViewById(R.id.attendance_history_time)
                        val subText: TextView = mView.findViewById(R.id.attendance_history_sub)
                        val clickMe: ImageView = mView.findViewById(R.id.attendance_historyLogo)
                        dateText.text = mDate
                        subText.text = mSubID
                        timeText.text = mClass

                        clickMe.setOnClickListener {
                            mUser.clear()
                            userData.clear()
                            pDialog.show()
                            mReference.child("$mYear/$mClass/Marks/Date-wise/$mDate/$mSubID").addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        for (i in snapshot.children)
                                        {
                                            mUser.add(i.key.toString())
                                            userData.add(i.value.toString())
                                        }
                                        val obj = Intent(activity, MarksDisplayActivity::class.java)
                                        obj.putExtra("Mj3",mDate)
                                        obj.putExtra("Mj4",mSubID)
                                        obj.putExtra("Mj5",mUser)
                                        obj.putExtra("Mj6",userData)
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