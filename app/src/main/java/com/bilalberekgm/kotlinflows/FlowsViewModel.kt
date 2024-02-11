package com.bilalberekgm.kotlinflows

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bilalberekgm.kotlinflows.model.Post
import com.bilalberekgm.kotlinflows.model.ProfileState
import com.bilalberekgm.kotlinflows.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout


class FlowsViewModel():ViewModel() {

    private var _flatMapFlowData = MutableStateFlow(0)
    val flatMapFlowData: StateFlow<Int> = _flatMapFlowData.asStateFlow()

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

        //<-----------------------------Flow terminal operators----------------------->
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
        //<--------------------------FlatMapMerge---------------------------->
    /**
     *  val remoteRepo: Map<Int ,String> = mapOf(
     *         Pair(1,"bilal"),
     *         Pair(2,"yusuf"),
     *         Pair(3,"zeynep"),
     *         Pair(4,"rabia"),
     *         Pair(5,"bedia")
     *     )
     *
     *     //flatMapConcat is used to concatinate two sequential call.
     *     val flow1 = flow {
     *         (1..5).forEach {
     *             delay(200L)
     *             println("Getting  id of first user: $it")
     *             emit(it)
     *         }
     *     }
     *
     *
     *     private fun fetchUserById(): Flow<String>{
     *         return flow1.flatMapConcat { userId ->
     *             flow {
     *                 delay(2000L)
     *                 println("getting $userId.th user")
     *                 emit(remoteRepo[userId]!!)
     *             }
     *         }
     *     }
     *     init {
     *         collectFlatMap()
     *     }
     *     private fun collectFlatMap(){
     *         viewModelScope.launch(Dispatchers.IO) {
     *             fetchUserById().collect{ user ->
     *             _flatMapFlowData.value = user
     *             }
     *         }
     *     }
     *
     *
     */
        //<---------------------------------------------FlatMapMerge()------------------------------------->
    /**
     *
     *     val remoteRepo: Map<Int ,String> = mapOf(
     *         Pair(1,"bilal"),
     *         Pair(2,"yusuf"),
     *         Pair(3,"zeynep"),
     *         Pair(4,"rabia"),
     *        Pair(5,"bedia")
     *     )
     *
     *     //flatMapMerge first emit all userIds and then fetch users from remote repo.
     *     val flow1 = flow {
     *         (1..5).forEach {
     *             delay(200L)
     *             println("Getting  id of first user: $it")
     *             emit(it)
     *         }
     *     }
     *
     *
     *     private fun fetchUserById(): Flow<String>{
     *         return flow1.flatMapMerge { userId ->
     *             flow {
     *                 delay(2000L)
     *                 println("getting $userId.th user")
     *                 emit(remoteRepo[userId]!!)
     *             }
     *         }
     *     }
     *     init {
     *         collectFlatMap()
     *     }
     *     private fun collectFlatMap(){
     *         viewModelScope.launch(Dispatchers.IO) {
     *             fetchUserById().collect{ user ->
     *                 _flatMapFlowData.value = user
     *             }
     *         }
     *     }
     *
     */
        //<-----------------------------------FlatmapLatest--------------------------->
    /**
     *     val remoteRepo: Map<Int ,String> = mapOf(
     *         Pair(1,"bilal"),
     *         Pair(2,"yusuf"),
     *         Pair(3,"zeynep"),
     *         Pair(4,"rabia"),
     *         Pair(5,"bedia")
     *     )
     *
     *     //flatMapMerge first emit all userIds and then fetch users from remote repo.
     *     val flow1 = flow {
     *         (1..5).forEach {
     *             //fetching userId time
     *             delay(200L)
     *             println("Getting  id of first user: $it")
     *             emit(it)
     *         }
     *     }
     *
     *     // If fetching user time < fetching userId it  acts like flatmapMerge. Otherwise it will fetch only the latest user.
     *     private fun fetchUserById(): Flow<String>{
     *         return flow1.flatMapLatest { userId ->
     *             flow {
     *                 //fetching user time
     *                 delay(20L)
     *                 println("getting $userId.th user")
     *                 emit(remoteRepo[userId]!!)
     *             }
     *         }
     *     }
     *     init {
     *         collectFlatMap()
     *     }
     *     private fun collectFlatMap(){
     *         viewModelScope.launch(Dispatchers.IO) {
     *             fetchUserById().collect { user ->
     *                 _flatMapFlowData.value = user
     *             }
     *         }
     *     }
     */
        //<----------------------------Buffer()--------------------------->
    /**
     *   init {
     *         giveOrder()
     *     }
     *     private  fun giveOrder(){
     *         val flow = flow<String>{
     *             delay(250L)
     *                 emit("appetizer")
     *                 emit("MainDish")
     *                 emit("dessert")
     *             }
     *         //with buffer we  all values and  collect them at the same time .
     *         // this behaviour happen cause of the
     *         // different coroutine is used before and after  buffer() operator
     *         viewModelScope.launch {
     *             flow.onEach {
     *                 println("Flow: $it delivered")
     *             }.buffer()
     *                 .collect{
     *                 println("Flow: now eating $it")
     *                 delay(2000L)
     *                 println("Flow: finished eating $it")
     *             }
     *         }
     *         }
     *
     */
        //<--------------------------Conflate()------------------------------------------->
    /**
     *     init {
     *         giveOrder()
     *     }
     *     // With conflate operator we collect  first emition and take last emitted value.
     *     private  fun giveOrder(){
     *         val flow = flow{
     *
     *             emit("appetizer")
     *             emit("MainDish")
     *             emit("dessert")
     *             emit("kebab")
     *             emit("soup")
     *             emit("tea")
     *         }
     *         //with buffer we  all values and  collect them at the same time .
     *         // this behaviour happen cause of the
     *         // different coroutine is used before and after  buffer() operator
     *         viewModelScope.launch {
     *             flow.onEach {
     *                 println("Flow: $it delivered")
     *             }.conflate()
     *                 .collect{
     *                     println("Flow: now eating $it")
     *
     *                     println("Flow: finished eating $it")
     *                 }
     *         }
     *     }
     *
     */

