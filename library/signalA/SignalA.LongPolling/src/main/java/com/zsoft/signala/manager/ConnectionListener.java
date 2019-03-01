package com.zsoft.signala.manager;

import com.zsoft.signala.transport.StateBase;

/**
 * Created by 胡一鸣 on 2019/2/26.
 */
public abstract class ConnectionListener {
    void onError(Exception exception) {
    }

    void onMessage(String message) {
    }

    void onStateChanged(StateBase oldState, StateBase newState) {
    }

    void onConnected() {
    }
}
