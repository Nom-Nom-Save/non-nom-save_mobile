package ua.nure.nomnomsave.ui.establishmentDetails

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.ItemDetailsEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.PriceDataEntity
import ua.nure.nomnomsave.db.data.entity.CartItemEntity
import ua.nure.nomnomsave.ui.compose.NNSButton
import ua.nure.nomnomsave.ui.compose.NNSMenuBottomSheet
import ua.nure.nomnomsave.ui.compose.NNSMenuCard
import ua.nure.nomnomsave.ui.compose.NNSReviewBottomSheet
import ua.nure.nomnomsave.ui.compose.NNSReviewCard
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.theme.AppTheme
import android.util.Log

enum class DetailsTab {
    DETAILS, REVIEWS
}

@Composable
fun EstablishmentDetailsScreen(
    viewModel: EstablishmentDetailsViewModel,
    navController: NavController,
    cartViewModel: ua.nure.nomnomsave.ui.cart.CartViewModel
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                EstablishmentDetails.Event.OnBack -> navController.navigateUp()
                is EstablishmentDetails.Event.OnNavigate -> navController.navigate(route = it.route)
                is EstablishmentDetails.Event.OnAddToCart -> {
                    val menuItem = it.menuItem
                    val establishment = state.establishment
                    
                    val orderDetail = ua.nure.nomnomsave.db.data.entity.OrderDetailsEntity(
                        id = java.util.UUID.randomUUID().toString(),
                        orderId = "",
                        menuPriceId = menuItem.priceData?.id ?: "",
                        quantity = it.quantity,
                        price = menuItem.priceData?.discountPrice ?: menuItem.priceData?.originalPrice ?: 0.0,
                        originalPrice = menuItem.priceData?.originalPrice ?: 0.0,
                        discountPrice = menuItem.priceData?.discountPrice ?: 0.0,
                        itemName = menuItem.itemDetails?.name ?: "Unknown",
                        itemType = menuItem.itemType ?: "Product",
                        itemPicture = menuItem.itemDetails?.picture,
                        weight = menuItem.itemDetails?.weightInfo?.filter { c -> c.isDigit() }?.toIntOrNull() ?: 0,
                        minWeight = null,
                        maxWeight = null
                    )
                    
                    val cartItem = ua.nure.nomnomsave.ui.cart.LocalCartItem(
                        detail = orderDetail,
                        establishmentName = establishment?.name ?: "Unknown",
                        establishmentAddress = establishment?.adress,
                        establishmentLogo = establishment?.logo,
                        establishmentBanner = establishment?.banner,
                        allergens = menuItem.itemDetails?.allergens ?: emptyList(),
                        expiresAt = java.time.LocalDateTime.now().plusHours(2)
                    )
                    
                    cartViewModel.onAction(
                        ua.nure.nomnomsave.ui.cart.Cart.Action.OnAddToLocalCart(cartItem)
                    )
                }
            }
        }
    }

    EstablishmentDetailsScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun EstablishmentDetailsScreen(
    state: EstablishmentDetails.State,
    onAction: (EstablishmentDetails.Action) -> Unit
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(DetailsTab.DETAILS) }
    var selectedMenuItem by remember { mutableStateOf<MenuEntity?>(null) }

    val myReview = remember(state.reviews) { state.reviews.find { it.isMyReview } }
    val isLimitReached = myReview != null && myReview.isEditable

    NNSScreen {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.color.background)
        ) {

            if (LocalInspectionMode.current) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    model = R.drawable.placeholder_image,
                    contentDescription = "Establishment Photo",
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp),
                    model = state.establishment?.banner ?: state.establishment?.logo,
                    contentDescription = "Establishment Photo",
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = if (selectedTab == DetailsTab.REVIEWS) 120.dp else 0.dp)
            ) {
                Spacer(modifier = Modifier.height(280.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = AppTheme.dimension.extralarge, topEnd = AppTheme.dimension.extralarge))
                        .background(color = AppTheme.color.background)
                        .padding(horizontal = AppTheme.dimension.normal, vertical = AppTheme.dimension.large)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.establishment?.name ?: "-",
                                style = AppTheme.typography.large
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = state.establishment?.rating ?: "0.0",
                                style = AppTheme.typography.regular.copy(
                                    color = AppTheme.color.active,
                                    fontWeight = FontWeight.Medium
                                )
                            )
                            Icon(
                                modifier = Modifier
                                    .size(AppTheme.dimension.iconSize)
                                    .padding(AppTheme.dimension.extraSmall),
                                painter = painterResource(R.drawable.star_rate),
                                tint = AppTheme.color.active,
                                contentDescription = null
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.dimension.normal))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TabItem(
                            title = stringResource(R.string.details),
                            isSelected = selectedTab == DetailsTab.DETAILS,
                            onClick = { selectedTab = DetailsTab.DETAILS }
                        )

                        Spacer(modifier = Modifier.width(AppTheme.dimension.large))

                        TabItem(
                            title = stringResource(R.string.reviews),
                            isSelected = selectedTab == DetailsTab.REVIEWS,
                            onClick = { selectedTab = DetailsTab.REVIEWS }
                        )
                    }

                    if (selectedTab == DetailsTab.DETAILS) {
                        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)) {
                            InfoSection(
                                modifier = Modifier.padding(top = AppTheme.dimension.large),
                                title = stringResource(R.string.openingHours),
                                content = formatWorkingHours(
                                    rawHours = state.establishment?.workingHours,
                                )
                            )
                            InfoSection(
                                title = stringResource(R.string.address),
                                content = state.establishment?.adress ?: "-"
                            )
                            InfoSection(
                                title = stringResource(R.string.information),
                                content = state.establishment?.description ?: "-"
                            )
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = AppTheme.dimension.large),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)
                        ) {
                            state.menu?.forEach { menuItem ->
                                NNSMenuCard(
                                    modifier = Modifier.clickable {
                                        selectedMenuItem = menuItem
                                    },
                                    title = menuItem.itemDetails?.name ?: "",
                                    url = menuItem.itemDetails?.picture ?: "",
                                    collectTill = menuItem.priceData?.endTime ?: "",
                                    allergens = !menuItem.itemDetails?.allergens.isNullOrEmpty(),
                                    grams = menuItem.itemDetails?.weightInfo?.filter { it.isDigit() }?.toIntOrNull() ?: 0,
                                    picture = menuItem.itemDetails?.picture ?: ""
                                )
                            }
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = AppTheme.dimension.large),
                            verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)
                        ) {

                            if (state.reviews.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.noReviews),
                                    style = AppTheme.typography.regular.copy(color = AppTheme.color.grey),
                                    modifier = Modifier.padding(vertical = AppTheme.dimension.normal)
                                )
                            } else {
                                state.reviews.forEach { review ->
                                    NNSReviewCard(
                                        name = review.userName,
                                        date = review.createdAt,
                                        rating = review.rating,
                                        text = review.comment,
                                        isMyReview = review.isMyReview,
                                        isEditable = review.isEditable,
                                        onEdit = {
                                            onAction(EstablishmentDetails.Action.OnOpenReviewSheet(review = review))
                                        },
                                        onDelete = {
                                            onAction(EstablishmentDetails.Action.OnDeleteReview(review.id))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(AppTheme.dimension.large))

                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimension.normal),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                IconButton(
                    modifier = Modifier
                        .background(color = AppTheme.color.background.copy(alpha = 0.7f), shape = CircleShape),
                    onClick = { onAction(EstablishmentDetails.Action.OnBack) }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(AppTheme.dimension.iconSize)
                            .clip(shape = CircleShape)
                            .padding(AppTheme.dimension.extraSmall),
                        painter = painterResource(R.drawable.arrow_back),
                        tint = AppTheme.color.foreground,
                        contentDescription = null
                    )
                }

                IconButton(
                    modifier = Modifier
                        .background(color = AppTheme.color.background, shape = CircleShape),
                    onClick = { }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(AppTheme.dimension.iconSize)
                            .clip(shape = CircleShape)
                            .padding(AppTheme.dimension.extraSmall),
                        painter = painterResource(R.drawable.favorite_active),
                        tint = AppTheme.color.foreground,
                        contentDescription = null
                    )
                }
            }

            if (selectedTab == DetailsTab.REVIEWS) {
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(AppTheme.color.background.copy(alpha = 0.95f))
                        .padding(AppTheme.dimension.normal),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    NNSButton(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.writeReview),
                        enabled = !isLimitReached,
                        onClick = {
                            onAction(EstablishmentDetails.Action.OnOpenReviewSheet(review = null))
                        }
                    )

                    if (isLimitReached) {
                        Spacer(modifier = Modifier.height(AppTheme.dimension.small))
                        Text(
                            text = stringResource(R.string.oneReview),
                            style = AppTheme.typography.regular.copy(
                                color = AppTheme.color.error,
                            ),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }
            }
        }

        selectedMenuItem?.let { item ->
            NNSMenuBottomSheet(
                menuItem = item,
                onDismiss = { selectedMenuItem = null },
                onReserve = { quantity ->
                    onAction(
                        EstablishmentDetails.Action.OnReserveNow(
                            menuItem = item,
                            quantity = quantity
                        )
                    )
                    selectedMenuItem = null
                }
            )
        }

        if (state.showReviewSheet) {
            NNSReviewBottomSheet(
                initialReview = state.editingReview,
                onDismiss = { onAction(EstablishmentDetails.Action.OnCloseReviewSheet) },
                onSubmit = { rating, comment ->
                    onAction(EstablishmentDetails.Action.OnSubmitReview(rating, comment))
                }
            )
        }
    }
}

