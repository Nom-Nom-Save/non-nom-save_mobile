package ua.nure.nomnomsave.repository.menu

import kotlinx.coroutines.flow.Flow
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.repository.DataError
import ua.nure.nomnomsave.repository.Result
import ua.nure.nomnomsave.repository.dto.MenuDto
import ua.nure.nomnomsave.repository.dto.MenuResponseDto

interface MenuRepository {
    suspend fun fetchMenu(establishmentId: String): Result<MenuResponseDto, DataError>

    suspend fun fetchMenuItem(menuItemId: String): Result<MenuDto, DataError>

    suspend fun getMenuByEstablishment(establishmentId: String): Flow<List<MenuEntity>>

    suspend fun getMenuItem(menuItemId: String): Flow<MenuEntity>
}