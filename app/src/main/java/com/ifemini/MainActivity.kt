package com.ifemini

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ifemini.activity.ForgetPasswordActivity
import com.ifemini.activity.LoginActivity
import com.ifemini.activity.SafetyHomeActivity
import com.ifemini.intro.AppIntroActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private val SETTINGS_CODE = 3392
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = Firebase.auth

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val firstStart = preferences.getBoolean("first_start", true)
        // we added appetite in a later version, this code checks if the current system has been updated
        val appetitePresent = preferences.getBoolean("appetite_present", false)

        if (firstStart) {
            val intent = Intent(this, AppIntroActivity::class.java)
            Log.d(TAG, "starting app intro")
            // set up realm
            initializeRealm(this)
            // if this is the first start, the initialize will insert appetite
            preferences.edit().putBoolean("appetite_present", true).apply()
            startActivity(intent)
        } else if (!appetitePresent) {
        insertAppetite(this)
        preferences.edit().putBoolean("appetite_present", true).apply()
        }

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        val pagerAdapter = TabPagerAdapter(supportFragmentManager,
                this@MainActivity)
        viewPager.adapter = pagerAdapter

        // Give the TabLayout the ViewPager
        sliding_tabs.setupWithViewPager(viewPager)

        for (i in pagerAdapter.tabText.indices) {
            sliding_tabs.getTabAt(i)?.customView = pagerAdapter.getTabView(i)
        }
    }

    // setup the options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val currentUser = auth.currentUser
//        val uID = currentUser.uid
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, SETTINGS_CODE)
                return true
            }
            R.id.login -> {

            }
            R.id.logout -> {
                val intent = Intent(this@MainActivity, ForgetPasswordActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.save_phone -> {
                val intent = Intent(this@MainActivity, SafetyHomeActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return false
    }

    // code to close app if database is imported so we can restart
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == SETTINGS_CODE) {
            if (data?.getBooleanExtra("exitMain", false) == true) finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun navToDayView(day: Calendar) {
        // go to the index of the day view
        viewPager.currentItem = 1
        (viewPager.adapter as? TabPagerAdapter)?.setDayViewDay(day)
    }

    companion object {
        const val TAG = "MAIN"
    }
}
