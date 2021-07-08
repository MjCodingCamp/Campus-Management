package Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mjsiddiqui.campusmanagement.R

class HistoryAdapter(context: Context, date: MutableList<String>, link: MutableList<String>, name: MutableList<String>, branch: MutableList<String>): RecyclerView.Adapter<HistoryAdapter.mHolder>() {
    private val mContext = context
    private val dateList = date
    private val linkList = link
    private val subList = name
    private val branchList = branch

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mHolder {
        val mView = LayoutInflater.from(mContext).inflate(R.layout.history_layout, parent, false)
        return mHolder(mView)
    }

    override fun onBindViewHolder(holder: mHolder, position: Int) {
        holder.textViewDate.text = dateList[position]
        holder.textViewSub.text = subList[position]
        holder.textViewBranch.text = branchList[position]
        holder.clickMe.setOnClickListener {
            val obj = Intent(Intent.ACTION_VIEW, Uri.parse(linkList[position]))
            mContext.startActivity(obj)
        }
    }

    override fun getItemCount(): Int {
        return linkList.size
    }

    class mHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val textViewDate: TextView = itemView.findViewById(R.id.attendance_history_date)
        val textViewBranch: TextView = itemView.findViewById(R.id.attendance_history_time)
        val textViewSub: TextView = itemView.findViewById(R.id.attendance_history_sub)
        val clickMe: ImageView = itemView.findViewById(R.id.attendance_historyLogo)
    }
}