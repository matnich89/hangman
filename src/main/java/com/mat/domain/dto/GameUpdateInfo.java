package com.mat.domain.dto;

public class GameUpdateInfo extends BasicGameInfo {

    private String usedCharacter;

    public GameUpdateInfo(final long gameId, final String usedCharacter) {
        super(gameId);
        this.usedCharacter = usedCharacter;
    }

    /*
    Required for jackson
     */
    public GameUpdateInfo() {
    }

    public String getUsedCharacter() {
        return usedCharacter;
    }

}


