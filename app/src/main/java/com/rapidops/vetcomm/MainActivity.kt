package com.rapidops.vetcomm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var intentTypeText: TextView
    private lateinit var moduleText: TextView
    private lateinit var actionText: TextView
    private lateinit var queryText: TextView
    private lateinit var queryContainer: LinearLayout
    private lateinit var rawIntentText: TextView
    private lateinit var intentCard: CardView
    private lateinit var rawIntentCard: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        intentTypeText = findViewById(R.id.intentTypeText)
        moduleText = findViewById(R.id.moduleText)
        actionText = findViewById(R.id.actionText)
        queryText = findViewById(R.id.queryText)
        queryContainer = findViewById(R.id.queryContainer)
        rawIntentText = findViewById(R.id.rawIntentText)
        intentCard = findViewById(R.id.intentCard)
        rawIntentCard = findViewById(R.id.rawIntentCard)

        // Process initial intent
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            setIntent(it)
            processIntent(it)
        }
    }

    private fun processIntent(intent: Intent?) {
        if (intent == null) {
            showNoIntentState()
            return
        }

        // Display raw intent data for debugging
        showRawIntentData(intent)

        // Determine intent type
        val intentType = when {
            intent.hasExtra("feature") -> "OPEN_APP_FEATURE"
            intent.hasExtra("module") || intent.data?.getQueryParameter("module") != null -> "GET_RECORD"
            else -> "UNKNOWN"
        }
        intentTypeText.text = intentType

        when (intentType) {
            "OPEN_APP_FEATURE" -> handleOpenAppFeature(intent)
            "GET_RECORD" -> handleGetRecord(intent)
            else -> showUnknownIntent()
        }
    }

    private fun handleGetRecord(intent: Intent) {
        // Extract module and name from intent or URI parameters
        val module =
            intent.getStringExtra("module") ?: intent.data?.getQueryParameter("module") ?: "unknown"
        val name =
            intent.getStringExtra("name") ?: intent.data?.getQueryParameter("name") ?: "unknown"

        // Get the proper module display name
        val moduleName = getModuleDisplayName(module)

        // Update UI
        moduleText.text = moduleName
        actionText.text = getString(R.string.action_search)
        queryText.text = name
        queryContainer.visibility = View.VISIBLE
    }

    private fun getModuleDisplayName(moduleKey: String): String {
        return when (moduleKey.lowercase(Locale.ROOT)) {
            "deal", "deals" -> getString(R.string.module_deals)
            "contact", "contacts" -> getString(R.string.module_contacts)
            "company", "companies" -> getString(R.string.module_companies)
            "ticket", "tickets" -> getString(R.string.module_tickets)
            "quote", "quotes" -> getString(R.string.module_quotes)
            "activity", "activities", "task", "tasks" -> getString(R.string.module_activities)
            else -> moduleKey.capitalize()
        }
    }

    private fun handleOpenAppFeature(intent: Intent) {
        // Extract the module name from the feature parameter
        val feature = intent.getStringExtra("feature") ?: return

        // Convert feature name to proper module name
        val moduleName = getModuleNameFromFeature(feature)

        moduleText.text = moduleName
        actionText.text = "Listing"
        queryContainer.visibility = View.GONE

        // In a real app, we would navigate to the appropriate listing screen
        // navigateToModule(moduleName, isSearch = false)
    }

    private fun getModuleNameFromFeature(feature: String): String {
        return when {
            feature.contains("deal", ignoreCase = true) -> getString(R.string.module_deals)
            feature.contains("contact", ignoreCase = true) -> getString(R.string.module_contacts)
            feature.contains("company", ignoreCase = true) ||
                    feature.contains(
                        "companies",
                        ignoreCase = true
                    ) -> getString(R.string.module_companies)

            feature.contains("ticket", ignoreCase = true) -> getString(R.string.module_tickets)
            feature.contains("quote", ignoreCase = true) ||
                    feature.contains(
                        "activity",
                        ignoreCase = true
                    ) -> getString(R.string.module_quotes)

            feature.contains("activities", ignoreCase = true) ||
                    feature.contains(
                        "task",
                        ignoreCase = true
                    ) -> getString(R.string.module_activities)

            feature.contains("quote", ignoreCase = true) -> getString(R.string.module_quotes)

            else -> feature.capitalize()
        }
    }

    private fun showRawIntentData(intent: Intent) {
        val action = intent.action ?: "null"
        val data = intent.dataString ?: "null"
        val extras = if (intent.extras != null) {
            intent.extras!!.keySet().joinToString("\n") { key ->
                "$key=${intent.extras!!.get(key)}"
            }
        } else {
            "null"
        }

        // Remove the leading indentation from the multiline string
        rawIntentText.text = getString(R.string.action_data_extras, action, data, extras)

        // Set text alignment explicitly in code as well
        rawIntentText.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }

    private fun showNoIntentState() {
        intentTypeText.text = getString(R.string.none)
        moduleText.text = getString(R.string.none)
        actionText.text = getString(R.string.none)
        queryContainer.visibility = View.GONE
        rawIntentText.text = getString(R.string.no_intent_data_available)
    }

    private fun showUnknownIntent() {
        intentTypeText.text = getString(R.string.unknown)
        moduleText.text = getString(R.string.unknown)
        actionText.text = getString(R.string.unknown)
        queryContainer.visibility = View.GONE
    }

    // Extension function to capitalize strings
    private fun String.capitalize(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}