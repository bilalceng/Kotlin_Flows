package com.bilalberekgm.kotlinflows

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bilalberekgm.kotlinflows.ui.theme.KotlinFlowsTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
   private  val viewModel :FlowsViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        collectLatestLifeCycleFlow(viewModel.flatMapFlowData){number ->
            //binding.counterTv.text = number.toString()
        }
        /**
         *         lifecycleScope.launch{
         *             repeatOnLifecycle(Lifecycle.State.STARTED){
         *                 viewModel.countDownFlow.collectLatest{number ->
         *                     binding.counterTv.text = number.toString()
         *                 }
         *             }
         *         }
         *
         */

        setContent {
            KotlinFlowsTheme {
                val viewModel = viewModel<FlowsViewModel>()
                val count  = viewModel.flatMapFlowData.collectAsState(initial = "")

                Box(modifier = Modifier.fillMaxSize()){
                   Button(
                       modifier = Modifier.align(Alignment.Center),
                       onClick = { viewModel.increment() }

                   ) {
                        Text(text = "counter is: ${count.value}")
                   }
                }

            }
        }
    }

    private fun <T> ComponentActivity.collectLatestLifeCycleFlow(
        flow:Flow<T>, collect:suspend (T) -> Unit
    ){
        lifecycleScope.launch {
            /**
             * Warning: Never collect a flow from the UI directly from launch or
             * the launchIn extension function if the UI needs to be updated.
             * These functions process events even when the view is not visible.
             * This behavior can lead to app crashes.
             * To avoid that, use the repeatOnLifecycle API as shown follow.
             */

            /**
             * LiveData.observe() automatically unregisters
             * the consumer when the view goes to the STOPPED state,
             * whereas collecting from a StateFlow or any other flow
             * does not stop collecting automatically.
             * To achieve the same behavior,
             * you need to collect the flow from a Lifecycle.repeatOnLifecycle block.
             */
            repeatOnLifecycle(Lifecycle.State.STARTED){
                flow.collectLatest(collect)
            }

        }
    }
}



