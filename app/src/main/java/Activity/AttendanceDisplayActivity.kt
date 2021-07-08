package Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.mjsiddiqui.campusmanagement.R
import java.util.ArrayList

class AttendanceDisplayActivity : AppCompatActivity() {
    private var mUser = ArrayList<String>()
    private var mData = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_attendance_display)
        val user = intent.getStringArrayListExtra("Mj3")
        val data = intent.getStringArrayListExtra("Mj4")
        val mDate = intent.getStringExtra("Mj5")
        val mTime = intent.getStringExtra("Mj6")
        val textView1:TextView = findViewById(R.id.history_AttendanceDate)
        val textView2:TextView = findViewById(R.id.history_AttendanceTime)
        val mLayout:LinearLayout = findViewById(R.id.attendance_history_display)
        textView1.text = mDate
        textView2.text = mTime

        if (user != null && data != null)
        {
            mUser = user
            mData = data
            val mSize = mUser.size
            for (i in 0 until mSize)
            {
                val sView = layoutInflater.inflate(R.layout.attendance_history_view,null)
                val sUser:TextView = sView.findViewById(R.id.history_AttendanceUser)
                val sData:TextView = sView.findViewById(R.id.history_AttendanceUserData)
                sUser.text = mUser[i]

                if (mData[i] == "Present")
                {
                    sData.text = mData[i]
                    val x = getColor(R.color.colorEvent2)
                    sData.setTextColor(x)
                }else
                {
                    sData.text = mData[i]
                    val x = getColor(R.color.colorEvent)
                    sData.setTextColor(x)
                }
                mLayout.addView(sView)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

}