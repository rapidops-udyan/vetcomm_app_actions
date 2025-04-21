package com.rapidops.vetcomm

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private lateinit var intentTypeText: TextView
    private lateinit var moduleText: TextView
    private lateinit var actionText: TextView
    private lateinit var queryText: TextView
    private lateinit var queryContainer: LinearLayout
    private lateinit var rawIntentText: TextView
    private lateinit var intentCard: CardView
    private lateinit var rawIntentCard: CardView
    private lateinit var fab: FloatingActionButton

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
            intent.hasExtra("query") || intent.hasExtra("@type") ||
                    (intent.data?.getQueryParameter("query") != null) ||
                    (intent.data?.getQueryParameter("@type") != null) -> "GET_THING"
            else -> "UNKNOWN"
        }
        intentTypeText.text = intentType

        when (intentType) {
            "OPEN_APP_FEATURE" -> handleOpenAppFeature(intent)
            "GET_THING" -> handleGetThing(intent)
            else -> showUnknownIntent()
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

    private fun handleGetThing(intent: Intent) {
        // Extract query and type from intent or URI parameters
        val query = intent.getStringExtra("query") ?: intent.data?.getQueryParameter("query") ?: "None"
        val type = intent.getStringExtra("type") ?: intent.data?.getQueryParameter("type") ?: "Unknown"

        // Convert type to proper module name
        val moduleName = getModuleNameFromType(type)

        moduleText.text = moduleName
        actionText.text = "Search Record"
        queryText.text = query
        queryContainer.visibility = View.VISIBLE

        // In a real app, we would navigate to search results
        // navigateToModule(moduleName, isSearch = true, query = query)
    }

    private fun getModuleNameFromFeature(feature: String): String {
        return when {
            feature.contains("deal", ignoreCase = true) -> getString(R.string.module_deals)
            feature.contains("contact", ignoreCase = true) ||
                    feature.contains("people", ignoreCase = true) -> getString(R.string.module_contacts)
            feature.contains("compan", ignoreCase = true) ||
                    feature.contains("business", ignoreCase = true) -> getString(R.string.module_companies)
            feature.contains("ticket", ignoreCase = true) ||
                    feature.contains("issue", ignoreCase = true) -> getString(R.string.module_tickets)
            feature.contains("quote", ignoreCase = true) ||
                    feature.contains("estimate", ignoreCase = true) -> getString(R.string.module_quotes)
            feature.contains("activit", ignoreCase = true) ||
                    feature.contains("task", ignoreCase = true) ||
                    feature.contains("event", ignoreCase = true) -> getString(R.string.module_activities)
            else -> feature.capitalize()
        }
    }

    private fun getModuleNameFromType(type: String): String {
        return when {
            type.contains("deal", ignoreCase = true) ||
                    type.contains("opportunit", ignoreCase = true) -> getString(R.string.module_deals)
            type.contains("contact", ignoreCase = true) ||
                    type.contains("person", ignoreCase = true) ||
                    type.contains("client", ignoreCase = true) -> getString(R.string.module_contacts)
            type.contains("compan", ignoreCase = true) ||
                    type.contains("organization", ignoreCase = true) ||
                    type.contains("business", ignoreCase = true) -> getString(R.string.module_companies)
            type.contains("ticket", ignoreCase = true) ||
                    type.contains("issue", ignoreCase = true) ||
                    type.contains("case", ignoreCase = true) -> getString(R.string.module_tickets)
            type.contains("quote", ignoreCase = true) ||
                    type.contains("estimate", ignoreCase = true) ||
                    type.contains("proposal", ignoreCase = true) -> getString(R.string.module_quotes)
            type.contains("activit", ignoreCase = true) ||
                    type.contains("task", ignoreCase = true) ||
                    type.contains("event", ignoreCase = true) -> getString(R.string.module_activities)
            else -> type.capitalize()
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
        rawIntentText.text = "Action: $action\nData: $data\nExtras: \n$extras"

        // Set text alignment explicitly in code as well
        rawIntentText.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
    }

    private fun showNoIntentState() {
        intentTypeText.text = "None"
        moduleText.text = "None"
        actionText.text = "None"
        queryContainer.visibility = View.GONE
        rawIntentText.text = "No intent data available"
    }

    private fun showUnknownIntent() {
        intentTypeText.text = "Unknown"
        moduleText.text = "Unknown"
        actionText.text = "Unknown"
        queryContainer.visibility = View.GONE
    }

    // Extension function to capitalize strings
    private fun String.capitalize(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }
}