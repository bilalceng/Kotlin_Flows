package com.bilalberekgm.kotlinflows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FlowsViewModel():ViewModel() {


    val countDownFlow: Flow<Int> =
        flow {
            val startingValue = 10
            var currentValue = startingValue
            while (currentValue > 0){
                delay(1000L)
                emit(currentValue)
                currentValue--
            }
        }


    init {
        collectFlow()
    }

    /**
     *   private fun collectFlow(){
     *         viewModelScope.launch {
     *             countDownFlow.collect{
     *                 println("the current time is: $it")
     *             }
     *         }
     *     }
     */


    // Just write "the current time is: 0" to the logcat
    private fun collectFlow(){
        viewModelScope.launch {
            countDownFlow.collectLatest{
                delay(1500L)
                println("the current time is: $it")
            }
        }
    }
}