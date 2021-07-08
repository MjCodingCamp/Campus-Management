package com.mjsiddiqui.campusmanagement

import Adapter.SecondAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AssignmentFragment : Fragment() {
    private var mDate = mutableListOf<String>()
    private var link = mutableListOf<String>()
    private var name = mutableListOf<String>()
    private var subject = mutableListOf<String>()
    private val lReference = FirebaseDatabase.getInstance().getReference("Links")
    private val mAuth = FirebaseAuth.getInstance()
    private val dReference = FirebaseDatabase.getInstance().getReference("Login/Student")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_assignment, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_assignment)
        val userId = mAuth.currentUser?.uid
        if (userId != null)
        {
            dReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val uid = i.key
                        if (userId == uid) {
                            val output = i.value as Map<String, String>
                            val uClass = output.getValue("Class")
                            val uYear = output.getValue("Year")
                            lReference.child(uYear).child(uClass).child("Assignment").addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (i in snapshot.children)
                                    {
                                        if(i.key != "ID")
                                        {
                                            val out = i.value as Map<String, String>
                                            link.add(out.getValue("Link").toString())
                                            name.add(out.getValue("Teacher").toString())
                                            subject.add(out.getValue("Subject").toString())
                                            mDate.add(out.getValue("Date").toString())
                                        }
                                    }
                                    val layout = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL,false)
                                    recyclerView.layoutManager = layout
                                    recyclerView.adapter = activity?.let { SecondAdapter(it,link,name,subject,mDate,"Assignment") }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            })
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity,"Check internet connection", Toast.LENGTH_SHORT).show()
                }
            })
        }
        return view
    }

}