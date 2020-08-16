package com.company.projectName.android.clean.domain.feature.counter

import com.company.projectName.android.base.mvu.None
import com.company.projectName.android.base.mvu.ScreenCmdData
import com.company.projectName.android.base.mvu.reducer
import com.company.projectName.android.clean.domain.feature.counter.contract.CounterContract
import com.company.projectName.android.clean.domain.feature.counter.contract.TimerContract

val counterReducer = reducer { state, msg ->
    state as CounterState
    when (msg) {
        is CounterContract.Message.TimerClick -> {
            if(state.isProgress){
                ScreenCmdData(
                    state = state.copy(isProgress = false),
                    cmd = CounterContract.Command.StopTimer
                )
            }else{
                ScreenCmdData(
                    state = state.copy(isProgress = true),
                    cmd = CounterContract.Command.StartTimer
                )
            }
        }
        is TimerContract.Message.CounterNewValue -> {
            ScreenCmdData(
                state = state.copy(counter = msg.value),
                cmd = None()
            )
        }
        else -> {
            ScreenCmdData(
                state = state,
                cmd = None()
            )
        }
    }
}