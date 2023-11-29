package com.running.data.exception

import com.running.data.state.ResponseState
import com.running.domain.util.Constants
import okhttp3.internal.http2.ErrorCode
import retrofit2.Response
import java.io.IOException


inline fun <reified T: Any> Response<T>.errorHandler(): ResponseState<T> {
    return try{
        when(this.code()){
            Constants.CODE_UN_AUTHORIZE -> ResponseState.Error(message = "404 Err")
            Constants.CODE_BAD_REQUEST -> ResponseState.Error(message = "401 Err")
            else->{
                val body = this.body()
                return if(body == null){
                    ResponseState.Error(message = "Unhandled Err")
                }else{
                    ResponseState.Success(data = body)
                }
            }
        }
    }catch (e: IOException){
        ResponseState.Error(message = "Internet Err")
    }catch (t: Throwable){
        ResponseState.Error(message = "Unhandled Err")
    }
}