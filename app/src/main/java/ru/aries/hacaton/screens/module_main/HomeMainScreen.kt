package ru.aries.hacaton.screens.module_main

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import ru.aries.hacaton.R
import ru.aries.hacaton.base.common_composable.BoxImageLoad
import ru.aries.hacaton.base.common_composable.BoxImageRowRes
import ru.aries.hacaton.base.common_composable.BoxSpacer
import ru.aries.hacaton.base.common_composable.ButtonAccentApp
import ru.aries.hacaton.base.common_composable.DialogBackPressExit
import ru.aries.hacaton.base.common_composable.FloatingActionButtonApp
import ru.aries.hacaton.base.common_composable.IconApp
import ru.aries.hacaton.base.common_composable.IconButtonApp
import ru.aries.hacaton.base.common_composable.TextBodyLarge
import ru.aries.hacaton.base.common_composable.TextBodyMedium
import ru.aries.hacaton.base.common_composable.TextTitleMedium
import ru.aries.hacaton.base.common_composable.colorsButtonAccentApp
import ru.aries.hacaton.base.common_composable.colorsIconButtonApp
import ru.aries.hacaton.base.extension.clickableRipple
import ru.aries.hacaton.base.extension.toDateString
import ru.aries.hacaton.base.res.DimApp
import ru.aries.hacaton.base.res.TextApp
import ru.aries.hacaton.base.theme.ThemeApp
import ru.aries.hacaton.base.util.getQualifiedName
import ru.aries.hacaton.base.util.rememberImageRaw
import ru.aries.hacaton.base.util.rememberModel
import ru.aries.hacaton.models.api.Gender
import ru.aries.hacaton.models.api.GettingBidByBank
import ru.aries.hacaton.models.api.GettingOffer

class HomeMainScreen() : Screen {
    override val key: ScreenKey = keyName

    companion object {
        val keyName = object {}::class.getQualifiedName
    }

    @Composable
    override fun Content() {
        val model = rememberModel<HomeMainModel>()
        val activity = (LocalContext.current as? Activity)
        val status by model.status.collectAsState()
        val user by model.user.collectAsState()
        val bid by model.bid.collectAsState()
        val offers by model.offers.collectAsState()

        DialogBackPressExit()
        MainScreen(
            status = status,
            bid = bid,
            offers = offers,
            onClickLogout = { activity?.finish() },
            onClickNotifications = {},
            avatar = user.avatar ?: "",
            name = user.first_name ?: "",
            onClickUser = {},
            onClickAddNewOffer = model::loadData,
            onClickApproval = model::setAccept,
            onClickMenu = model::setNewtScreen,
        )
    }
}

@Composable
private fun MainScreen(
    status: ScreenChoose,
    bid: List<GettingBidByBank>,
    offers: List<GettingOffer>,
    onClickLogout: () -> Unit,
    onClickNotifications: () -> Unit,
    onClickAddNewOffer: () -> Unit,
    avatar: String,
    name: String,
    onClickUser: () -> Unit,
    onClickApproval: (
        isAccepted: Boolean,
        bidId: Int,
        actualAmount: Int,
        annualPayment: Int,
        percent: Int,
    ) -> Unit,
    onClickMenu: (ScreenChoose) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {


        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(ThemeApp.colors.background)

        ) {
            TopPanel(
                status = status,
                onClickLogout = onClickLogout,
                onClickNotifications = onClickNotifications,
            )
            Row(
                modifier = Modifier.weight(1f)
            ) {
                RightPanel(
                    avatar = avatar,
                    status = status,
                    name = name,
                    onClickUser = onClickUser,
                    onClickMenu = onClickMenu,
                )

                when (status) {
                    ScreenChoose.FIRST -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(DimApp.screenPadding),
                        verticalArrangement = Arrangement.spacedBy(DimApp.screenPadding),
                        content = {
                            items(items = offers, key = { it.id }) { item ->
                                ItemOffers(
                                    title = item.title ?: "",
                                    minPrice = item.min_price?.toString() ?: "",
                                    id = item.id.toString(),
                                    maxPrice = item.max_price?.toString() ?: "",
                                    percent = item.percent?.let { it / 100f }?.toString() ?: "",
                                    annualPayment = item.annual_payment?.toString() ?: "",
                                    paymentTerm = item.payment_term?.toString() ?: "",
                                    nameBank = item.bank?.name ?: "",
                                    urlBank = item.bank?.url ?: "",
                                    iconBank = item.bank?.icon ?: "",
                                    idBank = item.bank?.id?.toString() ?: "",
                                )
                            }
                        })


                    ScreenChoose.SECOND -> LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(DimApp.screenPadding),
                        verticalArrangement = Arrangement.spacedBy(DimApp.screenPadding),
                        content = {
                            items(items = bid, key = { it.id }) { item ->

                                ItemBid(
                                    desiredAmount = item.desired_amount?.toString() ?: "",
                                    idBid = item.id.toString(),
                                    isAccepted = item.is_accepted,
                                    actualAmount = item.actual_amount?.toString() ?: "",
                                    annualPayment = item.annual_payment?.toString() ?: "",
                                    percent = item.percent?.let { it / 100f }?.toString() ?: "",
                                    created = item.created?.toDateString() ?: "",
                                    titleOffer = item.offer?.title ?: "",
                                    minPriceOffer = item.offer?.min_price?.toString() ?: "",
                                    idOffer = item.offer?.id.toString(),
                                    maxPriceOffer = item.offer?.max_price?.toString() ?: "",
                                    percentOffer = item.offer?.percent?.toString() ?: "",
                                    annualPaymentOffer = item.offer?.annual_payment?.toString()
                                        ?: "",
                                    paymentTermOffer = item.offer?.payment_term?.toString() ?: "",
                                    firstName = item.user?.first_name?.toString() ?: "",
                                    lastName = item.user?.last_name?.toString() ?: "",
                                    patronymic = item.user?.patronymic?.toString() ?: "",
                                    birthdate = item.user?.birthdate?.toDateString() ?: "",
                                    gender = item.user?.convertInEnumGender(),
                                    onClickAccepted = {
                                        onClickApproval.invoke(
                                            true,
                                            item.id,
                                            item.actual_amount ?: 1,
                                            item.offer?.annual_payment ?: 1,
                                            item.offer?.percent ?: 1,
                                        )
                                    },
                                    onClickNoAccepted = {
                                        onClickApproval.invoke(
                                            false,
                                            item.id,
                                            item.desired_amount ?: 1,
                                            item.offer?.annual_payment ?: 1,
                                            item.offer?.percent ?: 1,
                                        )
                                    },
                                )
                            }
                        })

                }
            }
        }

        FloatingActionButtonApp(
            modifier = Modifier
                .systemBarsPadding()
                .align(Alignment.BottomEnd)
                .padding(DimApp.screenPadding),
            onClick = onClickAddNewOffer
        ) {
            IconApp(
                painter = rememberImageRaw(id = R.raw.ic_cached)
            )
        }

    }
}

