package ua.nure.nomnomsave.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.db.DbRepositoryImpl
import ua.nure.nomnomsave.repository.auth.AuthRepository
import ua.nure.nomnomsave.repository.auth.AuthRepositoryImpl
import ua.nure.nomnomsave.repository.establishment.EstablishmentRepository
import ua.nure.nomnomsave.repository.establishment.EstablishmentRepositoryImpl
import ua.nure.nomnomsave.repository.menu.MenuRepository
import ua.nure.nomnomsave.repository.menu.MenuRepositoryImpl
import ua.nure.nomnomsave.repository.order.OrderRepository
import ua.nure.nomnomsave.repository.order.OrderRepositoryImpl
import ua.nure.nomnomsave.repository.profile.ProfileRepository
import ua.nure.nomnomsave.repository.profile.ProfileRepositoryImpl
import ua.nure.nomnomsave.repository.resource.ResourceRepository
import ua.nure.nomnomsave.repository.resource.ResourceRepositoryImpl
import ua.nure.nomnomsave.repository.token.TokenRepository
import ua.nure.nomnomsave.repository.token.TokenRepositoryImpl
import ua.nure.nomnomsave.repository.user.UserRepository
import ua.nure.nomnomsave.repository.user.UserRepositoryImpl
import javax.inject.Singleton


@OptIn(ExperimentalCoroutinesApi::class)
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Provides
    @Singleton
    fun provideDbRepository(
        @ApplicationContext context: Context,
        tokenRepository: TokenRepository
    ): DbRepository = DbRepositoryImpl(
        context = context,
        tokenRepository = tokenRepository
    )

    @Provides
    @Singleton
    fun provideTokenRepository(
        dataStore: DataStore<Preferences>
    ): TokenRepository = TokenRepositoryImpl(
        dataStore = dataStore
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    fun provideAuthRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
        tokenRepository: TokenRepository,
    ): AuthRepository = AuthRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository,
        tokenRepository = tokenRepository
    )

    @Provides
    fun provideResourceRepository(
        @ApplicationContext context: Context,
    ): ResourceRepository = ResourceRepositoryImpl(context = context)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Singleton
    @Provides
    fun provideProfileRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
    ): ProfileRepository = ProfileRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository
    )

    @Provides
    fun provideOrderRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
    ): OrderRepository = OrderRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    @Provides
    fun provideUserRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
    ): UserRepository = UserRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository
    )

    @Provides
    fun provideEstablishmentRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
    ): EstablishmentRepository = EstablishmentRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository
    )

    @Provides
    fun provideMenuRepository(
        httpClient: HttpClient,
        @DbDeliveryDispatcher dbDeliveryDispatcher: CloseableCoroutineDispatcher,
        dbRepository: DbRepository,
    ): MenuRepository = MenuRepositoryImpl(
        httpClient = httpClient,
        dbDeliveryDispatcher = dbDeliveryDispatcher,
        dbRepository = dbRepository
    )
}