package com.sm.mancala.domain.game;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

import com.sm.mancala.domain.pit.Pit;
import com.sm.mancala.domain.player.Player;
import com.sm.mancala.domain.player.PlayersGroup;
import com.sm.mancala.web.model.GameMoveResult;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Map;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter(value = PRIVATE)
@NoArgsConstructor(access = PROTECTED)
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private UUID customId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "field_id")
    private Field field;

    @Enumerated(value = EnumType.STRING)
    private GameStatus status;

    private Game(UUID customId, PlayersGroup playersGroup, Field field, GameStatus status) {
        this.customId = customId;
        this.playersGroup = playersGroup;
        this.field = field;
        this.status = status;
    }

    public static Game createGame(int playersNumber, int cupsNumber, int stonesNumber) {
        final PlayersGroup playersGroup = PlayersGroup.create(playersNumber);
        final Field field = Field.createField(playersGroup, cupsNumber, stonesNumber);

        return new Game(
                UUID.randomUUID(),
                playersGroup,
                field,
                GameStatus.ACTIVE
        );
    }

    public GameMoveResult makeMove(UUID playerUuid, int cupNumber) {
        validateActivePlayer(playerUuid);

        final Pit moveLastPit = field.makeMove(playerUuid, cupNumber);

        if (field.isPlayerFinished(playerUuid)) {
            field.collectRemainingStonesToMancala(playerUuid);
            final Map<UUID, Integer> scorePerPlayer = field.getScorePerPlayer();
            determineGameResult(scorePerPlayer);
        }

        final Player nextPlayer = playersGroup.moveToNextPlayer();
        System.out.println(nextPlayer);
        return null;
    }


    private void validateActivePlayer(UUID playerUuid) {
        final Player activePlayer = playersGroup.getActivePlayer();
        if (!activePlayer.getCustomId().equals(playerUuid)) {
            throw new IllegalArgumentException(
                    String.format("Player '%s' is not active game player", playerUuid)
            );
        }
    }

    private void determineGameResult(Map<UUID, Integer> scorePerPlayer) {
        System.out.println(scorePerPlayer);
    }

    public UUID getCustomId() {
        return customId;
    }
}
