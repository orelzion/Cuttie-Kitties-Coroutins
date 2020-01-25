package com.github.orelzion.cuttiekitties.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.orelzion.cuttiekitties.R
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, BreedListFragment())
            .commit()
    }
}
