package com.mjsiddiqui.campusmanagement

import Adapter.HistoryAdapter
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dmax.dialog.SpotsDialog


class NotesHistoryFragment : Fragment() {
    private lateinit var mContext:Context
    private lateinit var pDialog: AlertDialog
    private lateinit var recyclerView:RecyclerView
    private val mAuth = FirebaseAuth.getInstance()
    private val fReference = FirebaseDatabase.getInstance().getReference("Login/Teacher")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes_history, container, false)
        recyclerView = view.findViewById(R.id.notes_history)
        mContext = activity!!

        pDialog = SpotsDialog.Builder().setContext(mContext).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        val currentUID = mAuth.uid
        if (currentUID != null)
        {
            fReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children)
                    {
                        val uid = i.key.toString()
                        if (currentUID == uid)
                        {
                            updateNotes(uid)
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
        }
        return view
    }

    private fun updateNotes(uid: String) {
        val dateList = mutableListOf<String>()
        val nameList = mutableListOf<String>()
        val linkList = mutableListOf<String>()
        val branchList = mutableListOf<String>()

        fReference.child("$uid/Notes-History").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key != "ID")
                    {
                        val output = i.value as Map<String, String>
                        val name = output.getValue("Subject")
                        val date = output.getValue("Date")
                        val link = output.getValue("Link")
                        val branch = output.getValue("Branch")
                        dateList.add(date)
                        nameList.add(name)
                        linkList.add(link)
                        branchList.add(branch)
                    }
                    val layout = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                    recyclerView.layoutManager = layout
                    recyclerView.adapter = HistoryAdapter(mContext,dateList,linkList,nameList,branchList)
                    pDialog.dismiss()
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}