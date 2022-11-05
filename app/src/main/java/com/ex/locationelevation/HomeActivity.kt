package com.ex.locationelevation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ex.locationelevation.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var bind: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.goToReimaginedButton.setOnClickListener{
            Intent(this, reimaginedView::class.java)
                .apply { startActivity(this) }
        }

        bind.goToLocationButton.setOnClickListener{
            Intent(this, UsingClientProvider::class.java)
                .apply { startActivity(this) }
        }

        bind.goToGenerateQRButton.setOnClickListener{
            Intent(this, QRGeneratorActivity::class.java)
                .apply { startActivity(this) }
        }

        bind.goToScanQRCodeButton2.setOnClickListener{
            Intent(this, QRCodeScannerActivity::class.java)
                .apply { startActivity(this) }
        }

        bind.goToOxygenCoinDemoButton.setOnClickListener{
            Intent(this, OxygenCoinDemoActivity::class.java)
                .apply { startActivity(this) }
        }



    }


}