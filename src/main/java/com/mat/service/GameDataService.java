package com.mat.service;


import com.mat.controller.GameController;
import com.mat.repository.GameRepository;
import com.mat.domain.dto.GameDto;
import com.mat.domain.dto.GameUpdateInfo;
import com.mat.domain.entity.GameEntity;
import com.mat.domain.entity.UsedCharacterEntity;
import com.mat.util.WordGenerationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class GameDataService {

    @Autowired
    private GameRepository gameRepository;

    private final Map<Long, GameDto> currentGames = new ConcurrentHashMap<>();
    private final Map<Long, Object> locks = new ConcurrentHashMap<>();

    private final Logger logger = LoggerFactory.getLogger(GameController.class);

    /**
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public GameDto create() {
        logger.info("Received create request");
        GameDto gameDto;
        try {
            gameDto = convertEntityToDto(gameRepository.save(new GameEntity(WordGenerationUtil.selectWord())));
        } catch (ExecutionException | InterruptedException e) {
            return new GameDto();
        }
        gameDto.incrementNumberOfPlayers();
        currentGames.putIfAbsent(gameDto.getGameId(), gameDto);
        return gameDto;
    }

    /**
     * Updates the status of a game
     *
     * @param gameUpdateInfo
     * @return
     */
    public GameUpdateInfo update(final GameUpdateInfo gameUpdateInfo) {
        logger.info("Received update Request");
        synchronized (getUpdateSyncObject(gameUpdateInfo.getGameId())) {
            final GameDto gameDto = currentGames.get(gameUpdateInfo.getGameId());
            if (!isLetterAlreadyUsed(gameUpdateInfo, gameDto)) {
                addNewUsedCharacterAndDetermineIfCorrectGuess(gameUpdateInfo.getUsedCharacter(), gameDto);
            }
            currentGames.replace(gameDto.getGameId(), gameDto);

            gameRepository.save(convertDtoToEntityAndPersist(gameDto, gameUpdateInfo));

            locks.remove(gameDto.getGameId());
        }
        return gameUpdateInfo;
    }

    /**
     * Handles a player leaving a game
     *
     * @param id
     */
    public void kill(final long id) {
        logger.info("Kill Service invoked");
        synchronized (getUpdateSyncObject(id)) {
            GameDto gameDto = currentGames.get(id);
            gameDto.decrementNumberOfPlayers();

            if (gameDto.getNumberOfPlayers() <= 0) {
                // No one is playing the game so move from cache and store on disk
                gameRepository.save(convertDtoToEntityAndPersist(gameDto, null));
            } else {
                currentGames.replace(id, gameDto);
            }
        }
    }

    /**
     * Loads an existing game
     *
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public GameDto load(final long id) {

        logger.info("Load Service invoked with game id:{}", id);
        GameDto gameDto = currentGames.get(id);
        if (gameDto == null) {
            try {
                gameDto = convertEntityToDto(gameRepository.findOne(id));
            } catch (ExecutionException | InterruptedException e) {
                return new GameDto();
            }
            if (gameDto == null) {
                // game does not exist
                return new GameDto();
            }
            gameDto.incrementNumberOfPlayers();
            currentGames.put(id, gameDto);
        } else {
            gameDto.incrementNumberOfPlayers();
            currentGames.replace(gameDto.getGameId(), gameDto);
        }
        return gameDto;
    }

    /**
     * Converts a Set of usedCharacterEntities to a set of strings
     *
     * @param usedCharacterEntities
     * @return
     */
    private static Set<String> convertUsedCharacterEntities(final Set<UsedCharacterEntity> usedCharacterEntities) {
        if (usedCharacterEntities != null) {
            return usedCharacterEntities.stream().map(UsedCharacterEntity::getCharacterUsed).collect(Collectors.toSet());
        }
        return Collections.emptySet();
    }

    /**
     * Converts a GameEntity to a GameDto
     *
     * @param gameEntity
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private GameDto convertEntityToDto(final GameEntity gameEntity) throws ExecutionException, InterruptedException {

        if (gameEntity == null) {
            return null;
        }

        final Callable<Set<String>> usedCharacterTask = () -> convertUsedCharacterEntities(gameEntity.getUsedCharacterEntities());
        final ExecutorService executorService = Executors.newFixedThreadPool(1);

        /*
        Create and submit future to convert usedCharacterEntities to Strings
        to speed things up
         */
        final Future<Set<String>> usedCharactersFuture = executorService.submit(usedCharacterTask);
        final GameDto gameDto = new GameDto();
        gameDto.setWord(gameEntity.getWord());
        gameDto.setGameId(gameEntity.getGameId());
        gameDto.setAttemptsLeft(gameEntity.getAttemptsLeft());

        /*
        The Future should have completed by now
        so get the result if it has not completed the current thread will
        hold at this point until the future is complete
        */
        gameDto.setUsedCharacters(usedCharactersFuture.get());

        return gameDto;
    }

    private GameEntity convertDtoToEntityAndPersist(final GameDto gameDto, final GameUpdateInfo gameUpdateInfo) {
        final GameEntity gameEntity = gameRepository.findOne(gameDto.getGameId());
        if (gameUpdateInfo != null) {
            gameEntity.addUsedEntity(new UsedCharacterEntity(gameUpdateInfo.getUsedCharacter()));
        }

        gameEntity.setAttemptsLeft(gameDto.getAttemptsLeft());

        return gameEntity;
    }

    /**
     * Checks if a game has been updated by another player
     *
     * @param id
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public GameDto poll(final long id) {
        GameDto gameDto;
        synchronized (getUpdateSyncObject(id)) {
            gameDto = currentGames.get(id);
            if (gameDto == null) {
                /*
                 Potentially polling again after server has been shut down
                 so lets go and get the game from the database;
                 */
                try {
                    gameDto = convertEntityToDto(gameRepository.findOne(id));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentGames.put(gameDto.getGameId(), gameDto);
            }
            locks.remove(id);
        }
        return gameDto;
    }

    /**
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public Set<GameDto> peek() {
        return new HashSet<>(currentGames.values());
    }

    private Object getUpdateSyncObject(final long id) {
        locks.putIfAbsent(id, new Object());
        return locks.get(id);
    }

    private boolean isLetterAlreadyUsed(final GameUpdateInfo gameUpdateInfo, final GameDto gameDto) {
        return gameDto.getUsedCharacters()
                .stream()
                .anyMatch(usedEntry -> usedEntry.equals(gameUpdateInfo.getUsedCharacter()));
    }

    private void addNewUsedCharacterAndDetermineIfCorrectGuess(final String letter, final GameDto gameDto) {
        final String word = gameDto.getWord();
        int guessedLetterIndex = word.indexOf(letter);
        gameDto.addUsedCharcter(letter);
        if (guessedLetterIndex < 0) {
            gameDto.decrementNumberOfAttemptsLeft();
        }
    }
}

