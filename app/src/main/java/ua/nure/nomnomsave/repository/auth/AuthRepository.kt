package ua.nure.nomnomsave.repository.auth

import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.ResponseDto

interface AuthRepository {
    suspend fun register(fullName: String, email: String, password: String, role: String): Result<ResponseDto, DataError>
}