package Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import Activity.PdfViewerActivity
import com.mjsiddiqui.campusmanagement.R
import com.squareup.picasso.Picasso


class FirstAdapter(context: Context, list: MutableList<String>, link: MutableList<String>): RecyclerView.Adapter<FirstAdapter.mHolder>() {
    private val context = context
    private val list = list
    private val link = link
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): mHolder {
        val mView = LayoutInflater.from(context).inflate(R.layout.syllabus_layout, parent, false)
        return mHolder(mView)
    }

    override fun onBindViewHolder(holder: mHolder, position: Int) {
        Picasso.get().load(list[position]).into(holder.imageView)
        holder.imageView.setOnClickListener{
            val intent = Intent(context, PdfViewerActivity::class.java)
            intent.putExtra("Mj5",link[position])
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class mHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView:ImageView = itemView.findViewById(R.id.syllabus_logo)
    }
}