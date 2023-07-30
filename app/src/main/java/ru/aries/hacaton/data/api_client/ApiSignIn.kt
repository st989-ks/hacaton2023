package ru.aries.hacaton.data.api_client


import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess
import ru.aries.hacaton.base.util.logE
import ru.aries.hacaton.models.api.BidApprovalResponse
import ru.aries.hacaton.models.api.BidCallbackBody
import ru.aries.hacaton.models.api.GettingBidByBank
import ru.aries.hacaton.models.api.GettingOffer
import ru.aries.hacaton.models.api.LoginData
import ru.aries.hacaton.models.api.RudApi
import ru.aries.hacaton.models.api.TokenWithUser

class ApiSignIn(
    private val client: Client,
) {

    suspend fun postSignIn(
        email: String,
        password: String,
    ): RudApi<TokenWithUser> {
        return try {
            val response = client.api.post(
                urlString = "/api/sign-in/email-password/",
                block = {
                    setBody(LoginData(email = email, password = password))
                }
            )
            val data = response.body<RudApi<TokenWithUser>>()
            data.isSuccess = response.status.isSuccess()
            data
        } catch (e: Exception) {
            logE("postSignIn", e.message)
            e.printStackTrace()
            RudApi.getError(description = e.message, errorCode = e.hashCode())
        }
    }
    suspend fun getBids(): RudApi<List<GettingBidByBank>> {
        return try {
            val response = client.api.get(urlString = "/api/cp/bids/")
            response.body<RudApi<List<GettingBidByBank>>>().copy(
                isSuccess = response.status.isSuccess()
            )
        } catch (e: Exception) {
            logE("getBids", e.message)
            e.printStackTrace()
            RudApi.getError(description = e.message, errorCode = e.hashCode())
        }
    }

    suspend fun getOffers(): RudApi<List<GettingOffer>> {
        return try {
            val response = client.api.get(urlString = "/api/cp/offers/")
            response.body<RudApi<List<GettingOffer>>>().copy(
                isSuccess = response.status.isSuccess()
            )
        } catch (e: Exception) {
            logE("getOffers", e.message)
            e.printStackTrace()
            RudApi.getError(description = e.message, errorCode = e.hashCode())
        }
    }


    suspend fun postBidId(
        bid: BidCallbackBody,
        bidId: Int,
    ): RudApi<BidApprovalResponse> {
        return try {
            val response = client.api.post(
                urlString = "/api/cp/bids/${bidId}/process/",
                block = {
                    setBody(bid)
                }
            )
            val data = response.body<RudApi<BidApprovalResponse>>()
            data.isSuccess = response.status.isSuccess()
            data
        } catch (e: Exception) {
            logE("postBidId", e.message)
            e.printStackTrace()
            RudApi.getError(description = e.message?.take(100), errorCode = e.hashCode())
        }
    }

}