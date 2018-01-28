package com.mat.controller;

import com.mat.domain.dto.GameDto;
import com.mat.domain.dto.GameUpdateInfo;
import com.mat.service.GameDataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/game")
public class GameController {

    @Autowired
    private GameDataService gameDataService;

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    @RequestMapping(value = "/create", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public GameDto create() throws ExecutionException, InterruptedException {
        logger.info("Create request received");
        return gameDataService.create();
    }

    @RequestMapping(value = "/update", produces = "application/json", method = RequestMethod.POST)
    @ResponseBody
    public GameUpdateInfo update(@RequestBody final GameUpdateInfo gameUpdateInfo) {
        logger.info("Update Request Received for game: {}", gameUpdateInfo.getGameId());
        return gameDataService.update(gameUpdateInfo);
    }

    @RequestMapping(value = "/load", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public GameDto load(@RequestParam("gameid") final long id) throws ExecutionException, InterruptedException {
        logger.info("Load request recieved for game: {}", id);
        return gameDataService.load(id);
    }

    @RequestMapping(value = "/poll", produces = "application/json", method = RequestMethod.GET)
    public GameDto poll(@RequestParam("gameid") final long id) throws ExecutionException, InterruptedException {
        logger.info("Poll request received for game: {}", id);
        return gameDataService.poll(id);
    }

    @RequestMapping(value = "/kill", produces = "application/json", method = RequestMethod.POST)
    public void kill(@RequestBody final GameUpdateInfo gameUpdateInfo) {
        logger.info("Kill request received for game: {}", gameUpdateInfo.getGameId());
        gameDataService.kill(gameUpdateInfo.getGameId());
    }

    @RequestMapping(value = "/peek", produces = "application/json", method = RequestMethod.GET)
    @ResponseBody
    public Set<GameDto> peek() {
        logger.info("Peek Request received");
        return gameDataService.peek();
    }

}
