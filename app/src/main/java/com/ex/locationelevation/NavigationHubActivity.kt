package com.ex.locationelevation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ex.locationelevation.databinding.ActivityNavigationHubBinding

class NavigationHubActivity : AppCompatActivity() {

    private lateinit var bind: ActivityNavigationHubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityNavigationHubBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if(supportActionBar != null) { supportActionBar!!.hide() }

        bind.bottomNavigationView.setupWithNavController(
            findNavController(R.id.fragmentContainerView)
        )

    }

}