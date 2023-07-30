package ru.aries.hacaton.screens.module_main

import cafe.adriel.voyager.core.model.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.aries.hacaton.base.BaseModel
import ru.aries.hacaton.base.util.logE
import ru.aries.hacaton.data.gDSetLoader
import ru.aries.hacaton.models.api.BidApproval
import ru.aries.hacaton.models.api.BidCallbackBody
import ru.aries.hacaton.models.api.GettingBidByBank
import ru.aries.hacaton.models.api.GettingOffer
import ru.aries.hacaton.models.api.GettingUser
import ru.aries.hacaton.models.api.mockGettingBidByBank
import ru.aries.hacaton.models.api.mockGettingOffer
import ru.aries.hacaton.use_case.UseCaseSignIn

class HomeMainModel(
    private val apiSignIn: UseCaseSignIn
) : BaseModel() {

    private val _status = MutableStateFlow(ScreenChoose.FIRST)
    val status = _status.asStateFlow()

    private val _user = MutableStateFlow(GettingUser())
    val user = _user.asStateFlow()

    private val _offers = MutableStateFlow(listOf<GettingOffer>())
    val offers = _offers.asStateFlow()

    private val _bid = MutableStateFlow(listOf<GettingBidByBank>())
    val bid = _bid.asStateFlow()

    init {
        coroutineScope.launch {
            loadData()
//            while (true){
//                loadData()
//                delay(2000)
//            }
        }
    }

    fun loadData() {
        coroutineScope.launch {
            _bid.value = apiSignIn.getBids().data ?: listOf(mockGettingBidByBank)
            _offers.value = apiSignIn.getOffers().data ?: listOf(mockGettingOffer)
            logE(_offers.value)
            _user.value = apiSignIn.getUserLocalData()
        }
    }

    fun setAccept(
        isAccepted: Boolean,
        bidId: Int,
        actualAmount: Int,
        annualPayment: Int,
        percent: Int,
    ) = coroutineScope.launch {
        apiSignIn.postBidId(
            bid = BidCallbackBody(
                is_accepted = isAccepted,
                approval = BidApproval(
                    actual_amount = actualAmount,
                    annual_payment = annualPayment,
                    percent = percent,
                ),
            ),
            bidId = bidId,
            flowStart = { gDSetLoader(true) },
            flowSuccess = {
                loadData()
                gDSetLoader(false)
            },
            flowError = { gDSetLoader(false) },
            flowMessage = ::message,
        )
    }

    fun setNewtScreen(scr: ScreenChoose) {
        _status.value = scr
    }


}