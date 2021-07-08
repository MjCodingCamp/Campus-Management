package com.mjsiddiqui.campusmanagement

import Activity.FailedActivity
import Activity.SuccessActivity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat
import java.util.*

class AddAssignmentFragment : Fragment() {
    private val year = arrayOf("1st-Year","2nd-Year","3rd-Year","4th-Year")
    private val branch1 = arrayOf("ST-CE","ST-CS","ST-EC","ST-ME")
    private val branch2 = arrayOf("TT-CE","TT-CS","TT-EC","TT-ME")
    private val branch3 = arrayOf("BT-CE","BT-CS","BT-EC","BT-ME")
    private lateinit var storageReference:FirebaseStorage
    private lateinit var databaseReference:FirebaseDatabase
    private lateinit var mButton:Button
    private lateinit var mEditText:EditText
    private lateinit var pDialog:AlertDialog
    private lateinit var mBranch:Array<String>
    private var mData:Uri? = null
    private var sYear:String? = null
    private var sBranch:String? = null
    private lateinit var mContext:Context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_assignment, container, false)
        val button:ImageView = view.findViewById(R.id.add_assignment)
        pDialog = SpotsDialog.Builder().setContext(activity).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("File is uploading...")
        mContext = activity!!

        storageReference = FirebaseStorage.getInstance()
        databaseReference = FirebaseDatabase.getInstance()

        button.setOnClickListener {
            uploadAssignment()
        }

        return view
    }


    private fun uploadAssignment() {
        sYear = null; sBranch = null; mData = null
        val obj = AlertDialog.Builder(mContext)
        val mView = layoutInflater.inflate(R.layout.upload_layout,null)
        val spin1:Spinner = mView.findViewById(R.id.spin_AssignYear)
        val spin2:Spinner = mView.findViewById(R.id.spin_AssignBranch)
        mEditText = mView.findViewById(R.id.assignment_name)
        mButton  = mView.findViewById(R.id.btn_PdfLoad)

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
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        mButton.setOnClickListener {
            chooseFile()
        }

        obj.setView(mView)
        obj.setCancelable(false)
        obj.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {}
        })
        obj.setPositiveButton("Upload", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                uploadFile()
            }
        })
        obj.show()
    }


    private fun chooseFile() {
        val obj = Intent()
        obj.setType("application/pdf")
        obj.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(obj,"Please select PDF file"),92)
    }


    private fun uploadFile() {
        pDialog.show()
        if (mEditText.text.isNotBlank() && mData != null && sBranch != null && sYear != null)
        {
            val mReference = storageReference.reference.child("Assignments/$sYear/$sBranch/"+"${mEditText.text}-(${System.currentTimeMillis()})"+".pdf")
            mReference.putFile(mData!!).addOnSuccessListener {
                val x = it.storage.downloadUrl
                while (!x.isComplete);
                val uri = x.result

                updateHistory(uri)

                pDialog.dismiss()
                val obj = Intent(mContext,SuccessActivity::class.java)
                obj.putExtra("Mj5","Upload Complete")
                startActivity(obj)
            }.addOnProgressListener {
                val x = (100.0*it.bytesTransferred)/it.totalByteCount
                pDialog.setMessage("File is uploading...${x.toInt()}%")
            }.addOnFailureListener(){
                pDialog.dismiss()
                val obj = Intent(mContext,FailedActivity::class.java)
                obj.putExtra("Mj5","Upload Failed")
                startActivity(obj)
            }
        }else
        {
            if(sBranch == null) {
                Toast.makeText(mContext,"Please Choose Branch",Toast.LENGTH_LONG).show()
                pDialog.dismiss()
                uploadAssignment()
            } else if(mEditText.text.isEmpty())
            {
                Toast.makeText(mContext,"Please Enter Assignment Name",Toast.LENGTH_LONG).show()
                pDialog.dismiss()
                uploadAssignment()
            } else
            {
                Toast.makeText(mContext,"Please Select PDF file",Toast.LENGTH_LONG).show()
                pDialog.dismiss()
                uploadAssignment()
            }
        }
    }

    private fun updateHistory(uri: Uri?) {
        val mAuth = FirebaseAuth.getInstance()
        val currentUID = mAuth.uid
        databaseReference.getReference("Login/Teacher").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children)
                    {
                        if (i.key == currentUID)
                        {
                            val output = i.value as Map<String, String>
                            val teacherName = output.getValue("Name")
                            updateData(currentUID,uri,teacherName)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun updateData(currentUID: String?, uri: Uri?, teacherName: String) {
        var uploadID = 0
        databaseReference.getReference("Login/Teacher").child("$currentUID/Assignment-History").addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children)
                    {
                        if (i.key == "ID")
                        {
                            uploadID = i.value.toString().toInt()
                            uploadID--
                            val x = SimpleDateFormat("dd-MM-yyyy")
                            val currentDate = x.format(Date())
                            val data = mapOf<String,String>("Branch" to sBranch.toString(), "Subject" to mEditText.text.toString(), "Date" to currentDate, "Link" to uri.toString(), "Teacher" to teacherName)
                            databaseReference.getReference("Login/Teacher").child("$currentUID/Assignment-History/ID").setValue(uploadID)
                            databaseReference.getReference("Login/Teacher").child("$currentUID/Assignment-History/$uploadID").setValue(data)
                            databaseReference.getReference("Links/$sYear/$sBranch/Assignment/ID").addListenerForSingleValueEvent(
                                object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var id = snapshot.value.toString().toInt()
                                        id--
                                        databaseReference.getReference("Links/$sYear/$sBranch/Assignment/ID").setValue(id)
                                        databaseReference.getReference("Links/$sYear/$sBranch/Assignment/$id").setValue(data)
                                    }
                                    override fun onCancelled(error: DatabaseError) {}
                                })
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 92 && data != null && data.data != null)
        {
            mData = data.data
            mButton.text = "File Selected"
        }
    }
}