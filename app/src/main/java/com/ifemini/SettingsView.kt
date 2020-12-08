package com.ifemini


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_settings.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.net.Uri
import androidx.preference.Preference
import androidx.preference.PreferenceDataStore
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
//import com.google.firebase.storage.FirebaseStorage
//import com.google.firebase.storage.StorageReference
//import com.google.firebase.storage.UploadTask
import com.takisoft.preferencex.PreferenceFragmentCompat
import java.net.URI
import java.util.*



/**
 * A simple [Fragment] subclass.
 */
class SettingsView : PreferenceFragmentCompat() {
    private val CREATE_BACKUP_FILE_CODE = 1
    private val SELECT_RESTORE_FILE_CODE = 0
//    var firebaseStore: FirebaseStorage? = null
//    var storageReference: StorageReference? = null


    private val realm = Realm.getDefaultInstance()

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {

        preferenceManager.preferenceDataStore = RealmPreferenceDataStore(context, realm)
        setPreferencesFromResource(R.xml.preferences, rootKey);

        findPreference<Preference>("backup_data")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            backupDatabase()
            true
        }
        findPreference<Preference>("restore_data")?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            restoreDatabase()
            true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close()
    }

    private fun backupDatabase() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/octet-stream"
            putExtra(Intent.EXTRA_TITLE, "ifemini-backup-${Calendar.getInstance().formatDate()}")

        }

        startActivityForResult(intent, CREATE_BACKUP_FILE_CODE)
    }

    private fun restoreDatabase() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }

        startActivityForResult(intent, SELECT_RESTORE_FILE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SELECT_RESTORE_FILE_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            val success = importDBFromUri(uri, context!!)
            if (success) {
                Toast.makeText(context!!, resources.getString(R.string.restore_success), Toast.LENGTH_LONG).show()
                (activity as? SettingsActivity)?.restartApp()
            }
            else Toast.makeText(context!!, resources.getString(R.string.restore_failed), Toast.LENGTH_LONG).show()
        } else if (requestCode == CREATE_BACKUP_FILE_CODE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data

            val success = exportDBToURI(uri, context!!)

            if (success) {
                if (uri != null) {
//                    uploadToCloud(uri)
                };
                Toast.makeText(context!!, resources.getString(R.string.db_export_success), Toast.LENGTH_LONG).show()
            }
            else Toast.makeText(context!!, resources.getString(R.string.db_export_fail), Toast.LENGTH_LONG).show()
        }
    }

//    private fun uploadToCloud(filePath: Uri) {
//          val ref = storageReference?.child("uploads/" + UUID.randomUUID().toString())
//        val uploadTask = ref?.putFile(filePath)
//        val urlTask = uploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    Toast.makeText(context!!, "not done", Toast.LENGTH_LONG).show()
//
//                    throw it
//                }
//            }
//            return@Continuation ref.downloadUrl
//        })?.addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Toast.makeText(context!!, "done", Toast.LENGTH_LONG).show()
//
//                val downloadUri = task.result
//            } else {
//                Toast.makeText(context!!, "task not successfull", Toast.LENGTH_LONG).show()
//
//
//                // Handle failures
//            }
//        }?.addOnFailureListener{
//            Toast.makeText(context!!, "task Failure", Toast.LENGTH_LONG).show()
//
//
//        }
//    }

    companion object {

        fun newInstance(): SettingsView {
            val fragment = SettingsView()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }
}

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        // draw the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    // exit when the back button is pressed
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle arrow click here
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun restartApp() {
        val mStartActivity = Intent(this, MainActivity::class.java)
        val mPendingIntentId = 123456
        val mPendingIntent = PendingIntent.getActivity(this.applicationContext,
                mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 10, mPendingIntent)

        val returnIntent = Intent()
        returnIntent.putExtra("exitMain", true)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }
}

// this class is kind of a hack. It persists preferences in a realm CycleInfo object
class RealmPreferenceDataStore(context: Context?, private val realm: Realm): PreferenceDataStore() {
    private val mentalSymptoms = context?.resources!!.getStringArray(R.array.categories)[1]
    private val physicalActivity = context?.resources!!.getStringArray(R.array.categories)[2]
    private val sexualActivity = context?.resources!!.getStringArray(R.array.categories)[3]
    private val appetite = context?.resources!!.getStringArray(R.array.categories)[4]

    //TODO clean this up once we're sure it works
    override fun getBoolean(key: String?, defValue: Boolean): Boolean {
        return when(key) {
            "mental_tracking" ->
                realm.where(Category::class.java)
                        .equalTo("name", mentalSymptoms).findFirst()?.active ?: defValue
            "physical_tracking" ->
                realm.where(Category::class.java)
                    .equalTo("name", physicalActivity).findFirst()?.active ?: defValue
            "sexual_tracking" ->
                realm.where(Category::class.java)
                        .equalTo("name", sexualActivity).findFirst()?.active ?: defValue
            "appetite_tracking" ->
                realm.where(Category::class.java)
                        .equalTo("name", appetite).findFirst()?.active ?: defValue
            else -> super.getBoolean(key, defValue)
        }
    }

    override fun putBoolean(key: String?, value: Boolean) {
        Log.d(TAG, "put boolean called for $key")
        when(key) {
            "mental_tracking" -> realm.setCategoryState(mentalSymptoms, value)
            "physical_tracking" -> realm.setCategoryState(physicalActivity, value)
            "sexual_tracking" -> realm.setCategoryState(sexualActivity, value)
            "appetite_tracking" -> realm.setCategoryState(appetite, value)
            else -> super.putBoolean(key, value)
        }
    }

    override fun getString(key: String?, defValue: String?): String? {
        Log.d(TAG, "get string called for $key")
        return when(key) {
            "period_length" -> realm.getCycleInfo().periodLength.toString()
            "cycle_length" -> realm.getCycleInfo().cycleLength.toString()
            else -> super.getString(key, defValue)
        }
    }

    override fun putString(key: String?, value: String?) {
        when(key) {
            "period_length" -> realm.executeTransactionAsync {
                it.where(CycleInfo::class.java).findFirst()?.periodLength = value!!.toInt()
            }
            "cycle_length" -> realm.executeTransactionAsync {
                it.where(CycleInfo::class.java).findFirst()?.cycleLength = value!!.toInt()
            }
            else -> super.putString(key, value)
        }
    }

    companion object {
        const val TAG = "SETTINGS"
    }
}