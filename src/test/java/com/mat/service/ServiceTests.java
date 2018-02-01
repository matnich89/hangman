package com.mat.service;

import com.mat.HangmanApplication;
import com.mat.dao.GameDao;
import com.mat.domain.dto.GameDto;
import com.mat.domain.dto.GameUpdateInfo;
import com.mat.domain.entity.GameEntity;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.concurrent.ExecutionException;

import static org.mockito.Matchers.any;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HangmanApplication.class)
@WebAppConfiguration
public class ServiceTests {

    @InjectMocks
    private GameDataService gameDataService;

    @MockBean
    private GameDao gameDao;

    private final GameEntity gameEntityToReturn = new GameEntity();

    private final static long MOCK_ID = 1234L;

    private final static String MOCK_WORD = "Apple";

    private final GameUpdateInfo gameUpdateInfo = new GameUpdateInfo(1234L, "U");

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        gameEntityToReturn.setGameId(MOCK_ID);
        gameEntityToReturn.setAttemptsLeft(10);
        gameEntityToReturn.setWord(MOCK_WORD);
        Mockito.when(gameDao.create(any(GameEntity.class))).thenReturn(gameEntityToReturn);
        Mockito.when(gameDao.load(MOCK_ID)).thenReturn(gameEntityToReturn);
    }


    @Test
    public void testCreate() {
        final GameDto gameDto = gameDataService.create();
        Assert.assertEquals(MOCK_ID, gameDto.getGameId());
        Assert.assertEquals(MOCK_WORD, gameDto.getWord());
        Assert.assertEquals(10, gameDto.getAttemptsLeft());
    }

    @Test
    public void testLoad() {
        gameDataService.create();
        final GameDto gameDto = gameDataService.load(MOCK_ID);
        Assert.assertEquals(MOCK_ID, gameDto.getGameId());
        Assert.assertEquals(MOCK_WORD, gameDto.getWord());
        Assert.assertEquals(10, gameDto.getAttemptsLeft());
    }

    @Test
    public void testUpdate() {
        gameDataService.create();
        final GameUpdateInfo returnedGameUpdateInfo = gameDataService.update(this.gameUpdateInfo);
        Assert.assertNotNull(returnedGameUpdateInfo.getGameId());
        GameDto gameDto = gameDataService.load(MOCK_ID);
        Assert.assertTrue(gameDto.getUsedCharacters().contains("U"));
        Assert.assertTrue(gameDto.getAttemptsLeft() == 9);

    }

    @Test
    public void shouldDecrementNumberOfAttemptsOnUpdateWhichIsIncorrectGuess() {
        final GameUpdateInfo gameUpdateInfo = new GameUpdateInfo(1234L, "Z");
        gameDataService.create();
        gameDataService.update(gameUpdateInfo);
        final GameDto updatedGameDto = gameDataService.load(1234L);
        Assert.assertTrue(updatedGameDto.getAttemptsLeft() == 9);
    }

    @Test
    public void shouldNotDecrementNumberOfAttemptsOnUpdateWhereGuessIsCorrect(){
        final GameUpdateInfo gameUpdateInfo = new GameUpdateInfo(1234L, "A");
        gameDataService.create();
        gameDataService.update(gameUpdateInfo);
        final GameDto updatedGameDto = gameDataService.load(1234L);
        Assert.assertTrue(updatedGameDto.getAttemptsLeft() == 10);
    }

    @Test
    public void shouldNotAttemptToLoadFromDataBaseWhenGameIsInCache(){
        gameDataService.create();
        gameDataService.load(1234L);
        Mockito.verify(gameDao, Mockito.never()).load(1234L);
    }

    @Test
    public void shouldAttemptToLoadFromDataBaseWhenGameIsInCache() {
        gameDataService.load(12345L);
        Mockito.verify(gameDao, Mockito.times(1)).load(12345L);
    }

    @Test
    public void shouldAddNewUsedCharacter()  {
        final GameUpdateInfo gameUpdateInfo = new GameUpdateInfo(1234L, "A");
        gameDataService.create();
        gameDataService.update(gameUpdateInfo);
        final GameDto updatedGameDto = gameDataService.load(1234L);
        Assert.assertTrue(updatedGameDto.getUsedCharacters().contains("A"));
    }

}




