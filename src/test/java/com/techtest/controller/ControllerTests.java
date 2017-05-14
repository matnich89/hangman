package com.techtest.controller;

import com.techtest.domain.dto.GameDto;
import com.techtest.domain.dto.GameUpdateInfo;
import com.techtest.service.GameDataService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@RunWith(SpringRunner.class)
@WebMvcTest(GameController.class)
public class ControllerTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private GameDataService gameDataService;

    @Test
    public void testGameCreation() throws Exception {

        final String json = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\fixtures\\newGame.json")));
        final GameDto gameDto = new GameDto(454, "Bob");
        gameDto.setUsedCharacters(Collections.emptySet());
        gameDto.setAttemptsLeft(10);
        given(this.gameDataService.create()).willReturn(gameDto);

        this.mvc.perform(get("/game/create")).andExpect(content().json(json, true));
    }

    @Test
    public void testGameLoad() throws Exception {

        final String json = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\fixtures\\loadedGame.json")));

        final GameDto gameDto = new GameDto(456, "Apple");

        gameDto.setAttemptsLeft(8);
        gameDto.setUsedCharacters(Collections.singleton("A"));

        given(this.gameDataService.load(456L)).willReturn(gameDto);

        this.mvc.perform(get("/game/load").param("gameid", String.valueOf(456L))).andExpect(content().json(json, true));
    }

    @Test
    public void testGameUpdate() throws Exception {

        final GameUpdateInfo gameUpdateInfo = new GameUpdateInfo(2359296L, "L");

        final String jsonPassedToController = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\fixtures\\gameUpdateInfoForUpdate.json")));

        final String expectedJson = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\fixtures\\expectedGameUpdateInfo.json")));

        given(this.gameDataService.update(any(GameUpdateInfo.class))).willReturn(gameUpdateInfo);

        this.mvc.perform(post("/game/update").contentType(MediaType.APPLICATION_JSON).content(jsonPassedToController)).andExpect(content().json(expectedJson, true));
    }

    @Test
    public void testGamePoll() throws Exception {

        final String json = new String(Files.readAllBytes(Paths.get("src\\test\\resources\\fixtures\\loadedGame.json")));

        final GameDto gameDto = new GameDto(456, "Apple");

        gameDto.setAttemptsLeft(8);
        gameDto.setUsedCharacters(Collections.singleton("A"));

        given(this.gameDataService.poll(456L)).willReturn(gameDto);

        this.mvc.perform(get("/game/poll").param("gameid", String.valueOf(456L))).andExpect(content().json(json, true));
    }

}
