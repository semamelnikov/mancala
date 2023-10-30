package com.sm.mancala.domain.game;

import static lombok.AccessLevel.PRIVATE;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.pit.PitType;
import com.sm.mancala.domain.player.PlayersGroup;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
                                player.getCustomId()
                        )
                )
                .flatMap(Collection::stream)
                .toList();

        field.pits = pits;
        field.lastCupIndex = getLastCupIndex(pits);

        return field;
    }

    private static int getLastCupIndex(List<Pit> pits) {
        return pits.size() - 2;
    }

    public static List<Pit> createPitsForPlayer(
            Field field,
            int initialStonesNumberPerCup,
            int initialNumberOfCups,
            UUID playerUuid) {

        final List<Pit> pits = new ArrayList<>(initialNumberOfCups + 1);
        for (int i = 0; i < initialNumberOfCups; i++) {
            pits.add(Cup.createCup(initialStonesNumberPerCup, playerUuid, field));
        }
        pits.add(Mancala.createMancala(playerUuid, field));
        return pits;
    }

    public Pit makeMove(UUID playerUuid, int pitNumber) {
        validatePitNumberRange(pitNumber);

        int pitIndex = pitNumber - 1;
        final Pit pit = pits.get(pitIndex);

        validatePitMoveEligibility(pit, playerUuid, pitNumber);

        final int moveLastPitIndex = processMove(pit, pitIndex);
        final Pit moveLastPit = pits.get(moveLastPitIndex);
        if (isCaptureMoveRequired(playerUuid, moveLastPit)) {
            makeCaptureAction(playerUuid, moveLastPit, moveLastPitIndex);
        }
        return moveLastPit;
    }

    public void makeCaptureAction(UUID playerUuid, Pit pit, int moveLastPitIndex) {
        final Pit mancala = getPlayerMancala(playerUuid);
        final int oppositePitIndex = lastCupIndex - moveLastPitIndex;
        final Pit oppositePit = pits.get(oppositePitIndex);
        mancala.sowStones(pit.pickUpStones());
        mancala.sowStones(oppositePit.pickUpStones());
    }

    public boolean isPlayerFinished(UUID playerUuid) {
        return pits.stream()
                .filter(pit -> pit.isOwnedBy(playerUuid) && pit.isCup())
                .allMatch(Pit::isEmpty);
    }

    public void collectRemainingStonesToMancala(UUID currentPlayerToExcept) {
        final Map<UUID, List<Pit>> cupsPerPlayer = pits.stream()
                .filter(pit -> !pit.isOwnedBy(currentPlayerToExcept) && pit.isCup())
                .collect(Collectors.groupingBy(Pit::getPlayerUuid));

        for (Map.Entry<UUID, List<Pit>> playerCups : cupsPerPlayer.entrySet()) {
            final Pit mancala = getPlayerMancala(playerCups.getKey());
            for (Pit cup : playerCups.getValue()) {
                mancala.sowStones(cup.pickUpStones());
            }
        }
    }

    public Map<UUID, Integer> getScorePerPlayer() {
        final Map<UUID, Integer> cupsPerPlayer = pits.stream()
                .filter(Pit::isMancala)
                .collect(
                        Collectors.groupingBy(
                                Pit::getPlayerUuid, Collectors.summingInt(Pit::getStoneCount)
                        )
                );
        return cupsPerPlayer;
    }

    private void validatePitNumberRange(int pitNumber) {
        if (pitNumber < 1 || pitNumber > pits.size()) {
            throw new IllegalStateException(
                    String.format("Pit number '%s' is not valid value", pitNumber)
            );
        }
    }

    private void validatePitMoveEligibility(Pit pit, UUID currentPlayerUuid, int pitNumber) {
        // cupNumber should belong to playerUuid
        if (!pit.getPlayerUuid().equals(currentPlayerUuid)) {
            throw new IllegalArgumentException(
                    String.format(
                            "Pit with number '%s' does not belong to player '%s'",
                            pitNumber,
                            currentPlayerUuid
                    )
            );
        }

        // check if this pit is cup
        if (!pit.getType().equals(PitType.CUP)) {
            throw new IllegalStateException(
                    String.format("Pit with number '%s' is not a cup", pitNumber)
            );
        }

        // check if pit is not empty
        if (pit.isEmpty()) {
            throw new IllegalStateException(
                    String.format("Pit with number '%s' is empty", pitNumber)
            );
        }
    }

    private int processMove(Pit pit, int cupIndex) {
        final int currentStonesNumber = pit.pickUpStones();

        int currentIndex = cupIndex;
        int stones = currentStonesNumber;

        while (stones > 0) {
            currentIndex = (currentIndex + 1) % pits.size();
            final Pit currentPit = pits.get(currentIndex);

            if (currentPit.isSowAllowedTo(pit.getPlayerUuid())) {
                currentPit.sowStones();
                stones--;
            }
        }

        return currentIndex;
    }

    private boolean isCaptureMoveRequired(UUID playerUuid, Pit moveLastPit) {
        return moveLastPit.isCup()
                && moveLastPit.isOwnedBy(playerUuid)
                && moveLastPit.getStoneCount() == 1;
    }

    private Pit getPlayerMancala(UUID playerUuid) {
        return pits.stream()
                .filter(pit -> pit.isOwnedBy(playerUuid) && pit.isMancala())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        String.format("Mancala Pit for player '%s' is not found", playerUuid)
                ));
    }
}
