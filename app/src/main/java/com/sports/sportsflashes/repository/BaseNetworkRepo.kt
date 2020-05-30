package com.sports.sportsflashes.repository

import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.sports.sportsflashes.model.BaseResponse
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

abstract class BaseNetworkRepo(gson: Gson) {
    fun startNetworkService(
        networkResponseObserver: MutableLiveData<NetworkResponse>,
        onSuccess: ((Any?) -> Unit)? = null,
        onFailure: ((Any?) -> Unit)? = null,
        networkRequest: Single<BaseResponse<Any>>
    ) {
//        val request = networkRequest()
        networkRequest.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<BaseResponse<Any>> {
                override fun onSuccess(t: BaseResponse<Any>) {

                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })
    }
}