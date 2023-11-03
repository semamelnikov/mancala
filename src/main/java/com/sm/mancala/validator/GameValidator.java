package com.sm.mancala.validator;

import com.sm.mancala.domain.pit.Cup;

public interface GameValidator {

    void validateActivePlayer(Long activePlayerId, Long currentPlayerId, Long gameId);

    void validateCupMoveEligibility(Cup cup, Integer cupNumber);
}
