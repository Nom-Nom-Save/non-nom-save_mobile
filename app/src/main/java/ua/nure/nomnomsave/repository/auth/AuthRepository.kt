package ua.nure.nomnomsave.repository.auth

import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.ResponseDto

interface AuthRepository {
    suspend fun register(name: String, email: String, password: String): Result<ResponseDto, DataError>
    suspend fun verifyEmail(email: String, code: String): Result<ResponseDto, DataError>
    suspend fun forgotPassword(email: String): Result<ResponseDto, DataError>
    suspend fun verifyResetCode(email: String, code: String): Result<ResponseDto, DataError>
    suspend fun resetPassword(email: String, newPassword: String): Result<ResponseDto, DataError>
}