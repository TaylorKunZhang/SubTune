package cc.taylorzhang.subtune.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class NotificationActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_FROM_NOTIFICATION, true)
        startActivity(intent)
        finish()
    }
}