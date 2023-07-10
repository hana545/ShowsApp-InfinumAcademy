package infinuma.android.shows.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import infinuma.android.shows.R
/*
1. Put the app in background and move it back to foreground

2023-07-09 12:18:59.329  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onCreate
2023-07-09 12:18:59.346  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:18:59.349  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onResume
2023-07-09 12:19:07.970  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:19:07.990  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:19:09.052  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onRestart
2023-07-09 12:19:09.054  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:19:09.055  8869-8869  ActivityLifecycle       infinuma.android.shows               D  onResume


2. Kill the app

2023-07-09 12:19:47.704  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:19:47.721  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:19:48.528  8874-8874  ActivityLifecycle       infinuma.android.shows               D  onDestroy

3. Lock the phones screen and unlock it

2023-07-09 12:20:09.197  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onPause
2023-07-09 12:20:09.225  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onStop
2023-07-09 12:20:14.227  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onRestart
2023-07-09 12:20:14.233  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onStart
2023-07-09 12:20:14.237  9302-9302  ActivityLifecycle       infinuma.android.shows               D  onResume

*/


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Log.d("ActivityLifecycle", "onCreate")
    }

    override fun onStart() {
        Log.d("ActivityLifecycle", "onStart")
        super.onStart()
    }

    override fun onPause() {
        Log.d("ActivityLifecycle", "onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d("ActivityLifecycle", "onResume")
        super.onResume()
    }

    override fun onStop() {
        Log.d("ActivityLifecycle", "onStop")
        super.onStop()
    }
    override fun onDestroy() {
        Log.d("ActivityLifecycle", "onDestroy")
        super.onDestroy()
    }

    override fun onRestart() {
        Log.d("ActivityLifecycle", "onRestart")
        super.onRestart()
    }
}