@Composable
private fun InfoSection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = AppTheme.typography.large,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = content,
            style = AppTheme.typography.regular.copy(color = AppTheme.color.grey)
        )
    }
}

@Composable
private fun TabItem(
    title: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = AppTheme.typography.regular.copy(
                color = if (isSelected) AppTheme.color.active else AppTheme.color.grey,
                fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .height(2.dp)
                    .width(AppTheme.dimension.iconSize)
                    .background(color = AppTheme.color.active, shape = RoundedCornerShape(1.dp))
            )
        } else {
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}

@Composable
private fun formatWorkingHours(rawHours: String?): String {
    if (rawHours.isNullOrBlank()) return "-"
    val closedText = stringResource(R.string.closed)

    val daysMap = mapOf(
        "mon" to "Monday",
        "tue" to "Tuesday",
        "wed" to "Wednesday",
        "thu" to "Thursday",
        "fri" to "Friday",
        "sat" to "Saturday",
        "sun" to "Sunday"
    )

    return try {
        rawHours.split("|").mapNotNull { dayData ->
            val parts = dayData.split("=")
            if (parts.size == 2) {
                val dayKey = parts[0].trim()
                val timeValue = parts[1].trim()

                val dayName = daysMap[dayKey] ?: dayKey.replaceFirstChar { it.uppercase() }

                val time = if (timeValue.lowercase() == "closed") closedText else timeValue

                "$dayName: $time"
            } else {
                null
            }
        }.joinToString("\n")
    } catch (e: Exception) {
        rawHours
    }
}

