package com.mjsiddiqui.campusmanagement

import Activity.FailedActivity
import Activity.SuccessActivity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat
import java.util.*

class AttendanceFragment : Fragment() {
    private var totalStudents = 0
    private var mYear:String? = null
    private var mBranch:String? = null
    private var mSubject:String? = null
    private lateinit var currentTime: String
    private lateinit var currentDate: String
    private val userList = mutableMapOf<String,String>()
    private val userList1 = mutableMapOf<String,Int>()
    private val userList2 = mutableMapOf<String,Int>()
    private lateinit var mAuth: FirebaseAuth
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")
    private val mReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Links")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_attendance, container, false)
        val mLayout = view.findViewById<LinearLayout>(R.id.attendance_view)
        val textView:TextView = view.findViewById(R.id.attendance_date)
        val uploadButton:Button = view.findViewById(R.id.attendance_upload)
        val x = SimpleDateFormat("dd:MM:yyyy")
        val f = SimpleDateFormat("HH:mm aa")
        currentTime = f.format(Date())
        currentDate = x.format(Date())
        textView.text = currentDate
        val pDialog = SpotsDialog.Builder().setContext(context).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()
        mAuth = FirebaseAuth.getInstance()

        uploadButton.setOnClickListener {
            if (userList.size == totalStudents)
            {
                pDialog.show()
                mReference.child("$mYear/$mBranch/Attendance/Date-wise/$currentDate/$mSubject-$currentTime").setValue(userList)
                    .addOnSuccessListener {
                        pDialog.dismiss()
                        val obj = Intent(context, SuccessActivity::class.java)
                        obj.putExtra("Mj5","Upload Complete")
                        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                        startActivity(obj)
                    }
                    .addOnFailureListener{
                        pDialog.dismiss()
                        val obj = Intent(context, FailedActivity::class.java)
                        obj.putExtra("Mj5","Upload Failed")
                        startActivity(obj)
                    }
                history()
                studentAttendance()
            }else{
                Toast.makeText(activity,"Some Students are Left",Toast.LENGTH_SHORT).show()
            }
        }

        mReference.child("$mYear/$mBranch/Attendance/Student").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    totalStudents = 0
                    for (i in snapshot.children) {
                        totalStudents++
                        val userName = i.key.toString()
                        val mView = LayoutInflater.from(context).inflate(R.layout.attendance_user, null)
                        val mRadioGroup:RadioGroup =  mView.findViewById(R.id.attendClass)
                        uploadAttendance(mRadioGroup,userName)
                        val name: TextView = mView.findViewById(R.id.attendUser)
                        name.text = userName
                        mLayout.addView(mView)
                    }
                    pDialog.dismiss()
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        return view
    }

    private fun studentAttendance() {
        val mRunnable = Runnable {
            for (i in userList1)
            {
                val key = i.key
                val attend = userList1[key]
                val total = userList2[key]
                val data = mapOf<String,Int>("Attend" to attend!!.toInt(), "Total" to total!!.toInt())
                mReference.child("$mYear/$mBranch/Attendance/Student").child("$key/$mSubject").setValue(data)
            }
        }
        val mThread = Thread(mRunnable)
        mThread.start()
    }


    private fun history(){
        val userId = mAuth.currentUser?.uid
        fReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    val uid = i.key.toString()
                    if(uid == userId)
                    {
                       uploadHistory(userId)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadHistory(userId: String) {
        var uploadID = 0
        fReference.child(userId).child("Attendance-History").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if(i.key.toString() == "ID")
                    {
                        uploadID = i.value.toString().toInt()
                        uploadID--
                        val historyDate = mutableMapOf<String,String>("SubID" to "$mSubject-$currentTime", "Date" to "$currentDate", "Class" to "$mBranch","Time" to "$currentTime", "Subject" to "$mSubject", "Year" to "$mYear")
                        fReference.child(userId).child("Attendance-History").child("ID").setValue("$uploadID")
                        fReference.child(userId).child("Attendance-History").child(uploadID.toString()).setValue(historyDate)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun uploadAttendance(mRadioGroup: RadioGroup, user:String) {
        mRadioGroup.setOnCheckedChangeListener(object : RadioGroup.OnCheckedChangeListener {
            override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
                var total:Int = 0
                var attend:Int = 0
                mReference.child("$mYear/$mBranch/Attendance/Student/$user/$mSubject").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children)
                        {
                            if (i.key.toString() == "Attend")
                            {
                                attend = i.value.toString().toInt()
                            }else if(i.key.toString() == "Total")
                            {
                                total = i.value.toString().toInt()
                            }
                        }
                        upload(checkedId,total,attend,user)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(context,"Connection Error",Toast.LENGTH_SHORT).show()
                    }
                })
            }
        })
    }

    private fun upload(checkedId: Int, t: Int, a: Int, user: String) {
        var total = t
        var attend = a
        when(checkedId)
        {
            R.id.present->{
                userList.put(user,"Present")
                total++
                attend++
                userList1.put(user,attend)
                userList2.put(user,total)
            }
            R.id.absent->{
                userList.put(user,"Absent")
                total++
                userList1.put(user,attend)
                userList2.put(user,total)
            }
        }
    }


    fun mData(Year: String?, Branch: String?, Subject: String?) {
        mYear = Year
        mBranch = Branch
        mSubject = Subject
    }
}