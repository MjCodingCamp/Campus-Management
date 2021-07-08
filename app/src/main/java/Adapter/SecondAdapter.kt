package Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.mjsiddiqui.campusmanagement.R

class SecondAdapter(context: Context, link:MutableList<String>, name:MutableList<String>, sub:MutableList<String>,mDate:MutableList<String>,dMode:String): RecyclerView.Adapter<SecondAdapter.uHolder>() {
    private val uLink = link
    private val uName = name
    private val uSub = sub
    private val mDate = mDate
    val dMode = dMode
    private val mContext = context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): uHolder {
        if (dMode == "Notes")
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.notes_layout,null)
            val obj1 = uHolder(view)
            return obj1
        }else
        {
            val view = LayoutInflater.from(mContext).inflate(R.layout.assignment_logo,null)
            val obj2 = uHolder(view)
            return obj2
        }

    }

    override fun onBindViewHolder(holder: uHolder, position: Int) {
        holder.text1.setText(uSub[position])
        holder.text2.setText(uName[position])
        holder.text3.setText(mDate[position])
        holder.view.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uLink[position]))
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return uName.size
    }

    class uHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val text1:TextView  = itemView.findViewById(R.id.assignment_textView)
        val text2:TextView  = itemView.findViewById(R.id.teacher_text)
        val text3:TextView  = itemView.findViewById(R.id.date_text)
        val view:ConstraintLayout = itemView.findViewById(R.id.click_notes)
    }
}