    fun increment() {
        _flatMapFlowData.value += 1
    }
        //<-----------------------------------SharedIn()------------------------------------------------>
    /**
     *     private fun getFlowWithMultipleCollectors(): Flow<Int> {
     *         return flow {
     *             (1..10).forEach {
     *                 delay(1000L)
     *                 emit(it)
     *
     *             }
     *         }
     *     }
     *
     *     init {
     *         getFlowWithMultipleCollectors()
     *         collector()
     *     }
     *
     *     private fun collector() {
     *         val flow = getFlowWithMultipleCollectors().shareIn(viewModelScope, SharingStarted.Eagerly , replay = 1)
     *         viewModelScope.launch {
     *             viewModelScope.launch {
     *                 withTimeout(5000L){
     *                     flow.collect {
     *                         delay(500L)
     *                         println("First FLOW: collected value is: $it  ")
     *                     }
     *                 }
     *             }.join()
     *
     *             println("go to second coroutine")
     *
     *             viewModelScope.launch {
     *                 flow.collect {
     *                     delay(500L)
     *                     println("Second FLOW: collected value is: $it  ")
     *                 }
     *             }
     *         }
     *     }
     */
    var numberString = mutableStateOf("")
    private val isAuthenticated = MutableStateFlow(true)
    private val user = MutableStateFlow<User?>(null)
    private val posts = MutableStateFlow(emptyList<Post>())

    private val _profileState = MutableStateFlow<ProfileState?>(null)
    val profileState = _profileState.asStateFlow()

    private val flow1 = (1..15).asFlow().onEach { delay(1000L) }
    private val flow2 = (1..5).asFlow().onEach { delay(10000L) }

    /**
     *    init {
     *         user.combine(posts){ user , posts ->
     *            _profileState.value = profileState.value?.copy(
     *                profilePicUrl = user?.profilePicUrl,
     *                userName = user?.userName,
     *                description = user?.description,
     *                post = posts
     *            )
     *         }.launchIn(viewModelScope)
     *     }
     *
     */
        //<--------------------------Combine()------------------------------------>
    /**
     *        isAuthenticated.combine(user){ isAuthenticated,user ->
     *             if (isAuthenticated) user else null
     *         //Combine operator function is used when two more flows needs to be combined and
     *         // when one of the flows that have combined is emit something it is triggered.
     *         }.combine(posts){ user , posts ->
     *             user?.let {
     *                     _profileState.value = profileState.value?.copy(
     *                         profilePicUrl = user.profilePicUrl,
     *                         userName = user.userName,
     *                         description = user.description,
     *                         post = posts
     *                     )
     *             }
     *         }.launchIn(viewModelScope)
     */
        //<---------------------------------------Zip()----------------------------------->
    /**
     *          //Zip is uses to combine flows also but it is only works when two
     *          zipped flows execute at the same time.
     *         flow1.zip(flow2){ number1, number2 ->
     *             numberString.value += "($number1 , $number2)"
     *         }.launchIn(viewModelScope)
     */
        //<--------------------------------------merge()----------------------------->
    /**
     *     init {
     *         //the merge function emmit and collect all the
     *         // flows that injected its constructor without any condition.
     *         merge(flow1, flow2).onEach {number ->
     *             numberString.value += number.toString()
     *         }
     *     }
     *
     */

}