@Preview(showSystemUi = true)
@Composable
fun EstablishmentDetailsScreenPreview() {
    AppTheme {
        EstablishmentDetailsScreen(
            state = EstablishmentDetails.State(
                establishment = EstablishmentEntity(
                    id = "1",
                    name = "Golden bakery",
                    workingHours = "08:00 AM - 06:00 PM",
                    adress = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi fringilla sit amet magna vitae tincidunt. Mauris non lectus id lectus aliquam porta sed pretium tellus.",
                    rating = "5",
                    isEmailVerified = true,
                ),
                menu = listOf(
                    MenuEntity(
                        id = "m1",
                        itemDetails = ItemDetailsEntity(
                            name = "Pastry Surprise Box",
                            weightInfo = "200g",
                            allergens = listOf("Gluten", "Nuts")
                        ),
                        priceData = PriceDataEntity(
                            endTime = "19:00"
                        )
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun EstablishmentDetailsScreenDarkPreview() {
    AppTheme {
        EstablishmentDetailsScreen(
            state = EstablishmentDetails.State(
                establishment = EstablishmentEntity(
                    id = "1",
                    name = "Golden bakery",
                    workingHours = "08:00 AM - 06:00 PM",
                    adress = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                    description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi fringilla sit amet magna vitae tincidunt. Mauris non lectus id lectus aliquam porta sed pretium tellus.",
                    rating = "5",
                    isEmailVerified = true
                ),
                menu = listOf(
                    MenuEntity(
                        id = "m1",
                        itemDetails = ItemDetailsEntity(
                            name = "Pastry Surprise Box",
                            weightInfo = "200g",
                            allergens = listOf("Gluten", "Nuts")
                        ),
                        priceData = PriceDataEntity(
                            endTime = "19:00"
                        )
                    )
                )
            ),
            onAction = {}
        )
    }
}