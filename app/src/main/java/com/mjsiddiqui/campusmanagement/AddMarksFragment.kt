package com.mjsiddiqui.campusmanagement

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.database.*
import java.util.*

class AddMarksFragment : Fragment() {
    private val mReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Links")
    private val year = arrayOf("1st-Year","2nd-Year","3rd-Year","4th-Year")
    private val branch1 = arrayOf("ST-CE","ST-CS","ST-EC","ST-ME")
    private val branch2 = arrayOf("TT-CE","TT-CS","TT-EC","TT-ME")
    private val branch3 = arrayOf("BT-CE","BT-CS","BT-EC","BT-ME")
    private lateinit var mBranch:Array<String>
    private val sub = mutableListOf<String>()
    private var sYear:String? = null
    private var sBranch:String? = null
    private var sSubject:String? = null
    private var sDate:String? = null
    private var sExam:String? = null
    private var sMarks:String? = "20"
    private lateinit var mContext:Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_marks, container, false)
        val addMarks:ImageView = view.findViewById(R.id.add_marks)
        mContext = activity!!
        addMarks.setOnClickListener {
            sYear = null; sBranch = null; sSubject = null; sDate = null; sExam = null; sMarks = "20"
            addExamMarks()
        }
        return view
    }

    private fun addExamMarks() {
        val obj = AlertDialog.Builder(mContext)
        val view2 = layoutInflater.inflate(R.layout.marks_layout,null)
        val mDate:TextView = view2.findViewById(R.id.marks_date)
        val spin1:Spinner = view2.findViewById(R.id.spin_yearMarks)
        val spin2:Spinner = view2.findViewById(R.id.spin_branchMarks)
        val spin3:Spinner = view2.findViewById(R.id.spin_subjectMarks)

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
                mReference.child("$sYear/$sBranch/Marks/Subject").addListenerForSingleValueEvent(object : ValueEventListener {
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
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        spin3.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sSubject = sub[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val editTextMarks:EditText = view2.findViewById(R.id.marksEvaluated)
        editTextMarks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                sMarks = editTextMarks.text.toString()
            }
        })

        val editTextExam:EditText = view2.findViewById(R.id.editText_exam)
        editTextExam.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                sExam = editTextExam.text.toString()
            }
        })


        mDate.setOnClickListener {
            updateDate(mDate)
        }
        obj.setPositiveButton("Proceed") { dialog, which -> proceed()}
        obj.setNegativeButton("Cancel") { dialog, which -> }
        obj.setCancelable(false)
        obj.setView(view2)
        obj.show()
    }

    private fun proceed() {
        if(sDate != null && sYear != null && sBranch != null && sSubject != null && sExam != null  && sMarks != null)
        {
            val obj = UploadMarksFragment()
            obj.mDate(sDate!!, sYear!!, sBranch!!, sSubject!!, sExam!!, sMarks!!)
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.marks_container, obj)?.commit()
        }else
        {
            Toast.makeText(mContext,"Fill complete details before proceed", Toast.LENGTH_SHORT).show()
            addExamMarks()
        }
    }

    private fun updateDate(mDate: TextView) {
        val cal = Calendar.getInstance()
        val mYear = cal.get(Calendar.YEAR)
        val mMonth = cal.get(Calendar.MONTH)
        val mDay = cal.get(Calendar.DAY_OF_MONTH)
        val mObj = DatePickerDialog(mContext, object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                sDate = "$dayOfMonth-${month+1}-$year"
                mDate.text = sDate
            }

        },mYear,mMonth,mDay)
        mObj.setCancelable(false)
        mObj.show()
    }
}