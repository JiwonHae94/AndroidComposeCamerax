package com.jwhae.camerax_jetpack_compose.dao.config

import androidx.compose.runtime.Composable
import androidx.datastore.preferences.core.Preferences
import java.lang.NullPointerException
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppConfig private constructor(context:Context){
    enum class Option(val key :  Preferences.Key<Boolean>, val description : String? = null){
        DEBUG_ENABLE(key = booleanPreferencesKey("is_debug_enabled"), description = null)
    }

    private val dataStore : DataStore<Preferences> = context.createDataStore(name = "ApplicationConfig")

    private fun <T> getConfigFlow(key : Preferences.Key<T>): Flow<T>{
        return dataStore.data.catch {
                exception ->
            if(exception is IOException){
                emit(emptyPreferences())
            }else{
                throw exception
            }
        }.map { preference ->
            preference[key] ?: throw NullPointerException("key not found")
        }
    }

    suspend fun updateUserConfiguration(config : Option, value : Boolean){
        dataStore.edit { preference ->
            preference[config.key] = value
        }
    }

    companion object{
        private var appConfig : AppConfig? = null
        private val DEBUG_ENABLED = booleanPreferencesKey(Option.DEBUG_ENABLE.name)

        fun get() : AppConfig{
            appConfig ?: throw NullPointerException("Configuration has not been initialized. Please initialize before usaage")
            return appConfig!!
        }

        fun init(context:Context){
            appConfig = AppConfig(context)
        }
    }
}