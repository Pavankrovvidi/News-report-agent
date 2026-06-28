package com.purevibe.newsagent.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.purevibe.newsagent.agents.AgentRegistry
import com.purevibe.newsagent.ai.AiClient
import com.purevibe.newsagent.ai.AiException
import com.purevibe.newsagent.ai.ApiKeyStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentReportScreen(agentId: String?, onBack: () -> Unit, onSettings: () -> Unit) {
    val context = LocalContext.current
    val agent = remember(agentId) { AgentRegistry.byId(agentId) }
    val client = remember { AiClient(ApiKeyStore(context)) }
    val scope = rememberCoroutineScope()

    var loading by remember { mutableStateOf(false) }
    var report by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun load() {
        val a = agent ?: return
        loading = true; error = null; report = ""
        scope.launch {
            try {
                report = a.run(client)
            } catch (e: AiException) {
                error = e.message
            } catch (e: Exception) {
                error = "Could not load the report. Check your internet connection."
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(agentId) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(agent?.title ?: "Report") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { load() }, enabled = !loading) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                loading -> Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 64.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator()
                    Text("Gathering the latest report…", style = MaterialTheme.typography.bodyMedium)
                }

                error != null -> Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = onSettings) { Text("Open Settings") }
                    Button(onClick = { load() }) { Text("Try again") }
                }

                else -> Text(report, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(Modifier.size(24.dp))
        }
    }
}
