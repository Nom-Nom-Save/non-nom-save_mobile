package ua.nure.nomnomsave.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import ua.nure.nomnomsave.di.AppModule.DATA_STORE_KEY
import javax.inject.Qualifier
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_KEY)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    const val DATA_STORE_KEY = "settings"

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> =
        context.dataStore

    @Provides
    @DbDeliveryDispatcher
    @Singleton
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun providesDbDeliveryDispatcher(): CloseableCoroutineDispatcher =
        newSingleThreadContext("dbDeliveryDispatcher")
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DbDeliveryDispatcher