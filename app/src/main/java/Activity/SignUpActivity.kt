package Activity


import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.*
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.database.FirebaseDatabase
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_signup.*

class SignUpActivity : AppCompatActivity() {
    private lateinit var pDialog: AlertDialog
    private var visibility:String = "hide"
    private val mCourse = arrayOf("B.Tech","M.Tech")
    private val mBranch = arrayOf("CSE","ME","EC","CE")
    private val signUpMode:Array<String> = arrayOf("Student")
    private val mReference = FirebaseDatabase.getInstance().getReference("Registration")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_signup)

        pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")

        btn_signUp.setOnClickListener {
            val passIn = password.text.toString()
            val userIn = email_Id.text.toString()
            if (passIn.isEmpty() or userIn.isEmpty() or name.text.isEmpty() or course.text.isEmpty() or branch.text.isEmpty()){
                Toast.makeText(applicationContext,"Please fill all details",Toast.LENGTH_SHORT).show()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(userIn).matches()){
                Toast.makeText(applicationContext,"Enter a valid email",Toast.LENGTH_SHORT).show()
            } else if(!LoginActivity.Utils.password.matcher(passIn).matches()) {
                Toast.makeText(applicationContext, "Choose a strong password", Toast.LENGTH_SHORT).show()
            } else{
                pDialog.show()
                val fullName = name.text.trim().toString()
                val email = email_Id.text.trim().toString()
                val userPassword = password.text.trim().toString()
                val userCourse = course.text.trim().toString()
                val userBranch = branch.text.trim().toString()
                val mMap:Map<String,String> = mapOf("Name" to fullName, "Email" to email, "Password" to userPassword, "Course" to userCourse, "Branch" to userBranch, "Status" to "Pending")
                val hashKey = mReference.push().key.toString()

                mReference.child(hashKey).setValue(mMap).addOnSuccessListener {
                    signUp()
                    name.text.clear(); email_Id.text.clear(); password.text.clear(); course.text.clear(); branch.text.clear()
                }.addOnFailureListener {
                    Toast.makeText(applicationContext,"Check Internet Connection",Toast.LENGTH_SHORT).show()
                }
            }
        }

        val passwordVisibility: ImageView = findViewById(R.id.pass_eye)
        passwordVisibility.setOnClickListener {
            if (visibility == "hide"){
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
                visibility = "show"
                passwordVisibility.setImageResource(R.drawable.show)
            }else{
                password.transformationMethod = PasswordTransformationMethod.getInstance()
                visibility = "hide"
                passwordVisibility.setImageResource(R.drawable.hide)
            }
        }

        val spinner: Spinner = findViewById(R.id.spin_SignUp)
        spinner.adapter = ArrayAdapter(this, R.layout.login_mode,signUpMode)

        val item1 = ArrayAdapter(this, R.layout.login_mode,mCourse)
        val item2 = ArrayAdapter(this, R.layout.login_mode,mBranch)
        course.setAdapter(item1)
        branch.setAdapter(item2)
    }

    private fun signUp() {
        val obj = Dialog(this)
        val mView = layoutInflater.inflate(R.layout.alert_dialog,null)
        val textView:TextView = mView.findViewById(R.id.alert_Message)
        textView.text = "We will verify your details if you belong to MGM society then you will get access, otherwise not."
        obj.setContentView(mView)
        obj.window?.setBackgroundDrawable(getDrawable(R.drawable.alert_backgound))
        obj.setCancelable(true)
        obj.show()
        pDialog.dismiss()
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}