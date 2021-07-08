package Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.mjsiddiqui.campusmanagement.R
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception

class ProfileActivity : AppCompatActivity() {
    private var visibility:String = "hide"
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mReference:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContentView(R.layout.activity_profile)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser
        mReference = FirebaseDatabase.getInstance().getReference("Login/Student")
        val userName:TextView = findViewById(R.id.full_name)
        val userEmail: TextView = findViewById(R.id.email)
        val userRoll:TextView = findViewById(R.id.roll_number)
        val userClass:TextView = findViewById(R.id.class_no)
        val userCourse:TextView = findViewById(R.id.mCourse)
        val userBranch:TextView = findViewById(R.id.mBranch)
        val userSem:TextView = findViewById(R.id.year)

        val pDialog = SpotsDialog.Builder().setContext(this).build()
        pDialog.setCancelable(false)
        pDialog.setMessage("Please Wait...")

        pass_Edit.setOnClickListener {
            val view = layoutInflater.inflate(R.layout.change_password,null)
            val obj = AlertDialog.Builder(this)
            obj.setView(view)
            obj.setCancelable(false)
            val showMe:ImageView = view.findViewById(R.id.new_show)
            val showU:ImageView = view.findViewById(R.id.old_show)
            val oldPass:EditText = view.findViewById(R.id.old_password)
            val newPass:EditText = view.findViewById(R.id.new_password)
            showMe.setOnClickListener{
                if (visibility == "hide"){
                    newPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    visibility = "show"
                    showMe.setImageResource(R.drawable.show)
                }else{
                    newPass.transformationMethod = PasswordTransformationMethod.getInstance()
                    visibility = "hide"
                    showMe.setImageResource(R.drawable.hide)
                }
            }
            showU.setOnClickListener {
                if (visibility == "hide"){
                    oldPass.transformationMethod = HideReturnsTransformationMethod.getInstance()
                    visibility = "show"
                    showU.setImageResource(R.drawable.show)
                }else{
                    oldPass.transformationMethod = PasswordTransformationMethod.getInstance()
                    visibility = "hide"
                    showU.setImageResource(R.drawable.hide)
                }
            }
            obj.setNegativeButton("Cancel") { dialog, which ->  }
            obj.setPositiveButton("Submit") { dialog, which ->
                pDialog.show()
                try{
                    if (user != null){
                        val currentEmail = user.email.toString()
                        val currentPass = oldPass.text.toString()
                        val credential:AuthCredential = EmailAuthProvider.getCredential(currentEmail,currentPass)
                        user.reauthenticate(credential).addOnCompleteListener {
                            if (it.isSuccessful){
                                user.updatePassword(newPass.text.toString()).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        pDialog.dismiss()
                                        Toast.makeText(applicationContext,"Successfully Updated",Toast.LENGTH_SHORT).show()
                                    }else {
                                        pDialog.dismiss()
                                        Toast.makeText(applicationContext,"Something Wrong",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }else
                            {
                                pDialog.dismiss()
                                Toast.makeText(applicationContext,"Wrong Password",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }catch (e:Exception){
                    pDialog.dismiss()
                    Toast.makeText(applicationContext,"Invalid Details",Toast.LENGTH_SHORT).show()
                }
            }
            obj.show()
        }

        if(user != null){
            pDialog.show()
            val userId = user.uid
            mReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(applicationContext,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children){
                        val myUID = i.key.toString()
                        if (myUID == userId){
                            val output = i.value as Map<String,String>
                            try {
                                userName.setText(output.getValue("Name"))
                                userEmail.text = output.getValue("Email")
                                userCourse.setText(output.getValue("Course"))
                                userBranch.setText(output.getValue("Branch"))
                                userClass.setText(output.getValue("Class"))
                                userRoll.setText(output.getValue("Roll No"))
                                userSem.setText(output.getValue("Year"))

                            }catch (e:Exception){
                                Toast.makeText(applicationContext,"Please complete your profile",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    pDialog.dismiss()
                }
            })
        }

        btn_Save.setOnClickListener {
            Toast.makeText(applicationContext,"This time you can't edit", Toast.LENGTH_SHORT).show()
            val obj = Intent(this, HomeActivity::class.java)
            startActivity(obj)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }
}