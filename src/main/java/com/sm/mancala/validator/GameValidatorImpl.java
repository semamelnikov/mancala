package com.sm.mancala.validator;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.exception.GameRuleException;
import com.sm.mancala.properties.GameProperties;
import org.springframework.stereotype.Component;

@Component
public class GameValidatorImpl implements GameValidator {

    private final GameProperties gameProperties;

    public GameValidatorImpl(GameProperties gameProperties) {
        this.gameProperties = gameProperties;
    }

    @Override
    public void validateActivePlayer(Long activePlayerId, Long currentPlayerId, Long gameId) {
        if (!activePlayerId.equals(currentPlayerId)) {
            throw new GameRuleException(String.format(
                    "Player with id = %s is not active player for game with id = %s",
                    currentPlayerId,
                    gameId
            ));
        }
    }

    @Override
    public void validateCupNumberRange(Integer currentCupNumber) {
        if (currentCupNumber < 1 || currentCupNumber > gameProperties.getCupsNumber()) {
            throw new GameRuleException(
                    String.format("Cup number '%s' is out of range", currentCupNumber)
            );
        }
    }

    @Override
    public void validateCupMoveEligibility(Cup cup, Integer cupNumber) {
        if (cup.isEmpty()) {
            throw new GameRuleException(
                    String.format("Cup number '%s' is empty", cupNumber)
            );
        }
    }
}
