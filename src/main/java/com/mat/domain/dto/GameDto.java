package com.mat.domain.dto;

import java.util.*;

public class GameDto extends BasicGameInfo {

    private String word;
    private Set<String> usedCharacters;
    private int attemptsLeft;
    private int numberOfPlayers;

    public GameDto(final long id, final String word) {
        super(id);
        this.word = word;
    }

    /*
     Required For Jackson
     */
    public GameDto() {
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }


    public Set<String> getUsedCharacters() {
        return usedCharacters;
    }

    public void setUsedCharacters(Set<String> usedCharacters) {
        this.usedCharacters = usedCharacters;
    }

    public int getAttemptsLeft() {
        return attemptsLeft;
    }

    public void setAttemptsLeft(int attemptsLeft) {
        this.attemptsLeft = attemptsLeft;
    }

    public void incrementNumberOfPlayers()
    {
        numberOfPlayers++;
    }

    public void decrementNumberOfPlayers()
    {
        numberOfPlayers--;
    }

    public void decrementNumberOfAttemptsLeft()
    {
        attemptsLeft--;
    }

    public int getNumberOfPlayers()
    {
        return numberOfPlayers;
    }

    public void addUsedCharcter(String usedCharacter)
    {
        usedCharacters.add(usedCharacter);
    }

}
