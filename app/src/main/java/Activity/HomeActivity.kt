package Activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.mjsiddiqui.campusmanagement.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


// This is our Home page activity which will be displayed after login.

class HomeActivity : AppCompatActivity() {
    private lateinit var auth:FirebaseAuth
    private val fReference = FirebaseDatabase.getInstance().getReference("Login")
    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_drawer)
        auth = FirebaseAuth.getInstance()

        val bottomOption: BottomNavigationView = findViewById(R.id.bottomView)
        val mNavigation:NavigationView = findViewById(R.id.navigationView)
        val mView = layoutInflater.inflate(R.layout.header_drawer,null)
        val mText:TextView = mView.findViewById(R.id.mUser)
        val userId = auth.currentUser?.uid
        var uName:String = "Mj-Coder"

        val obj = NewsFragment()
        supportFragmentManager.beginTransaction().add(R.id.main_Container,obj).commit()

        fReference.child("Student").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext,"Check Your Internet Connection", Toast.LENGTH_SHORT).show()
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                for (i in snapshot.children){
                    val myUID = i.key.toString()
                    if (myUID == userId){
                        val output = i.value as Map<String,String>
                        uName = output.getValue("Name")
                        mText.text = uName
                        mNavigation.addHeaderView(mView)
                    }
                }
            }
        })

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolBarView)
        setSupportActionBar(toolbar)

        drawer = findViewById(R.id.drawerLayout)
        val icon: ActionBarDrawerToggle = ActionBarDrawerToggle(this,drawer,toolbar,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawer.addDrawerListener(icon)
        icon.syncState()

        mNavigation.setNavigationItemSelectedListener(object : NavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.itemId
                when(id){
                    R.id.payment -> {
                        val obj2 = PayFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj2).commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.college_web -> {
                        val obj3 = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.mgmnoida.org/"))
                        startActivity(obj3)
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.calender -> {
                        val obj4 = CalendarFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj4).commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.syllabus -> {
                        val obj5 = SyllabusFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj5).commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }
                    R.id.logout -> {
                        auth.signOut()
                        val obj6 = Intent(this@HomeActivity, LoginActivity::class.java)
                        startActivity(obj6)
                        finish()
                    }
                    R.id.notes -> {
                        val obj7 = NotesFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj7).commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }

                    R.id.assignment -> {
                        val obj8 = AssignmentFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj8).commit()
                        drawer.closeDrawer(GravityCompat.START)
                    }
                }
                return true
            }
        })

        bottomOption.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.itemId
                when(id)
                {
                    R.id.news ->{
                        val obj = NewsFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj).commit()
                    }
                    R.id.marks ->{
                        val obj = StudentMarksFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj).commit()
                    }
                    R.id.chat_box ->{
                        val obj = Intent(this@HomeActivity, ChatActivity::class.java)
                        obj.putExtra("Mj5",uName)
                        startActivity(obj)
                    }
                    R.id.attendance ->{
                        val obj = StudentAttendanceFragment()
                        supportFragmentManager.beginTransaction().replace(R.id.main_Container,obj).commit()
                    }
                }
                return true
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_options,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == R.id.about)
        {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    override fun onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START)
        }else{
            this.finish()
        }
    }

}