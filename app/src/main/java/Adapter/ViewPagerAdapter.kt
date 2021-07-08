package Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.mjsiddiqui.campusmanagement.R
import com.squareup.picasso.Picasso

class ViewPagerAdapter(val mContext: Context, val dateList: MutableList<String>, val contentList: MutableList<String>, val linkList: MutableList<String>, val senderList: MutableList<String>, val titleList: MutableList<String>):PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val x = LayoutInflater.from(mContext).inflate(R.layout.notice_viewer_layout,null)
        val dateTextView:TextView = x.findViewById(R.id.news_date)
        val image:ImageView = x.findViewById(R.id.news_Image)
        val titleTextView:TextView = x.findViewById(R.id.news_Title)
        val contentTextView:TextView = x.findViewById(R.id.news_Text)
        val senderTextView:TextView = x.findViewById(R.id.news_publisher)
        Picasso.get().load(linkList[position]).into(image)
        dateTextView.text = dateList[position]
        titleTextView.text = titleList[position]
        contentTextView.text = contentList[position]
        senderTextView.text = "Published By: ${senderList[position]}"
        container.addView(x)
        return x
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        val viewP = container as ViewPager
        val x = obj as View
        viewP.removeView(x)
    }

    override fun getCount(): Int {
        return titleList.size
    }
}