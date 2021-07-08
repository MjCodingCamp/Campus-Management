package Adapter

import Activity.ChatActivity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import java.util.*

class ChatRoomAdapter(val mContext: ChatActivity, val messageList: MutableList<String>, val nameList: MutableList<String>, val typeList: MutableList<String>, val uidList: MutableList<String>, val timeList: MutableList<String>, val currentUserName: String, val recycleRv: RecyclerView) :RecyclerView.Adapter<ChatRoomAdapter.uHolder>()
{

    private val mAuth = FirebaseAuth.getInstance()
    private var mPos = -1

    override fun getItemViewType(position: Int): Int {
        val uid = mAuth.currentUser?.uid
        if (uid == uidList[position])
        {
            return 1
        }else
        {
            return 0
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomAdapter.uHolder {

        if (viewType == 1)
        {
            val layout = LayoutInflater.from(mContext).inflate(R.layout.chat_outgoing,parent,false)
            return uHolder(layout)
        }else
        {
            val layout = LayoutInflater.from(mContext).inflate(R.layout.chat_incoming,parent,false)
            return uHolder(layout)
        }
    }

    override fun onBindViewHolder(holder: ChatRoomAdapter.uHolder, position: Int) {
        val mReference = FirebaseDatabase.getInstance().getReference("Community/Messages")
        val timestamp = timeList[position].toLong()
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = timestamp
        val dateTime = android.text.format.DateFormat.format(" dd/MM/yyyy hh:mm aa", cal).toString()
        holder.dateTxt.text = dateTime
        holder.nameTxt.text = nameList[position]

        if (typeList[position] == "Image"){
            holder.messageTxt.visibility = View.GONE
            holder.chatImageView.visibility = View.VISIBLE
            Picasso.get().load(messageList[position]).into(holder.chatImageView)
            holder.chatImageView.setOnClickListener {
                val obj = Intent(Intent.ACTION_VIEW, Uri.parse(messageList[position]))
                mContext.startActivity(obj)
            }
        }else if(typeList[position] == "Text") {
            holder.messageTxt.visibility = View.VISIBLE
            holder.messageTxt.text = messageList[position]
            holder.chatImageView.visibility = View.GONE
        }else {
            holder.messageTxt.visibility = View.VISIBLE
            holder.messageTxt.text = messageList[position]
            holder.chatImageView.visibility = View.GONE
            holder.chatView.setOnClickListener {
                mReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var mPosition = -1
                        mPos = -1
                        for (i in snapshot.children)
                        {
                            mPosition++
                            if (typeList[position] == i.key.toString())
                            {
                                mPos = mPosition
                                break
                            }
                        }
                        if (mPos != -1)
                        {
                            recycleRv.scrollToPosition(mPos)
                            notifyItemChanged(mPos)
                        }else
                        {
                            Toast.makeText(mContext,"Message has been deleted",Toast.LENGTH_SHORT).show()
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }

        if (position == mPos)
        {
            holder.chatView.setBackgroundColor(Color.GRAY)
            val mHandler = android.os.Handler()
            val mRunnable = Runnable {
                val x = ContextCompat.getColor(mContext,R.color.colorChat)
                holder.chatView.setBackgroundColor(x)
            }
            mHandler.postDelayed(mRunnable,1000)
        }


        val uid = mAuth.currentUser?.uid
        if (uid != uidList[position])
        {
            holder.chatBox.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val popupMenu = PopupMenu(mContext,v)
                    popupMenu.inflate(R.menu.chat_option2)
                    popupMenu.setOnMenuItemClickListener {
                        when(it.itemId)
                        {
                            R.id.chat_copy->{
                                Toast.makeText(mContext,"Copied",Toast.LENGTH_SHORT).show()
                            }
                            R.id.chat_reply->{
                                chatReply(position)
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
            })
        }else
        {
            holder.chatBox.setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    val popupMenu = PopupMenu(mContext,v)
                    popupMenu.inflate(R.menu.chat_option)
                    popupMenu.setOnMenuItemClickListener {
                        when(it.itemId)
                        {
                            R.id.chat_copy->{
                                Toast.makeText(mContext,"Copied",Toast.LENGTH_SHORT).show()
                            }
                            R.id.chat_reply->{
                                chatReply(position)
                            }
                            R.id.chat_delete->{
                                mReference.child(timeList[position]).removeValue().addOnSuccessListener {
                                    Toast.makeText(mContext,"Delete Success",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        true
                    }
                    popupMenu.show()
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return nameList.size
    }

    class uHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val messageTxt: TextView = itemView.findViewById(R.id.messageTxt)
        val dateTxt: TextView = itemView.findViewById(R.id.timeTxt)
        val nameTxt:TextView = itemView.findViewById(R.id.nameTxt)
        val chatBox:ImageView = itemView.findViewById(R.id.chat_container)
        val chatView:FrameLayout = itemView.findViewById(R.id.chat_view)
        val chatImageView:ImageView = itemView.findViewById(R.id.chat_Image)
    }

    private fun chatReply(position: Int){
        val obj = AlertDialog.Builder(mContext)
        val mView = LayoutInflater.from(mContext).inflate(R.layout.chat_reply,null)
        val userTxt = mView.findViewById<TextView>(R.id.chat_userName)
        val userMsg = mView.findViewById<TextView>(R.id.chat_userMsg)
        val userAns = mView.findViewById<EditText>(R.id.chat_userAns)
        userTxt.text = nameList[position]
        userMsg.text = messageList[position]
        obj.setView(mView)
        obj.setCancelable(true)
        obj.setPositiveButton("Reply", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface?, which: Int) {
                if (userAns.text.isNotEmpty()){
                    val senderUid:String = mAuth.uid.toString()
                    val timeStamp: String = System.currentTimeMillis().toString()
                    val messageType = timeList[position]
                    val mDate = mapOf<String,String>("Message" to userAns.text.trim().toString(), "SenderID" to senderUid, "Timestamp" to timeStamp, "Type" to messageType, "Name" to "$currentUserName  (Reply)")
                    val ref = FirebaseDatabase.getInstance().getReference("Community/Messages")

                    ref.child(timeStamp).setValue(mDate).addOnSuccessListener {
                        userAns.text = null
                    }.addOnFailureListener {
                        Toast.makeText(mContext,"Check Internet",Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(mContext,"Write your ans",Toast.LENGTH_SHORT).show()
                    chatReply(position)
                }
            }
        })
        obj.setNegativeButton("Cancel") { dialog, which -> }
        obj.show()
    }
}