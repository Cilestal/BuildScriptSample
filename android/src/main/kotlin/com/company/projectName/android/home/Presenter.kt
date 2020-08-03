package com.company.projectName.android.home

import androidx.compose.Composable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.company.projectName.android.base.*
import com.company.projectName.android.home.view.Data
import com.company.projectName.android.home.view.Initial
import com.company.projectName.android.home.view.Invalidatable
import com.company.projectName.android.home.view.Progress
import kotlinx.coroutines.delay

typealias State = @Composable() () -> Unit

@ExperimentalStdlibApi
class Presenter : Component {

    private var currentState: State = { Initial() }
        set(value) {
            field = value
            viewStateSource.postValue(value)
        }
    private val viewStateSource = MutableLiveData<State>()
    val viewState: LiveData<State> = viewStateSource

    private val program: Program by lazy {
        Program()
    }

    init {
        program.init(
            initialState = HomeScreenState.Initial,
            component = this
        )

        program.accept(HomeMsg.InvalidateClick)
    }

    override fun update(state: ScreenState, msg: Msg): ScreenCmdData {
        return when (msg) {
            is HomeMsg.InvalidateClick -> {
                when (state) {
                    is HomeScreenState.Invalidatable -> state.oldState
                    else -> state
                }.let {
                    ScreenCmdData(
                        state = HomeScreenState.Progress(it),
                        cmd = HomeCmd.InvalidateData
                    )
                }
            }
            is HomeMsg.NewDataReceived -> {
                when (state) {
                    is HomeScreenState.Progress -> state.oldState
                    else -> state
                }.let {
                    HomeScreenState.Data(msg.data).let {
                        ScreenCmdData(
                            state = HomeScreenState.Invalidatable(it),
                            cmd = None()
                        )
                    }
                }
            }
            else -> {
                ScreenCmdData(
                    state = state,
                    cmd = None()
                )
            }
        }
    }

    override fun render(state: ScreenState) {
        val state = state as HomeScreenState
        currentState = generateState(state)
    }

    private fun generateState(state: HomeScreenState): @Composable() () -> Unit {
        return {
            when (state) {
                is HomeScreenState.Initial -> {
                    Initial()
                }
                is HomeScreenState.Invalidatable -> {
                    Invalidatable(
                        oldState = {
                            render(state.oldState)
                        }, onClick = this::onInvalidateClick
                    )
                }
                is HomeScreenState.Data -> {
                    Data(data = state.data)
                }
                is HomeScreenState.Progress -> {
                    Progress {
                        render(state.oldState)
                    }
                }
            }
        }
    }

    override suspend fun call(cmd: Cmd): Msg {
        val cmd = cmd as HomeCmd

        return when (cmd) {
            is HomeCmd.InvalidateData -> {
                delay(10000)//todo: for test

                HomeMsg.NewDataReceived(
                    data = "data data data data data data data data data"
                )
            }
        }
    }

    private fun onInvalidateClick() {
        program.accept(HomeMsg.InvalidateClick)
    }
}