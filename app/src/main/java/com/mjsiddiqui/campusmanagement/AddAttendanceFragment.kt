package com.mjsiddiqui.campusmanagement

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.*

class AddAttendanceFragment : Fragment() {
    private val year = arrayOf("1st-Year","2nd-Year","3rd-Year","4th-Year")
    private val branch1 = arrayOf("ST-CE","ST-CS","ST-EC","ST-ME")
    private val branch2 = arrayOf("TT-CE","TT-CS","TT-EC","TT-ME")
    private val branch3 = arrayOf("BT-CE","BT-CS","BT-EC","BT-ME")
    private var sYear:String? = null
    private var sBranch:String? = null
    private var sSubject:String? = null
    private lateinit var mBranch:Array<String>
    private val sub = mutableListOf<String>()
    private lateinit var mContext:Context
    private val mReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Links")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_attendance, container, false)
        mContext = activity!!
        val add:ImageView = view.findViewById(R.id.add_attendance)
        add.setOnClickListener {
            updateAttendance()
        }

        return view
    }


    private fun updateAttendance() {
        val obj = AlertDialog.Builder(mContext)
        val view2 = layoutInflater.inflate(R.layout.attendance,null)
        val spin1:Spinner = view2.findViewById(R.id.spin_year)
        val spin2:Spinner = view2.findViewById(R.id.spin_branch)
        val spin3:Spinner = view2.findViewById(R.id.spin_subject)

        spin1.adapter = ArrayAdapter(mContext,R.layout.login_mode,year)
        spin1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sYear = year[position]
                when(year[position])
                {
                    "1st-Year"->{
                    }
                    "2nd-Year"->{
                        mBranch = branch1
                        spin2.adapter = ArrayAdapter(mContext,R.layout.login_mode,branch1)
                    }
                    "3rd-Year"->{
                        mBranch = branch2
                        spin2.adapter = ArrayAdapter(mContext,R.layout.login_mode,branch2)
                    }
                    "4th-Year"->{
                        mBranch = branch3
                        spin2.adapter = ArrayAdapter(mContext,R.layout.login_mode,branch3)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spin2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sBranch = mBranch[position]
                sub.clear()
                try {
                    mReference.child("$sYear/$sBranch/Attendance/Subject").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (i in snapshot.children)
                            {
                                sub.add(i.key!!.toInt(),i.value.toString())
                            }
                            spin3.adapter = ArrayAdapter(mContext,R.layout.login_mode,sub)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(mContext,"Connection Error",Toast.LENGTH_SHORT).show()
                        }
                    })
                }catch (e:Exception){Toast.makeText(mContext,"Data Not Available",Toast.LENGTH_SHORT).show()}
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spin3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sSubject = sub[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        obj.setView(view2)

        obj.setPositiveButton("Proceed", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if(sSubject != null && sBranch != null)
                {
                    val obj2 = AttendanceFragment()
                    obj2.mData(sYear,sBranch,sSubject)
                    activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.attendance_container,obj2)?.commit()
                }else
                {
                    Toast.makeText(context,"Fill complete details before proceed",Toast.LENGTH_SHORT).show()
                    updateAttendance()
                }
            }
        })
        obj.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
            }
        })
        obj.setCancelable(false)
        obj.show()
    }
}