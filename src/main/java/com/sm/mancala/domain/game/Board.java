package com.sm.mancala.domain.game;

import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.web.model.BoardDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    @OrderBy("boardIndex ASC")
    private List<Pit> pits;

    private int lastCupIndex;

    public Pit makeMove(Player player, int cupBoardIndex) {
        final Pit selectedPit = pits.get(cupBoardIndex);

        final int moveLastPitIndex = sowStones(selectedPit, cupBoardIndex);
        final Pit moveLastPit = pits.get(moveLastPitIndex);

        processCaptureMove(player, moveLastPitIndex, moveLastPit);

        return moveLastPit;
    }

    private int sowStones(Pit selectedPit, int cupIndex) {
        final int currentStonesNumber = selectedPit.pickUpStones();

        int currentIndex = cupIndex;
        int stones = currentStonesNumber;

        while (stones > 0) {
            currentIndex = (currentIndex + 1) % pits.size();
            final Pit currentPit = pits.get(currentIndex);

            if (currentPit.isSowAllowedFor(selectedPit.getPlayerId())) {
                currentPit.sowStones();
                stones--;
            }
        }

        return currentIndex;
    }

    private void processCaptureMove(Player player, int moveLastPitIndex, Pit moveLastPit) {
        final int oppositePitIndex = lastCupIndex - moveLastPitIndex;
        final Pit oppositePit = pits.get(oppositePitIndex);

        if (isCaptureMoveRequired(player.getId(), moveLastPit, oppositePit)) {
            makeCaptureAction(player.getMancala(), moveLastPit, oppositePit);
        }
    }

    private boolean isCaptureMoveRequired(Long playerId, Pit moveLastPit, Pit oppositePit) {
        return moveLastPit.isCup() && moveLastPit.isOwnedBy(playerId)
                && moveLastPit.getStoneCount() == 1 && !oppositePit.isEmpty();
    }

    public void makeCaptureAction(Mancala mancala, Pit pit, Pit oppositePit) {
        mancala.sowStones(pit.pickUpStones());
        mancala.sowStones(oppositePit.pickUpStones());
    }

    public BoardDto toDto() {
        return new BoardDto()
                .id(id)
                .lastCupIndex(lastCupIndex)
                .pits(pits.stream().map(Pit::toDto).toList());
    }
}
