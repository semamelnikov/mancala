package com.sm.mancala.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sm.mancala.MancalaApplication;
import com.sm.mancala.domain.game.Board;
import com.sm.mancala.domain.game.Game;
import com.sm.mancala.domain.game.GameMoveResult;
import com.sm.mancala.domain.game.GameMoveResultData;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.service.GameService;
import com.sm.mancala.web.model.CreateGameRequest;
import com.sm.mancala.web.model.ErrorResponse;
import com.sm.mancala.web.model.GameMove;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = MancalaApplication.class)
@AutoConfigureMockMvc
public class GameControllerTest {

    private final static String basePath = "/games";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    public void createGame_successful() throws Exception {
        final CreateGameRequest createGameRequest = new CreateGameRequest()
                .stonesPerCup(6)
                .playersNumber(2);
        final Game game = prepareGame();
        when(gameService.createGame(any(), any())).thenReturn(game);

        this.mockMvc.perform(post(basePath)
                        .content(mapToJson(createGameRequest))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().json(mapToJson(game.toDto())));
    }

    @Test
    public void createGame_invalidPlayersNumber_minConstrainViolation() throws Exception {
        final CreateGameRequest createGameRequest = new CreateGameRequest()
                .stonesPerCup(6)
                .playersNumber(1);

        final ErrorResponse errorResponse = performBadRequest(basePath, createGameRequest);
        assertThat(errorResponse.getMessage())
                .contains("playersNumber must be greater than or equal to");
    }

    @Test
    public void createGame_invalidStonesPerCup_minConstrainViolation() throws Exception {
        final CreateGameRequest createGameRequest = new CreateGameRequest()
                .stonesPerCup(0)
                .playersNumber(2);

        final ErrorResponse errorResponse = performBadRequest(basePath, createGameRequest);
        assertThat(errorResponse.getMessage())
                .contains("stonesPerCup must be greater than or equal to");
    }

    @Test
    public void createGame_invalidRequest_allFieldsRequired() throws Exception {
        final CreateGameRequest createGameRequest = new CreateGameRequest();

        final ErrorResponse errorResponse = performBadRequest(basePath, createGameRequest);
        assertThat(errorResponse.getMessage()).contains("playersNumber must not be null");
        assertThat(errorResponse.getMessage()).contains("stonesPerCup must not be null");
    }

    @Test
    public void getGameById_successful() throws Exception {
        final Long gameId = 1L;
        final Game game = prepareGame();
        when(gameService.getGameById(eq(gameId))).thenReturn(game);

        this.mockMvc.perform(get(basePath + "/" + gameId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(game.toDto())));
    }

    @Test
    public void makeGameMove_successful() throws Exception {
        final GameMove gameMove = new GameMove()
                .gameId(1L)
                .playerId(1L)
                .cupNumber(1);
        final GameMoveResultData result = new GameMoveResultData(
                new GameMoveResult(1L, null),
                prepareGame()
        );
        when(gameService.processMove(any())).thenReturn(result);

        this.mockMvc.perform(post(basePath + "/move")
                        .content(mapToJson(gameMove))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json(mapToJson(result.toDto())));
    }

    @Test
    public void makeGameMove_invalidCupNumber_minConstrainViolation() throws Exception {
        final GameMove gameMove = new GameMove()
                .gameId(1L)
                .playerId(1L)
                .cupNumber(0);

        final ErrorResponse errorResponse = performBadRequest(basePath + "/move", gameMove);
        assertThat(errorResponse.getMessage())
                .contains("cupNumber must be greater than or equal to");
    }

    @Test
    public void makeGameMove_invalidRequest_allFieldsRequired() throws Exception {
        final GameMove gameMove = new GameMove();

        final ErrorResponse errorResponse = performBadRequest(basePath + "/move", gameMove);
        assertThat(errorResponse.getMessage()).contains("gameId must not be null");
        assertThat(errorResponse.getMessage()).contains("playerId must not be null");
        assertThat(errorResponse.getMessage()).contains("cupNumber must not be null");
    }

    private <T> ErrorResponse performBadRequest(String url, T request)
            throws Exception {
        final var result = this.mockMvc.perform(post(url)
                        .content(mapToJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
        return mapFromJson(result.getResponse().getContentAsString(), ErrorResponse.class);
    }

    private String mapToJson(Object obj) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper.writeValueAsString(obj);
    }

    private <T> T mapFromJson(String json, Class<T> classReference)
            throws IOException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, classReference);
    }

    private Game prepareGame() {
        final PlayersGroup playersGroup = PlayersGroup.createPlayersGroup(2);
        final Board board = Board.createBoardForPlayers(playersGroup, 6, 6);

        return Game.createGame(
                playersGroup, board
        );
    }
}
