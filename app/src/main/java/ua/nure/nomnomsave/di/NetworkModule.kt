package ua.nure.nomnomsave.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import ua.nure.nomnomsave.repository.token.TokenRepository
import ua.nure.nomnomsave.BuildConfig
import ua.nure.nomnomsave.repository.ApiErrorResponse
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideChuckerInterceptor(
        @ApplicationContext context: Context
    ): ChuckerInterceptor =
        ChuckerInterceptor.Builder(context = context)
            .collector(
                collector = ChuckerCollector(
                    context = context,
                    showNotification = true,
                    retentionPeriod = RetentionManager.Period.ONE_WEEK,
                ),
            )
            .alwaysReadResponseBody(enable = true)
            .redactHeaders("Authorization")
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        chuckerInterceptor: ChuckerInterceptor
    ): OkHttpClient = OkHttpClient.Builder().apply {
        if (BuildConfig.DEBUG) {
            addInterceptor(
                chuckerInterceptor
            )
        }
    }.build()

    @Provides
    @Singleton
    fun provideKtorClient(
        okHttpClient: OkHttpClient,
        tokenRepository: TokenRepository
    ): HttpClient =
        HttpClient(OkHttp) {
            engine {
                preconfigured = okHttpClient
            }
            install(ContentNegotiation){
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            install(HttpTimeout) {
                socketTimeoutMillis = 20_000L
                requestTimeoutMillis = 20_000L
            }

//            install(Logging) {
//                logger = object : io.ktor.client.plugins.logging.Logger {
//                    override fun log(message: String) {
//                        println(message)
//                    }
//                }
//                level = LogLevel.ALL
//            }

            HttpResponseValidator {
                validateResponse { response ->
                    val statusCode = response.status.value

                    if(statusCode < 300) {
                        return@validateResponse
                    }

                    val errorBodyText = try {
                        response.bodyAsText()
                    } catch (e: Exception) {
                        throw ApiErrorException(
                            apiError = ApiError(
                                message = "Failed to read error body: ${e.message}",
                            ),
                            httpStatus = statusCode,
                            cause = e
                        )
                    }

                    try {
                        val apiError = Json.decodeFromString<ApiErrorResponse>(errorBodyText)
                        throw ApiErrorException(
                            apiError = ApiError(
                                message = apiError.message,
                            ),
                            httpStatus = statusCode,
                        )
                        
                    } catch (e: SerializationException) {
                        throw ApiErrorException(
                            apiError = ApiError(
                                message = "Non-JSON error or structure mismatch: $errorBodyText"
                            ),
                            httpStatus = statusCode,
                            cause = e
                        )
                    }
                }
            }

            defaultRequest {
                url("https://non-nom-save-server.fly.dev/api/")
                header(HttpHeaders.ContentType, ContentType.Application.Json)
            }
        }.apply {
            plugin(HttpSend).intercept { request ->
                val token = tokenRepository.token
                if(token != null
                    && !request.url.toString().contains("/auth/register")) {
                    request.headers.append(HttpHeaders.Authorization, "Bearer $token")
                }

                execute(request)
            }
        }
}

data class ApiErrorException(
    val apiError: ApiError,
    val httpStatus: Int,
    override val cause: Throwable? = null
) : Exception("API Error: ${apiError.message}", cause)

data class ApiError(
    val message: String,
)