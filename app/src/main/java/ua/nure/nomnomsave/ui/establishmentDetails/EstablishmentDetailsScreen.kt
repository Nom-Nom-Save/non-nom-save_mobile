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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import ua.nure.nomnomsave.R
import ua.nure.nomnomsave.db.data.entity.EstablishmentEntity
import ua.nure.nomnomsave.db.data.entity.ItemDetailsEntity
import ua.nure.nomnomsave.db.data.entity.MenuEntity
import ua.nure.nomnomsave.db.data.entity.PriceDataEntity
import ua.nure.nomnomsave.ui.compose.NNSMenuBottomSheet
import ua.nure.nomnomsave.ui.compose.NNSMenuCard
import ua.nure.nomnomsave.ui.compose.NNSScreen
import ua.nure.nomnomsave.ui.theme.AppTheme

enum class DetailsTab {
    DETAILS, REVIEWS
}

@Composable
fun EstablishmentDetailsScreen(
    viewModel: EstablishmentDetailsViewModel,
    navController: NavController
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.event.collect {
            when (it) {
                EstablishmentDetails.Event.OnBack -> navController.navigateUp()
                is EstablishmentDetails.Event.OnNavigate -> navController.navigate(route = it.route)
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

    NNSScreen {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.color.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp)
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = state.establishment?.banner,
                    contentDescription = "Establishment Photo",
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    modifier = Modifier
                        .padding(AppTheme.dimension.normal)
                        .align(Alignment.TopStart)
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
                        .padding(AppTheme.dimension.normal)
                        .align(Alignment.TopEnd)
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

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
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
                                text = state.establishment?.name ?: "Unknown name",
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
                                content = state.establishment?.workingHours ?: "Not specified"
                            )
                            InfoSection(
                                title = stringResource(R.string.address),
                                content = state.establishment?.adress ?: "Not specified"
                            )
                            InfoSection(
                                title = stringResource(R.string.information),
                                content = state.establishment?.description ?: "No description available."
                            )
                        }
                    } else {
                        Text(
                            text = "Reviews will be here soon...",
                            style = AppTheme.typography.regular.copy(color = AppTheme.color.grey),
                            modifier = Modifier.padding(vertical = AppTheme.dimension.normal)
                        )
                    }

                    Spacer(modifier = Modifier.height(AppTheme.dimension.large))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppTheme.dimension.large),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimension.normal)
                    ) {
                        state.menu?.forEach { menuItem ->
                            NNSMenuCard(
                                modifier = Modifier.clickable {
                                    selectedMenuItem = menuItem
                                },
                                title = menuItem.itemDetails?.name ?: "Unknown",
                                url = menuItem.itemDetails?.picture ?: "",
                                collectTill = menuItem.priceData?.endTime ?: "N/A",
                                allergens = !menuItem.itemDetails?.allergens.isNullOrEmpty(),
                                grams = menuItem.itemDetails?.weightInfo?.filter { it.isDigit() }?.toIntOrNull() ?: 0,
                                picture = menuItem.itemDetails?.picture ?: ""
                            )
                        }
                    }

                }
            }
        }
        selectedMenuItem?.let { item ->
            NNSMenuBottomSheet(
                menuItem = item,
                onDismiss = { selectedMenuItem = null },
                onReserve = { quantity ->
                    println("Reserved $quantity items!")
                    selectedMenuItem = null
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