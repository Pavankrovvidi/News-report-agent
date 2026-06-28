package com.purevibe.newsagent.agents

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.purevibe.newsagent.ai.AiClient

/**
 * One news agent = one category. Each agent has a system role and a prompt that
 * asks the AI for a concise report of recent news in its category.
 */
data class NewsAgent(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val color: Color,
    private val systemRole: String,
    private val prompt: String
) {
    suspend fun run(client: AiClient): String = client.generate(prompt, systemRole)
}

/** The five agents shown on the home screen. */
object AgentRegistry {

    val agents: List<NewsAgent> = listOf(
        NewsAgent(
            id = "sports",
            title = "Sports Agent",
            subtitle = "Latest sports news",
            icon = Icons.Filled.SportsSoccer,
            color = Color(0xFF2E7D32),
            systemRole = "You are a sports news reporter who writes clear, factual briefs.",
            prompt = report("sports (cricket, football, and other major sports)")
        ),
        NewsAgent(
            id = "ai",
            title = "AI News Agent",
            subtitle = "Latest AI & tech news",
            icon = Icons.Filled.SmartToy,
            color = Color(0xFF1565C0),
            systemRole = "You are a technology reporter specialising in artificial intelligence.",
            prompt = report("artificial intelligence and technology")
        ),
        NewsAgent(
            id = "politics",
            title = "Politics Agent",
            subtitle = "Latest political news",
            icon = Icons.Filled.Gavel,
            color = Color(0xFF6A1B9A),
            systemRole = "You are a neutral political news reporter. Present facts, not opinions.",
            prompt = report("politics and current affairs")
        ),
        NewsAgent(
            id = "stocks",
            title = "Stock Market Agent",
            subtitle = "Latest market news",
            icon = Icons.Filled.TrendingUp,
            color = Color(0xFFC62828),
            systemRole = "You are a financial markets reporter. Be precise and avoid investment advice.",
            prompt = report("the stock market and economy") +
                "\n\nEnd with a one-line reminder that this is information only, not financial advice."
        ),
        NewsAgent(
            id = "movies",
            title = "Movies Agent",
            subtitle = "Latest movie news",
            icon = Icons.Filled.Movie,
            color = Color(0xFFE65100),
            systemRole = "You are an entertainment reporter covering film releases and movie news.",
            prompt = report("movies, new releases, and the film industry")
        )
    )

    fun byId(id: String?): NewsAgent? = agents.firstOrNull { it.id == id }

    private fun report(topic: String): String =
        "Give me a report of the 5 most important recent news items about $topic. " +
            "For each item: write a short bold headline on its own line, then a 1–2 sentence summary. " +
            "Number them 1 to 5. Keep it concise and easy to read on a phone."
}
