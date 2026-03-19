package ua.nure.nomnomsave.repository.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.CloseableCoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.withContext
import ua.nure.nomnomsave.db.DbRepository
import ua.nure.nomnomsave.di.DbDeliveryDispatcher
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.auth.dto.RegisterRequest
import ua.nure.nomnomsave.repository.auth.dto.LoginRequest
import ua.nure.nomnomsave.repository.auth.dto.LoginDto
import ua.nure.nomnomsave.repository.auth.dto.VerifyCodeRequest
import ua.nure.nomnomsave.repository.dto.ResponseDto
import ua.nure.nomnomsave.repository.onSuccess
import ua.nure.nomnomsave.repository.safeCall
import ua.nure.nomnomsave.repository.token.TokenRepository

class AuthRepositoryImpl @OptIn(ExperimentalCoroutinesApi::class) constructor(
    private val httpClient: HttpClient,
    private val dbRepository: DbRepository,
    private val tokenRepository: TokenRepository,
    @DbDeliveryDispatcher private val dbDeliveryDispatcher: CloseableCoroutineDispatcher,
): AuthRepository {
    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): Result<ResponseDto, DataError> = withContext(Dispatchers.IO) {
        safeCall<ResponseDto> {
            httpClient.post("auth/register-user") {
                setBody(
                    body = RegisterRequest(
                        fullName = fullName,
                        email = email,
                        password = password
                    )
                )
            }
        }.onSuccess {  }
    }

    override suspend fun verifyEmail(
        email: String,
        code: String
    ): Result<ResponseDto, DataError> = withContext(Dispatchers.IO) {
        safeCall<ResponseDto> {
            httpClient.post("auth/verify-email") {
                setBody(
                    VerifyCodeRequest(
                        email = email,
                        code = code
                    )
                )
            }
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginDto, DataError> = withContext(Dispatchers.IO) {
        safeCall<LoginDto> {
            httpClient.post("auth/login") {
                setBody(
                    LoginRequest(
                        email = email,
                        password = password,
                        loginType = "user"
                    )
                )
            }
        }.onSuccess {
            tokenRepository.setToken(newToken = it.accessToken)
            tokenRepository.setUserName(newUserName = email)
        }
    }

}