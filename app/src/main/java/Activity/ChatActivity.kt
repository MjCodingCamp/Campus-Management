package Activity

import Adapter.ChatRoomAdapter
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import dmax.dialog.SpotsDialog
import java.util.*

class ChatActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var attachBtn: ImageButton
    private lateinit var sendBtn: ImageButton
    private lateinit var messageText: EditText
    private lateinit var recycleRv: RecyclerView
    private val messageList = mutableListOf<String>()
    private val nameList = mutableListOf<String>()
    private val uidList = mutableListOf<String>()
    private val timeList = mutableListOf<String>()
    private val typeList = mutableListOf<String>()
    private lateinit var pDialog:android.app.AlertDialog
    private var mData: Uri? = null
    private lateinit var ref:DatabaseReference
    private lateinit var userName:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        recycleRv = findViewById(R.id.recycle_Chat)
        attachBtn = findViewById(R.id.attachBtn)
        messageText = findViewById(R.id.msgText)
        sendBtn = findViewById(R.id.sendBtn)
        firebaseAuth = FirebaseAuth.getInstance()

        val layout = LinearLayoutManager(this@ChatActivity, LinearLayoutManager.VERTICAL, false)
        recycleRv.layoutManager = layout

        pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        userName = intent.getStringExtra("Mj5")

        ref = FirebaseDatabase.getInstance().getReference("Community/Messages")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                nameList.clear()
                typeList.clear()
                uidList.clear()
                timeList.clear()
                for (i in snapshot.children)
                {
                    val out = i.value as Map<String,String>
                    messageList.add(out.getValue("Message"))
                    nameList.add(out.getValue("Name"))
                    typeList.add(out.getValue("Type"))
                    uidList.add(out.getValue("SenderID"))
                    timeList.add(out.getValue("Timestamp"))

                }
                recycleRv.adapter = ChatRoomAdapter(this@ChatActivity,messageList,nameList,typeList,uidList,timeList,userName, recycleRv)
                recycleRv.scrollToPosition(messageList.size-1)
                pDialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        sendBtn.setOnClickListener {
            var message:String = messageText.text.trim().toString()
            val senderUid:String = firebaseAuth.uid.toString()
            val timeStamp: String = System.currentTimeMillis().toString()
            val messageType = "Text"
            if (message.isNotEmpty()) {
                val mDate = mapOf<String,String>("Message" to message.toString(), "SenderID" to senderUid, "Timestamp" to timeStamp, "Type" to messageType, "Name" to userName)
                ref.child(timeStamp).setValue(mDate).addOnSuccessListener {
                    messageText.text = null
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Check Internet",Toast.LENGTH_SHORT).show()
                }
            }
        }

        attachBtn.setOnClickListener {
            mData = null
            val obj = Intent()
            obj.setType("image/*")
            obj.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(Intent.createChooser(obj,"Please select Image"),92)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 92 && data != null && data.data != null)
        {
            mData = data.data
            pDialog.show()
            upload()
        }
    }

    private fun upload() {
        val storageReference = FirebaseStorage.getInstance()
        val mReference = storageReference.reference.child("Community").child(System.currentTimeMillis().toString() + ".jpg")
        mReference.putFile(mData!!).addOnSuccessListener {
            val x = it.storage.downloadUrl
            while (!x.isComplete);
            var uri = x.result

            val senderUid:String = firebaseAuth.uid.toString()
            val timeStamp: String = System.currentTimeMillis().toString()
            val messageType = "Image"
            if (uri != null) {
                val mDate = mapOf<String,String>("Message" to uri.toString(), "SenderID" to senderUid, "Timestamp" to timeStamp, "Type" to messageType, "Name" to userName)
                ref.child(timeStamp).setValue(mDate).addOnSuccessListener {
                    uri = null
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Check Internet",Toast.LENGTH_SHORT).show()
                }
            }

            pDialog.dismiss()
            Toast.makeText(applicationContext,"Uploaded",Toast.LENGTH_SHORT).show()
        }.addOnFailureListener{
            Toast.makeText(applicationContext,"Failed",Toast.LENGTH_SHORT).show()
            pDialog.dismiss()
        }.addOnProgressListener {
            val x = (100.0*it.bytesTransferred)/it.totalByteCount
            pDialog.setMessage("File is uploading...${x.toInt()}%")
        }
    }
}