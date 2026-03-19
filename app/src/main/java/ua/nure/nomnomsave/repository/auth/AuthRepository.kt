package ua.nure.nomnomsave.repository.auth

import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.ResponseDto
import ua.nure.nomnomsave.repository.auth.dto.LoginDto
import ua.nure.nomnomsave.repository.auth.dto.GoogleLoginDto

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<ResponseDto, DataError>
    suspend fun verifyEmail(email: String, code: String): Result<ResponseDto, DataError>

    //suspend fun googleLogIn(token: String, email: String): Result<GoogleLoginDto, DataError>

    suspend fun login(email: String, password: String): Result<LoginDto, DataError>
}