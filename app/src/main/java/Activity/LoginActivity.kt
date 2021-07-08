package Activity


import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_login.*
import java.lang.Exception
import java.util.regex.Pattern

// This is our login page activity which will display after Main activity if user will not be login otherwise it will not be displayed.

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth:FirebaseAuth
    private var visibility:String = "hide"
    private lateinit var spinner: Spinner
    private lateinit var pDialog:android.app.AlertDialog
    private val loginMode:Array<String> = arrayOf("Student","Teacher")
    private lateinit var dReference:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_login)

        val fReference = FirebaseDatabase.getInstance()
        dReference = fReference.getReference("Login")   // Here i am getting reference of realtime database.
        mAuth = FirebaseAuth.getInstance()  //Here i am getting reference of firebase authentication.

        // Here i am using third party API of AlertDialog which i shall use for processing.
        pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")

        signUp_TextView.setOnClickListener {  // Here i am adding listener with signUp text view. If any one click on SignUp then SignUp Activity will display.
            val obj = Intent(this, SignUpActivity::class.java)
            startActivity(obj)
        }

        forgot_TextView.setOnClickListener{
            // Here I am using a custom Alert Dialog which user will use to forgot password. If anyone wants to forgot his password, he/she can use this alert dialog.
            val obj = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.forgot_pasword,null)
            obj.setView(view)
            obj.setCancelable(false)
            obj.setNegativeButton("Cancel") { dialog, which ->  }
            obj.setPositiveButton("Submit") { dialog, which -> pDialog.show()
                val editText = view.findViewById<EditText>(R.id.forgot_passText)
                val emailText = editText.text.trim().toString()
                try {
                    mAuth.sendPasswordResetEmail(emailText).addOnCompleteListener(this){
                        if (it.isSuccessful){
                            forgotDone()
                        }else{
                            pDialog.dismiss()
                            Toast.makeText(applicationContext,"Invalid Email Address",Toast.LENGTH_SHORT).show()
                        }
                    }
                }catch (e:Exception){
                    pDialog.dismiss()
                }
            }
            obj.show()
        }

        btn_Login.setOnClickListener {
            pDialog.show()
            val pass = pass_EditText.text.trim().toString()
            val email = user_EditText.text.trim().toString()
            if(pass.isEmpty()  || email.isEmpty()){   // It will check if user name or password is empty then this block will be execute.
                Toast.makeText(applicationContext,"Please enter login details",Toast.LENGTH_SHORT).show()
                pDialog.dismiss()
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){   // Otherwise it will Email is a valid or not using predefine pattern.
                Toast.makeText(applicationContext,"Invalid Email",Toast.LENGTH_SHORT).show()
                pDialog.dismiss()
            } else if(!Utils.password.matcher(pass).matches()){ // Otherwise it will password is a valid or not according to the DFA.
                Toast.makeText(applicationContext,"Invalid Password",Toast.LENGTH_SHORT).show()
                pDialog.dismiss()
            } else{
                mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(this){
                    it.addOnFailureListener(){
                        pDialog.dismiss()
                        Toast.makeText(applicationContext,"Invalid Details",Toast.LENGTH_SHORT).show()
                    }
                    it.addOnSuccessListener(){
                        val dummy = spinner.selectedItem.toString()
                        // Here i am creating a .xml file which i am using to store login mode.
                        val obj:SharedPreferences.Editor = getSharedPreferences("Login_Mode",Context.MODE_PRIVATE).edit()
                        obj.putString("Mj5",dummy).apply()
                        updateUI()  // don't confused it just a user define function
                        pDialog.dismiss()
                    }
                }
            }
        }

        val passwordVisibility:ImageView = findViewById(R.id.pass_Show)
        passwordVisibility.setOnClickListener { // Here i am using transformation to show or hide login password with the help of listener.
            if (visibility == "hide"){
                pass_EditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                visibility = "show"     // Visibility is just a variable of string type, which i am using to trace the condition of the EditText.
                passwordVisibility.setImageResource(R.drawable.show)    // Here i am changing the image of ImageView according to the visibility.
            }else{
                pass_EditText.transformationMethod = PasswordTransformationMethod.getInstance()
                visibility = "hide"
                passwordVisibility.setImageResource(R.drawable.hide)    // Here i am also changing the image of ImageView according to the visibility.
            }
        }

        spinner = findViewById(R.id.spin_Login)
        // Here i am using a ArrayAdapter to store login modes(some strings) within the spinner with a custom layout. You can also checkout login_mode layout.
        spinner.adapter = ArrayAdapter(this, R.layout.login_mode,loginMode)
    }

    private fun forgotDone() {
        pDialog.dismiss()
        val obj = Dialog(this)
        obj.setContentView(R.layout.alert_dialog)
        obj.window?.setBackgroundDrawable(getDrawable(R.drawable.alert_backgound))
        obj.setCancelable(true)
        obj.show()
    }

    private fun updateUI() {
        var status:Boolean = false
        val user = mAuth.currentUser
        if (user != null){
            val userID = user.uid
            val sPreference:SharedPreferences = getSharedPreferences("Login_Mode", Context.MODE_PRIVATE)
            val logMode = sPreference.getString("Mj5",null).toString()
            if (logMode == "Student"){
                dReference.child(logMode).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children){
                            val myUID = i.key.toString()
                            if (myUID == userID){
                                status = true
                                val intent1 = Intent(this@LoginActivity, HomeActivity::class.java)
                                startActivity(intent1)
                                finish()
                            }
                        }
                        if (!status){
                            Toast.makeText(applicationContext,"Please login as a Teacher",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }else if(logMode == "Teacher"){
                dReference.child(logMode).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(applicationContext,"Check Your Internet Connection",Toast.LENGTH_SHORT).show()
                    }
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (i in snapshot.children){
                            val myUID = i.key.toString()
                            if (myUID == userID){
                                status = true
                                val intent2 = Intent(this@LoginActivity, HomeActivity2::class.java)
                                startActivity(intent2)
                                finish()
                            }
                        }
                        if (!status){
                            Toast.makeText(applicationContext,"Please login as a Student",Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            }else{
                Toast.makeText(applicationContext,"Please try again",Toast.LENGTH_SHORT).show()
            }

        }
    }

    class Utils() { // Here i am using a DFA or Regex for validation of user password with some conditions and restrictions.
        companion object{
            val password: Pattern = Pattern.compile("^" +
                        "(?=.*[0-9])" +     //a digit must occur at least once
                        "(?=.*[a-z])" +     //a lower case letter must occur at least once
                        "(?=.*[A-Z])" +     //an upper case letter must occur at least once
                        "(?=.*[@#$%^&+=])" +    //a special character must occur at least once
                        "(?=\\S+$)" +       //a special character must occur at least once
                        ".{8,}" +       //password should be of eight digits
                        "$")
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}