@Composable
private fun ItemOffers(
    title: String,
    minPrice: String,
    id: String,
    maxPrice: String,
    percent: String,
    annualPayment: String,
    paymentTerm: String,
    nameBank: String,
    urlBank: String,
    iconBank: String,
    idBank: String,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = DimApp.shadowElevation,
                shape = ThemeApp.shape.mediumAll
            )
            .background(ThemeApp.colors.backgroundVariant)
            .padding(DimApp.screenPadding)
    ) {

        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoxImageLoad(
                modifier = Modifier.height(DimApp.iconSizeBig),
                contentScale = ContentScale.FillHeight,
                drawableError = R.drawable.stab_avatar,
                drawablePlaceholder = R.drawable.stab_avatar,
                image = iconBank
            )
            BoxSpacer()
            TextTitleMedium(text = "Банк $nameBank")
        }
        TextTitleMedium(text = "№$id $title, от $percent %")
        BoxSpacer()
        TextBodyMedium(text = "Минимальная сумма: $minPrice")
        TextBodyMedium(text = "Maксимальная сумма: $maxPrice")
        TextBodyMedium(text = "Ежегодный платеж: $annualPayment")
        TextBodyMedium(text = "Срок оплаты: $paymentTerm")
    }
}

@Composable
private fun ItemBid(
    desiredAmount: String,
    idBid: String,
    isAccepted: Boolean?,
    actualAmount: String,
    annualPayment: String,
    percent: String,
    created: String,
    titleOffer: String,
    minPriceOffer: String,
    idOffer: String,
    maxPriceOffer: String,
    percentOffer: String,
    annualPaymentOffer: String,
    paymentTermOffer: String,
    firstName: String,
    lastName: String,
    patronymic: String,
    birthdate: String,
    gender: Gender?,
    onClickAccepted: () -> Unit,
    onClickNoAccepted: () -> Unit,

    ) {
    val colorAccepted = when (isAccepted) {
        true -> ThemeApp.colors.goodContent
        false -> ThemeApp.colors.attentionContent
        null -> ThemeApp.colors.textDark
    }

    val textAccepted = when (isAccepted) {
        true -> "Одобрено"
        false -> "Отказано"
        null -> "В ожидании"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = DimApp.shadowElevation,
                shape = ThemeApp.shape.mediumAll
            )
            .background(ThemeApp.colors.backgroundVariant)
            .padding(DimApp.screenPadding)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextTitleMedium(text = "Заявка №$idBid")
            BoxSpacer()
            TextTitleMedium(text = created)
            BoxSpacer()
            TextBodyLarge(
                color = colorAccepted,
                text = textAccepted
            )
        }
        TextBodyMedium(text = "Имя: $firstName")
        TextBodyMedium(text = "Фамилия: $lastName")
        TextBodyMedium(text = "Отчество: $patronymic")
        TextBodyMedium(text = "Дата рождения: $birthdate")
        TextBodyMedium(text = "Пол: ${gender?.getGenderText() ?: ""}")

        BoxSpacer()
        TextBodyMedium(text = "Список документов: ")
        BoxSpacer()
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(5) {
                BoxImageLoad(
                    modifier = Modifier
                        .size(DimApp.iconStubBig)
                        .clip(ThemeApp.shape.smallAll)
                        .clickableRipple { },
                    image = R.drawable.inn
                )
                BoxSpacer()
            }
        }
        BoxSpacer()
        if (isAccepted == null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {

                ButtonAccentApp(
                    modifier = Modifier
                        .padding(horizontal = DimApp.screenPadding),
                    colors = colorsButtonAccentApp().copy(
                        containerColor = ThemeApp.colors.attentionContent,
                        contentColor = ThemeApp.colors.onPrimary
                    ),
                    onClick = onClickNoAccepted,
                    text = "Отказать"
                )
                BoxSpacer()
                ButtonAccentApp(
                    modifier = Modifier
                        .padding(horizontal = DimApp.screenPadding),
                    onClick = onClickAccepted,
                    text = "Одобрить"
                )
            }
        }
    }
}

