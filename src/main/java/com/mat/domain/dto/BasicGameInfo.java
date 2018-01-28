package com.mat.domain.dto;


import java.io.Serializable;


public abstract class BasicGameInfo implements Serializable {

    private long gameId;

    BasicGameInfo(long gameId) {
        this.gameId = gameId;
    }

    BasicGameInfo() {
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

}
