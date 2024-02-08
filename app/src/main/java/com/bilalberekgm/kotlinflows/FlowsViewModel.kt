package com.bilalberekgm.kotlinflows

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class Post(
    var name: String = "",
    var surname: String = "",
    var postId: String
)

class FlowsViewModel():ViewModel() {

   private var _flatMapFlowData = MutableStateFlow("")
    val flatMapFlowData:StateFlow<String> = _flatMapFlowData.asStateFlow()

    val countDownFlow: Flow<Int> =
        flow {
            val startingValue = 10
            var currentValue = startingValue
            while (currentValue > 0) {
                delay(100L)
                emit(currentValue)
                currentValue--
            }
        }.filter { time ->
            time % 2 == 0
        }.map { evenTime ->
            evenTime * evenTime
        }.onEach {

        }.flowOn(Dispatchers.IO)


    /**
     *   init {
     *         collectFlow()
     *     }
     *
     * private fun collectFlow() {
     *         viewModelScope.launch {
     *             countDownFlow
     *                 .collect {
     *                 println("the current time is: $it")
     *             }
     *             //Count is terminal function like Collect. it returns number of specified elements.
     *            val count =  countDownFlow
     *                 .count {
     *                     it % 2 == 0
     *                 }
     *             val reduce =  countDownFlow
     *                 .filter { it % 4 == 0 }
     *                 .reduce { accumulator, value ->
     *                     accumulator + value
     *                 }
     *             println("the current reduce is: $reduce")
     *         }
     *     }
     *
     *
     *     // Just write "the current time is: 0" to the logcat
     *
     *     /**
     *      *   private fun collectFlow(){
     *      *         viewModelScope.launch {
     *      *             countDownFlow.collectLatest{
     *      *                 delay(1500L)
     *      *                 println("the current time is: $it")
     *      *             }
     *      *         }
     *      *     }
     *      * }
     *      */
     */




    val remoteRepo: Map<Int ,String> = mapOf(
        Pair(1,"bilal"),
        Pair(2,"yusuf"),
        Pair(3,"zeynep"),
        Pair(4,"rabia"),
        Pair(5,"bedia")
    )

    //flatMapConcat is used to concatinate two sequential call.
    val flow1 = flow {
        (1..5).forEach {
            delay(200L)
            println("Getting  id of first user: $it")
            emit(it)
        }
    }

    private fun fetchUserById(): Flow<String>{
        return flow1.flatMapConcat { userId ->
            flow {
                delay(2000L)
                println("getting $userId.th user")
                emit(remoteRepo[userId]!!)
            }
        }
    }
    init {
        collectFlatMap()
    }
    private fun collectFlatMap(){
        viewModelScope.launch(Dispatchers.IO) {
            fetchUserById().collect{ user ->
            _flatMapFlowData.value = user
            }
        }
    }
}