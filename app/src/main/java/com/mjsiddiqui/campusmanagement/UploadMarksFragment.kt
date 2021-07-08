package com.mjsiddiqui.campusmanagement

import Activity.FailedActivity
import Activity.SuccessActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog

class UploadMarksFragment : Fragment() {
    private var totalStudents = 0
    private lateinit var mYear:String
    private lateinit var mDate:String
    private lateinit var mBranch:String
    private lateinit var mSubject:String
    private lateinit var mExam:String
    private lateinit var mMarks:String
    private lateinit var pDialog:AlertDialog
    private val mMarksData = mutableMapOf<String,String>()
    private val mUserData = mutableMapOf<String,String>()
    private val mAuth = FirebaseAuth.getInstance()
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")
    private val mReference = FirebaseDatabase.getInstance().getReference("Links")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_upload_marks, container, false)
        val mLayout:LinearLayout = view.findViewById(R.id.marks_view)
        val textView1:TextView = view.findViewById(R.id.exam_Name)
        val textView2:TextView = view.findViewById(R.id.exam_date)
        val marksUpload:Button = view.findViewById(R.id.marks_upload)
        textView1.text = mExam
        textView2.text = mDate

        pDialog = SpotsDialog.Builder().setContext(context).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        mReference.child("$mYear/$mBranch/Marks/Student").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalStudents = 0
                for (i in snapshot.children)
                {
                    totalStudents++
                    val mView = layoutInflater.inflate(R.layout.marks_user,null)
                    val edit1:EditText = mView.findViewById(R.id.numObtain)
                    val textView:TextView = mView.findViewById(R.id.numTotal)
                    val userName:TextView = mView.findViewById(R.id.marksUser)
                    val mUser = i.key.toString()
                    userName.text = mUser
                    textView.text = mMarks
                    uploadMarks(mUser,edit1)
                    mLayout.addView(mView)
                }
                pDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Internet Connection",Toast.LENGTH_SHORT).show()
            }
        })

        marksUpload.setOnClickListener {
            if (mUserData.size == totalStudents)
            {
                pDialog.show()
                uploadData()
                mReference.child("$mYear/$mBranch/Marks/Date-wise/$mDate").child("$mExam ($mSubject)").setValue(mUserData).addOnSuccessListener {
                    pDialog.dismiss()
                    val obj = Intent(activity, SuccessActivity::class.java)
                    obj.putExtra("Mj5","Upload Complete")
                    activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
                    startActivity(obj)
                }.addOnFailureListener {
                    pDialog.dismiss()
                    val obj = Intent(context, FailedActivity::class.java)
                    obj.putExtra("Mj5","Upload Failed")
                    startActivity(obj)
                }
            }
            else{
                Toast.makeText(activity,"Some Students are Left",Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }


    private fun uploadData() {
        val currentUID = mAuth.uid.toString()
        fReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children) {
                    val uid = i.key.toString()
                    if (uid == currentUID) {
                        val out = i.value as Map<String, String>
                        val teacherName = out.getValue("Name")
                        uploadStudentData(teacherName)
                        upload(uid)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Check Internet Connection", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun upload(uid: String) {
        fReference.child(uid).child("Marks-History").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key == "ID")
                    {
                        var id = i.value.toString().toInt()
                        id--
                        val data = mutableMapOf<String,String>("Year" to mYear, "Class" to mBranch, "Date" to mDate, "SubID" to "$mExam ($mSubject)", "Subject" to mSubject)
                        fReference.child(uid).child("Marks-History").child("$id").setValue(data)
                        fReference.child(uid).child("Marks-History").child("ID").setValue(id)
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity,"Check Internet Connection",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadMarks(mUser: String, edit1: EditText) {
        edit1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val obtain = edit1.text.toString()
                if (obtain <= mMarks)
                {
                    mUserData.put(mUser,"$obtain/$mMarks")
                    mMarksData.put(mUser,obtain)
                }
                if (obtain > mMarks)
                {
                    Toast.makeText(activity,"Enter Valid Marks", Toast.LENGTH_SHORT).show()
                    edit1.text.clear()
                }
            }
        })
    }

    private fun uploadStudentData(teacherName: String) {
        val mRunnable = Runnable {
            for (i in mMarksData)
            {
                val data = mutableMapOf<String,String>("Date" to mDate, "Exam-Name" to mExam, "Subject" to mSubject, "Obtain" to i.value, "Total" to mMarks, "Teacher" to teacherName)
                var mID:Int = 0
                mReference.child("$mYear/$mBranch/Marks/Student").child(i.key).child("ID").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        mID = snapshot.value.toString().toInt()
                        mID--
                        mReference.child("$mYear/$mBranch/Marks/Student").child(i.key).child(mID.toString()).setValue(data)
                        mReference.child("$mYear/$mBranch/Marks/Student").child(i.key).child("ID").setValue(mID)
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
        val thread = Thread(mRunnable)
        thread.start()
    }


    fun mDate(sDate: String, sYear: String, sBranch: String, sSubject: String, sExam: String, sMarks: String) {
        mDate = sDate; mYear = sYear; mBranch = sBranch; mSubject = sSubject; mExam = sExam; mMarks = sMarks
    }

}