package com.ahmadshahwaiz.ghoststrings

import android.app.Application
import com.ghoststrings.sdk.GhostStrings
import com.ghoststrings.sdk.GhostStringsConfig

class GhostStringsApp : Application() {

    private val BACKEND           = "https://ghoststrings.ai"
    private val DEBUG_PROJECT_ID  = "dk_5c22c59fc93e46e588fecb22"
    private val PROD_PROJECT_ID   = "pk_c3c79738fe5643c0a3432fe4"

    override fun onCreate() {
        super.onCreate()

        // Initialize GhostStrings SDK once for the entire app lifecycle
        GhostStrings.init(
            context = this,
            config = GhostStringsConfig(
                projectId = if (BuildConfig.DEBUG) DEBUG_PROJECT_ID else PROD_PROJECT_ID,
                baseUrl   = BACKEND,
                debugMode = BuildConfig.DEBUG
            )
        )

        // Trigger an initial sync
        GhostStrings.sync()
    }
}