@Composable
private fun RightPanel(
    status: ScreenChoose,
    avatar: String,
    name: String,
    onClickUser: () -> Unit,
    onClickMenu: (ScreenChoose) -> Unit
) {
    val firstColor = if (status == ScreenChoose.FIRST) {
        colorsIconButtonApp().copy(contentColor = ThemeApp.colors.primary)
    } else {
        colorsIconButtonApp()
    }
    val secondColor = if (status == ScreenChoose.SECOND) {
        colorsIconButtonApp().copy(contentColor = ThemeApp.colors.primary)
    } else {
        colorsIconButtonApp()
    }

    Column(
        Modifier
            .fillMaxHeight()
            .width(DimApp.heightNavigationBottomBar)
            .background(ThemeApp.colors.backgroundVariant),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxSpacer()
        BoxImageLoad(
            modifier = Modifier
                .size(DimApp.iconSizeTouchBig)
                .clip(CircleShape)
                .clickableRipple { onClickUser.invoke() },
            drawableError = R.drawable.stab_avatar,
            drawablePlaceholder = R.drawable.stab_avatar,
            image = avatar
        )
        BoxSpacer()
        TextBodyMedium(
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Start,
            text = name
        )
        BoxSpacer()

        Column(
            modifier = Modifier.clickableRipple(
                bounded = false,
                radius = DimApp.heightNavigationBottomBar * .5f,
            ) { onClickMenu.invoke(ScreenChoose.FIRST) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconApp(
                tint = firstColor.contentColor,
                painter = rememberImageRaw(R.raw.ic_piggy_bank)
            )
            BoxSpacer(.4f)
            Text(
                text = "Лента",
                style = ThemeApp.typography.button,
                color = firstColor.contentColor
            )
        }
        BoxSpacer()
        Column(
            modifier = Modifier.clickableRipple(
                bounded = false,
                radius = DimApp.heightNavigationBottomBar * .5f,
            ) { onClickMenu.invoke(ScreenChoose.SECOND) },
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            IconApp(
                tint = secondColor.contentColor,
                painter = rememberImageRaw(R.raw.ic_files)
            )
            BoxSpacer(.4f)
            Text(
                text = "Заявки",
                style = ThemeApp.typography.button,
                color = secondColor.contentColor
            )
        }
    }
}

@Composable
private fun TopPanel(
    status: ScreenChoose,
    onClickLogout: () -> Unit,
    onClickNotifications: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(DimApp.heightNavigationBottomBar)
            .background(ThemeApp.colors.backgroundVariant)
            .padding(horizontal = DimApp.screenPadding),
    ) {

        Row(
            modifier = Modifier
                .align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BoxImageRowRes(
                modifier = Modifier
                    .size(DimApp.iconSizeOrder),
                image = R.raw.ic_piggy_bank,
                colorFilter = ColorFilter.tint(ThemeApp.colors.primary),
            )
            BoxSpacer()
            Text(
                style = ThemeApp.typography.titleLarge.copy(fontSize = DimApp.fontSplashSize),
                text = TextApp.baseTextNameApp,
                color = ThemeApp.colors.primary
            )
        }

        Text(
            modifier = Modifier.align(Alignment.Center),
            textAlign = TextAlign.Center,
            style = ThemeApp.typography.titleMedium,
            text = status.getTextTitle(),
            color = ThemeApp.colors.textDark
        )
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButtonApp(
                modifier = Modifier, onClick = onClickNotifications
            ) {
                IconApp(painter = rememberImageRaw(id = R.raw.ic_notifications))
            }
            BoxSpacer()
        }
    }
}

enum class ScreenChoose() {
    FIRST, SECOND;

    fun getTextTitle() = when (this) {
        FIRST -> "Лента предложений от банка"
        SECOND -> "Заявки пользователей на приобретение кредита"
    }
}
