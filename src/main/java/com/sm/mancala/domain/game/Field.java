package com.sm.mancala.domain.game;

import static lombok.AccessLevel.PRIVATE;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.pit.PitType;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.web.model.FieldDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter(value = PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "field", cascade = CascadeType.PERSIST)
    @OrderBy("id ASC")
    private List<Pit> pits;

    private int lastCupIndex;

    public static Field createField(
            PlayersGroup gamePlayers,
            int initialNumberOfCups,
            int initialStonesNumberPerCup
    ) {
        final Field field = new Field();

        final List<Pit> pits = gamePlayers.getPlayers().stream()
                .map(player -> createPitsForPlayer(
                                field,
                                initialStonesNumberPerCup,
                                initialNumberOfCups,
                                player
                        )
                )
                .flatMap(Collection::stream)
                .toList();

        field.pits = pits;
        field.lastCupIndex = getLastCupIndex(pits);

        return field;
    }

    private static List<Pit> createPitsForPlayer(
            Field field,
            int initialStonesNumberPerCup,
            int initialNumberOfCups,
            Player player) {

        final List<Pit> pits = new ArrayList<>(initialNumberOfCups + 1);
        for (int i = 0; i < initialNumberOfCups; i++) {
            pits.add(Cup.createCup(initialStonesNumberPerCup, player, field));
        }
        pits.add(Mancala.createMancala(player, field));
        return pits;
    }

    private static int getLastCupIndex(List<Pit> pits) {
        return pits.size() - 2;
    }

    public Pit makeMove(Long playerId, int pitNumber) {
        validatePitNumberRange(pitNumber);

        int pitIndex = pitNumber - 1;
        final Pit pit = pits.get(pitIndex);

        validatePitMoveEligibility(pit, playerId, pitNumber);

        final int moveLastPitIndex = sowStones(pit, pitIndex);
        final Pit moveLastPit = pits.get(moveLastPitIndex);

        processCaptureMove(playerId, moveLastPitIndex, moveLastPit);

        return moveLastPit;
    }

    public boolean isPlayerFinished(Long playerId) {
        return pits.stream()
                .filter(pit -> pit.isOwnedBy(playerId) && pit.isCup())
                .allMatch(Pit::isEmpty);
    }

    public void collectRemainingStonesToMancala(Long currentPlayerIdToExcept) {
        final Map<Long, List<Pit>> cupsPerPlayer = pits.stream()
                .filter(pit -> !pit.isOwnedBy(currentPlayerIdToExcept) && pit.isCup())
                .collect(Collectors.groupingBy(Pit::getPlayerId));

        for (Map.Entry<Long, List<Pit>> playerCups : cupsPerPlayer.entrySet()) {
            final Pit mancala = getPlayerMancala(playerCups.getKey());
            for (Pit cup : playerCups.getValue()) {
                mancala.sowStones(cup.pickUpStones());
            }
        }
    }

    public Map<Long, Integer> getScorePerPlayer() {
        return pits.stream()
                .filter(Pit::isMancala)
                .collect(
                        Collectors.groupingBy(
                                Pit::getPlayerId, Collectors.summingInt(Pit::getStoneCount)
                        )
                );
    }

    private void validatePitNumberRange(int pitNumber) {
        if (pitNumber < 1 || pitNumber > pits.size()) {
            throw new IllegalStateException(
                    String.format("Pit number '%s' is not valid value", pitNumber)
            );
        }
    }

    private void validatePitMoveEligibility(Pit pit, Long currentPlayerId, int pitNumber) {
        if (!pit.getPlayerId().equals(currentPlayerId)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Pit with number '%s' does not belong to player '%s'",
                            pitNumber,
                            currentPlayerId
                    )
            );
        }

        if (!pit.getType().equals(PitType.CUP)) {
            throw new IllegalStateException(
                    String.format("Pit with number '%s' is not a cup", pitNumber)
            );
        }

        if (pit.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Pit with number '%s' is empty", pitNumber)
            );
        }
    }

    private int sowStones(Pit pit, int cupIndex) {
        final int currentStonesNumber = pit.pickUpStones();

        int currentIndex = cupIndex;
        int stones = currentStonesNumber;

        while (stones > 0) {
            currentIndex = (currentIndex + 1) % pits.size();
            final Pit currentPit = pits.get(currentIndex);

            if (currentPit.isSowAllowedTo(pit.getPlayerId())) {
                currentPit.sowStones();
                stones--;
            }
        }

        return currentIndex;
    }

    private void processCaptureMove(Long playerId, int moveLastPitIndex, Pit moveLastPit) {
        final int oppositePitIndex = lastCupIndex - moveLastPitIndex;
        final Pit oppositePit = pits.get(oppositePitIndex);

        if (isCaptureMoveRequired(playerId, moveLastPit, oppositePit)) {
            makeCaptureAction(playerId, moveLastPit, oppositePit);
        }
    }

    private boolean isCaptureMoveRequired(Long playerId, Pit moveLastPit, Pit oppositePit) {
        return moveLastPit.isCup() && moveLastPit.isOwnedBy(playerId)
                && moveLastPit.getStoneCount() == 1 && !oppositePit.isEmpty();
    }

    public void makeCaptureAction(Long playerId, Pit pit, Pit oppositePit) {
        final Pit mancala = getPlayerMancala(playerId);
        mancala.sowStones(pit.pickUpStones());
        mancala.sowStones(oppositePit.pickUpStones());
    }

    private Pit getPlayerMancala(Long playerId) {
        return pits.stream()
                .filter(pit -> pit.isOwnedBy(playerId) && pit.isMancala())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Mancala Pit for player '%s' is not found", playerId)
                ));
    }

    public FieldDto toDto() {
        return new FieldDto()
                .id(id)
                .lastCupIndex(lastCupIndex)
                .pits(pits.stream().map(Pit::toDto).toList());
    }
}
