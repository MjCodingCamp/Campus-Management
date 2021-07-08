package Activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_notice.*

class NoticeActivity : AppCompatActivity() {
    private val mReference = FirebaseDatabase.getInstance().getReference("Notice")
    private lateinit var pDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice)

        pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")
        pDialog.show()

        val mAuth = FirebaseAuth.getInstance()
        val uid = mAuth.uid
        val mKey = intent.getStringExtra("Mj4")
        val mToken = intent.getStringExtra("Mj5")

        val deleteNotice:LinearLayout = findViewById(R.id.notice_delete)
        deleteNotice.setOnClickListener {
            if (mKey != null)
            {
                val obj = AlertDialog.Builder(this)
                obj.setTitle("Are you Sure!")
                obj.setMessage("Once you have deleted this notice it will be removed for forever.")
                obj.setPositiveButton("Yes", object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val mReference = FirebaseDatabase.getInstance().getReference("Notice")
                        mReference.child(mToken).removeValue().addOnSuccessListener {
                            val nReference = FirebaseDatabase.getInstance().getReference("Login/Teacher/$uid/Notice-History")
                            nReference.child(mKey).removeValue().addOnSuccessListener {
                                Toast.makeText(applicationContext,"Deleted",Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@NoticeActivity,HomeActivity2::class.java)
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
                })
                obj.setNegativeButton("No") { dialog, which -> }
                obj.show()
            }
        }

        if (mToken != null)
        {
            mReference.child(mToken).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val output = snapshot.value as Map<String, String>
                    val content = output.getValue("Content")
                    val date = output.getValue("Date")
                    val link = output.getValue("Link")
                    val publish = output.getValue("Publisher")
                    val title = output.getValue("Title")

                    news_mPublisher.text = publish
                    news_mText.text = content
                    news_mDate.text = date
                    news_mTitle.text = title
                    Picasso.get().load(link).into(news_Logo)
                    pDialog.dismiss()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
        else
        {
            Toast.makeText(applicationContext,"Something Wrong",Toast.LENGTH_SHORT).show()
        }
    }
}