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
         networkRequest.subscribeOn(Schedulers.io())
             .observeOn(AndroidSchedulers.mainThread())
             .subscribe(object : SingleObserver<BaseResponse<Any>> {
                 override fun onSuccess(t: BaseResponse<Any>) {
                     if (t != null) {
                         onSuccess?.let { it(t.data) }
                         networkResponseObserver.postValue(NetworkResponse(STATUS.SUCCESS, t.data))
                     }
                 }

                 override fun onSubscribe(d: Disposable) {
                 }

                 override fun onError(e: Throwable) {
                     onFailure?.let { it(e.message) }
                     networkResponseObserver.postValue(NetworkResponse(STATUS.ERROR))
                     e.printStackTrace()
                 }

             })
     }

   /* fun startNetworkService(
        networkResponseObserver: MutableLiveData<NetworkResponse>,
        onSuccess: ((Any?) -> Unit)? = null,
        onFailure: ((Any?) -> Unit)? = null,
        networkRequest: Single<List<Any>>
    ) {
        networkRequest.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<List<Any>> {
                override fun onSuccess(t: List<Any>) {
                    if (t != null) {
                        onSuccess?.let { it(t) }
                        networkResponseObserver.postValue(NetworkResponse(STATUS.SUCCESS, t))
                    }
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                    onFailure?.let { it(e.message) }
                    networkResponseObserver.postValue(NetworkResponse(STATUS.ERROR))
                    e.printStackTrace()
                }

            })

    }*/

}