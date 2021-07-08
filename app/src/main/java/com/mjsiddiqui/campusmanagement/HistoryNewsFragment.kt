package com.mjsiddiqui.campusmanagement

import Activity.NoticeActivity
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

class HistoryNewsFragment : Fragment() {
    private lateinit var mLayout: LinearLayout
    private lateinit var pDialog: AlertDialog
    private val mAuth = FirebaseAuth.getInstance()
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history_news, container, false)

        mLayout = view.findViewById(R.id.news_history_view)
        val currentUID = mAuth.uid
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
        fReference.child("$uid/Notice-History").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key.toString() != "ID")
                    {
                        val output = i.value as Map<String, String>
                        val mToken = output.getValue("Token").toString().toInt()
                        val mDate = output.getValue("Date")
                        val mTime = output.getValue("Time")
                        val title = output.getValue("Title")

                        val mView = layoutInflater.inflate(R.layout.history_layout, null)
                        val dateText: TextView = mView.findViewById(R.id.attendance_history_date)
                        val timeText: TextView = mView.findViewById(R.id.attendance_history_time)
                        val subText: TextView = mView.findViewById(R.id.attendance_history_sub)
                        val clickMe: ImageView = mView.findViewById(R.id.attendance_historyLogo)
                        dateText.text = mDate
                        subText.text = title
                        timeText.text = mTime
                        clickMe.setOnClickListener {
                            val intent = Intent(context,NoticeActivity::class.java)
                            intent.putExtra("Mj5",mToken.toString())
                            intent.putExtra("Mj4",i.key.toString())
                            startActivity(intent)
                        }

                        mLayout.addView(mView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}