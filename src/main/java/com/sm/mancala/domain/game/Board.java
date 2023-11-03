package com.sm.mancala.domain.game;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.web.model.BoardDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "board", cascade = CascadeType.PERSIST)
    @OrderBy("boardIndex ASC")
    private List<Pit> pits;

    private int lastCupIndex;

    public static Board createBoardForPlayers(
            PlayersGroup playersGroup,
            Integer cupsNumber,
            Integer stonesPerCup
    ) {
        final Board board = new Board();

        final List<Player> players = playersGroup.getPlayers();

        final List<Pit> pits = new ArrayList<>();
        for (final Player player : players) {
            // For each player we need to generate cups and one mancala
            final List<Cup> cups = IntStream.range(0, cupsNumber)
                    .mapToObj(i -> new Cup(stonesPerCup, player, board))
                    .toList();

            final Mancala mancala = new Mancala(player, board);

            player.setPits(cups, mancala);

            pits.addAll(cups);
            pits.add(mancala);
        }

        addPitsBoardIndices(pits, cupsNumber);

        // we have all the objects in pits list and references on the same objects are stored
        // in each player's instance for easy access.

        board.setPits(pits);
        board.setLastCupIndex(calculateLastCupIndex(pits));

        return board;
    }

    private static void addPitsBoardIndices(List<Pit> pits, Integer cupsNumber) {
        int pitsPerPlayer = cupsNumber + 1;
        int currentPitNumberForPlayer = 0;
        int currentPitIndex = 0;
        for (final Pit pit : pits) {
            pit.setBoardIndex(currentPitIndex);
            pit.setBoardNumberForPlayer((currentPitNumberForPlayer % pitsPerPlayer) + 1);
            currentPitIndex++;
            currentPitNumberForPlayer++;
        }
    }

    private static int calculateLastCupIndex(List<Pit> pits) {
        return pits.size() - 2;
    }

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
        if (moveLastPit.isMancala()) {
            return;
        }

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

    private void makeCaptureAction(Mancala mancala, Pit pit, Pit oppositePit) {
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
