package com.mjsiddiqui.campusmanagement

import Adapter.ViewPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewsFragment : Fragment() {
    private val linkList = mutableListOf<String>()
    private val contentList = mutableListOf<String>()
    private val dateList = mutableListOf<String>()
    private val titleList = mutableListOf<String>()
    private val senderList = mutableListOf<String>()
    private val mReference = FirebaseDatabase.getInstance().getReference("Notice")

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        val viewPager:ViewPager = view.findViewById(R.id.newsViewPager)

        mReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children)
                {
                    if (i.key != "ID")
                    {
                        val output = i.value as Map<String, String>
                        dateList.add(output.getValue("Date").toString())
                        contentList.add(output.getValue("Content").toString())
                        linkList.add(output.getValue("Link").toString())
                        senderList.add(output.getValue("Publisher").toString())
                        titleList.add(output.getValue("Title").toString())
                    }
                }
                viewPager.adapter = ViewPagerAdapter(activity!!,dateList,contentList,linkList,senderList,titleList)
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        return view
    }

}