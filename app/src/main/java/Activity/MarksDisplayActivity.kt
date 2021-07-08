package Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mjsiddiqui.campusmanagement.R

class MarksDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks_display)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        val mLayout:LinearLayout = findViewById(R.id.marks_history_display)

        val mDate = intent.getStringExtra("Mj3")
        val mExam = intent.getStringExtra("Mj4")
        if (mDate != null && mExam != null)
        {
            val textView1:TextView = findViewById(R.id.history_MarksDate)
            val textView2:TextView = findViewById(R.id.history_ExamName)
            textView1.text = mDate
            textView2.text = mExam
        }

        val userName = intent.getStringArrayListExtra("Mj5")
        val userData = intent.getStringArrayListExtra("Mj6")
        if (userName != null && userData != null)
        {
            val size = userName.size
            for (i in 0 until size)
            {
                val mView = layoutInflater.inflate(R.layout.marks_history_layout,null)
                val textView1:TextView = mView.findViewById(R.id.history_MUser)
                val textView2:TextView = mView.findViewById(R.id.history_MData)
                textView1.text = userName[i]
                textView2.text = userData[i]
                mLayout.addView(mView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}