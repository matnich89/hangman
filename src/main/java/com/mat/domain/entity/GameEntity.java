package com.mat.domain.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table( name = "game")
public class GameEntity {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column( name = "game_id")
    private Long gameId;

    @Column(name = "word")
    private String word;

    @ElementCollection
    private Set<UsedCharacterEntity> usedCharacterEntities = new HashSet<>();

    @Column(name = "attempts_left" )
    private int attemptsLeft = 10;

    public GameEntity(String word) {
        this.word = word;
    }

    public GameEntity() {
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Set<UsedCharacterEntity> getUsedCharacterEntities() {
        return usedCharacterEntities;
    }

    public Integer getAttemptsLeft() {
        return attemptsLeft;
    }

    public void setAttemptsLeft(Integer attemptsLeft) {
        this.attemptsLeft = attemptsLeft ;
    }


    public void addUsedEntity(final UsedCharacterEntity usedCharacterEntity)
    {
        usedCharacterEntities.add(usedCharacterEntity);
    }

}
