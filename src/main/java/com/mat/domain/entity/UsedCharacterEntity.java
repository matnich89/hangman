package com.mat.domain.entity;


import javax.persistence.*;

@Embeddable
public class UsedCharacterEntity {

    @Column(name="character_used")
    private String characterUsed;

    /*
     Required for jackson
     */
    public UsedCharacterEntity() {
    }

    public UsedCharacterEntity(String characterUsed) {

        this.characterUsed = characterUsed;
    }

    public String getCharacterUsed() {
        return characterUsed;
    }

}
