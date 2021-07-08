package Activity


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// This is our main activity which I am using it to display as splash screen only for 2 sec.

class MainActivity : AppCompatActivity() {
    private var mHandler: Handler? = null  // instance of Handler here I am assigning null value.
    private lateinit var mRunnable: Runnable        // instance of Runnable class which I shall late assign it.
    private lateinit var auth: FirebaseAuth
    private val dReference = FirebaseDatabase.getInstance().getReference("Login")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Here i am using Immersive Sticky mode to display UI on a full screen when user will start this app.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_main)
        auth = FirebaseAuth.getInstance()

        mRunnable = Runnable { // This is implementation of Runnable, which will execute after 2sec when mHandler will call postDelayed method.
            val mUser = auth.currentUser    //here i am getting current user if User will not login then it will return null value.
            if (mUser == null)
            {
                val obj1 = Intent(this, LoginActivity::class.java)
                startActivity(obj1)
                finish()
            }else{  //if user will be login then this block will be executed
                val userUID = mUser.uid //here i am getting current user UID
                val sPreference: SharedPreferences = getSharedPreferences("Login_Mode", Context.MODE_PRIVATE)
                val logMode = sPreference.getString("Mj5",null).toString()
                when(logMode){
                    "Student" -> {
                        dReference.child(logMode).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(applicationContext,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children){
                                    val myUID = i.key.toString()
                                    if (myUID == userUID){
                                        val intent1 = Intent(this@MainActivity, HomeActivity::class.java)
                                        startActivity(intent1)
                                        finish()
                                        break
                                    }
                                }
                            }
                        })
                    }
                    "Teacher" -> {
                        dReference.child(logMode).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(applicationContext,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
                            }
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (i in snapshot.children){
                                    val myUID = i.key.toString()
                                    if (myUID == userUID){
                                        val intent2 = Intent(this@MainActivity, HomeActivity2::class.java)
                                        startActivity(intent2)
                                        finish()
                                        break
                                    }
                                }
                            }
                        })
                    }
                    else -> {   //if anyone change the data of the xml file then this app will automatically logout.
                        auth.signOut()
                        Toast.makeText(applicationContext,"Please try again",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        mHandler = Handler()  // Here i am trying to manager application main thread handler.
        mHandler?.postDelayed(mRunnable,500)
        // Here i am calling postDelayed method of handler class which will help of create a separate thread and execute mRunnable block after 2sec.
    }

    override fun onResume() {
        super.onResume()
        // Here i am using immersive Sticky mode to display UI on a full screen when user will resume this app.
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mHandler != null){
            // Here i am trying to remove separate thread, because it can become reason of memory leak.
            mHandler?.removeCallbacks(mRunnable)
        }
    }
}