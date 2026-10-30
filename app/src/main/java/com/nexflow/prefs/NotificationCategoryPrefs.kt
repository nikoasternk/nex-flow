/*
 * Copyright 2026 NexFlow Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nexflow.prefs

import android.content.Context
import androidx.core.content.edit
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * User-defined notification replacement categories, shared across all flows.
 */
object NotificationCategoryPrefs {
    private const val PREFS_NAME = "nexflow_settings"
    private const val KEY_CATEGORIES = "notification_categories"

    fun getAll(context: Context): List<String> {
        val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CATEGORIES, null)
            ?: return emptyList()
        return runCatching { Json.decodeFromString<List<String>>(raw) }
            .getOrDefault(emptyList())
    }

    fun add(context: Context, category: String): List<String> {
        val trimmed = category.trim()
        if (trimmed.isEmpty()) return getAll(context)
        val updated = (getAll(context) + trimmed).distinctBy { it.lowercase() }
        save(context, updated)
        return updated
    }

    fun remove(context: Context, category: String): List<String> {
        val trimmed = category.trim()
        val updated = getAll(context).filterNot { it.equals(trimmed, ignoreCase = true) }
        save(context, updated)
        return updated
    }

    private fun save(context: Context, categories: List<String>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit { putString(KEY_CATEGORIES, Json.encodeToString(categories)) }
    }
}
