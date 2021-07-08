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
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import java.text.SimpleDateFormat
import java.util.*

class AddNewsFragment : Fragment() {
    private val databaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var storageReference: FirebaseStorage
    private lateinit var pDialog:AlertDialog
    private lateinit var mContext:Context
    private lateinit var mButton:Button
    private var mData: Uri? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_news, container, false)

        val btnClick: ImageView = view.findViewById(R.id.add_news)
        pDialog = SpotsDialog.Builder().setContext(activity).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("File is uploading...")
        mContext = activity!!

        storageReference = FirebaseStorage.getInstance()

        btnClick.setOnClickListener {
            uploadNews()
        }

        return view
    }

    private fun uploadNews() {
        mData = null
        val obj = AlertDialog.Builder(mContext)
        val mView = layoutInflater.inflate(R.layout.upload_news,null)
        val title:EditText = mView.findViewById(R.id.addNews_Title)
        val des:EditText = mView.findViewById(R.id.addNews_Des)
        mButton = mView.findViewById(R.id.btn_ImageLoad)
        mButton.setOnClickListener {
            loadImage()
        }
        obj.setView(mView)
        obj.setCancelable(false)
        obj.setPositiveButton("Upload", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val mTitle = title.text.trim().toString()
                val mDes = des.text.trim().toString()
                if (mTitle.isNotEmpty() && mDes.isNotEmpty() && mData != null) {
                    upload(mTitle,mDes)
                }else if(title.text.isEmpty()) {
                    Toast.makeText(mContext,"Enter Title",Toast.LENGTH_SHORT).show()
                    uploadNews()
                }else if(des.text.isEmpty()){
                    Toast.makeText(mContext,"Enter Description",Toast.LENGTH_SHORT).show()
                    uploadNews()
                }else{
                    Toast.makeText(mContext,"Please Select Image",Toast.LENGTH_SHORT).show()
                    uploadNews()
                }
            }
        })
        obj.setNegativeButton("Cancel") { dialog, which -> }
        obj.show()
    }


    private fun loadImage() {
        val obj = Intent()
        obj.setType("image/*")
        obj.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(obj,"Please select Image"),92)
    }


    private fun upload(title: String, description: String) {
        pDialog.show()
        val mReference = storageReference.reference.child("Notices").child(System.currentTimeMillis().toString() + ".jpg")
        mReference.putFile(mData!!).addOnSuccessListener {
            val x = it.storage.downloadUrl
            while (!x.isComplete);
            val uri:Uri? = x.result
            pDialog.dismiss()

            uploadHistory(uri.toString(),title,description)

            val obj = Intent(mContext, SuccessActivity::class.java)
            obj.putExtra("Mj5","Upload Complete")
            startActivity(obj)
        }.addOnProgressListener {
            val x = (100.0*it.bytesTransferred)/it.totalByteCount
            pDialog.setMessage("File is uploading...${x.toInt()}%")
        }.addOnFailureListener(){
            pDialog.dismiss()
            val obj = Intent(mContext, FailedActivity::class.java)
            obj.putExtra("Mj5","Upload Failed")
            startActivity(obj)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 92 && data != null && data.data != null)
        {
            mData = data.data
            mButton.text = "Image Selected"
        }
    }


    private fun uploadHistory(uri: String, title: String, description: String) {

        databaseReference.child("Notice/ID").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var keyID = snapshot.value.toString().toInt()
                keyID--
                val date = SimpleDateFormat("dd-MM-yyyy").format(Date())
                val time = SimpleDateFormat("HH:mm aa").format(Date())

                uploadData(keyID,uri,title,description,date,time)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

    }

    private fun uploadData(keyID: Int, uri: String, title: String, description: String, date: String, time: String) {
        val currentTime = "$date ($time)"
        val mAuth = FirebaseAuth.getInstance()
        val currentUid = mAuth.currentUser?.uid

        if (currentUid != null)
        {
            var sortTitle = ""
            if (title.length > 30)
            {
                sortTitle = title.subSequence(0, 30).toString() + "..."
            }
            else
            {
                sortTitle = title
            }
            val historyData = mapOf<String,String>("Date" to date, "Time" to time, "Token" to keyID.toString(), "Title" to sortTitle)

            databaseReference.child("Login/Teacher/$currentUid/Name").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val teacherName = snapshot.value.toString()
                    if (teacherName != null)
                    {
                        val noticeData = mapOf<String,String>("Content" to description,"Date" to currentTime, "Link" to uri, "Publisher" to teacherName, "Title" to title)

                        databaseReference.child("Notice/$keyID").setValue(noticeData)
                        databaseReference.child("Notice/ID").setValue(keyID)
                        databaseReference.child("Login/Teacher/$currentUid/Notice-History/ID").addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                var mID = snapshot.value.toString().toInt()
                                mID--
                                databaseReference.child("Login/Teacher/$currentUid/Notice-History").child(mID.toString()).setValue(historyData)
                                databaseReference.child("Login/Teacher/$currentUid/Notice-History/ID").setValue(mID)
                            }

                            override fun onCancelled(error: DatabaseError) {}
                        })

                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

}