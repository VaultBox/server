package dev.medzik.librepass.client

import dev.medzik.librepass.client.errors.ApiException
import dev.medzik.librepass.client.errors.ClientException
import dev.medzik.librepass.client.utils.JsonUtils
import dev.medzik.librepass.types.api.ResponseError
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

const val DEFAULT_API_URL = "https://librepass-api.medzik.dev"

/**
 * HTTP Client for sending requests to the API
 * @param apiURL api url address
 * @param accessToken access token to use for authorization
 */
class Client(
    private val apiURL: String,
    accessToken: String? = null,
) {
    private val httpClient = OkHttpClient()
    private val httpMediaTypeJson = "application/json; charset=utf-8".toMediaType()

    // create authorization header if access token is provided
    private val authorizationHeader = if (accessToken.isNullOrEmpty()) "" else "Bearer $accessToken"

    /**
     * Send a GET request to the API
     * @param endpoint endpoint of the API
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    fun get(endpoint: String): String {
        val request = Request.Builder()
            .url(apiURL + endpoint)
            .addHeader("Authorization", authorizationHeader)
            .get()
            .build()

        return executeAndExtractBody(request)
    }

    /**
     * Send a DELETE request to the API
     * @param endpoint endpoint of the API
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    fun delete(endpoint: String): String {
        val request = Request.Builder()
            .url(apiURL + endpoint)
            .addHeader("Authorization", authorizationHeader)
            .delete()
            .build()

        return executeAndExtractBody(request)
    }

    /**
     * Send a POST request to the API
     * @param endpoint endpoint of the API
     * @param json JSON body of the request
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    fun post(endpoint: String, json: String): String {
        val body = json.toRequestBody(httpMediaTypeJson)

        val request = Request.Builder()
            .url(apiURL + endpoint)
            .addHeader("Authorization", authorizationHeader)
            .post(body)
            .build()

        return executeAndExtractBody(request)
    }

    /**
     * Send a PATCH request to the API
     * @param endpoint endpoint of the API
     * @param json JSON body of the request
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    fun patch(endpoint: String, json: String): String {
        val body = json.toRequestBody(httpMediaTypeJson)

        val request = Request.Builder()
            .url(apiURL + endpoint)
            .addHeader("Authorization", authorizationHeader)
            .patch(body)
            .build()

        return executeAndExtractBody(request)
    }

    /**
     * Send a PUT request to the API
     * @param endpoint endpoint of the API
     * @param json JSON body of the request
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    fun put(endpoint: String, json: String): String {
        val body = json.toRequestBody(httpMediaTypeJson)

        val request = Request.Builder()
            .url(apiURL + endpoint)
            .addHeader("Authorization", authorizationHeader)
            .put(body)
            .build()

        return executeAndExtractBody(request)
    }

    /**
     * Execute a request and extract the body
     * @param request request to execute
     * @return response body
     */
    @Throws(ClientException::class, ApiException::class)
    private fun executeAndExtractBody(request: Request): String {
        try {
            // send request
            val response = httpClient.newCall(request).execute()

            // extract from response
            val statusCode = response.code
            val body = response.body.string()

            // error handling
            if (statusCode >= 300) {
                throw ApiException(
                    status = statusCode,
                    error = JsonUtils.deserialize<ResponseError>(body).error
                )
            }

            return body
        } catch (e: IOException) {
            throw ClientException(e)
        }
    }
}
