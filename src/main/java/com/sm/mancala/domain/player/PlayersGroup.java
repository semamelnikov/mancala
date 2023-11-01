package com.sm.mancala.domain.player;

import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.web.model.PlayersGroupDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
public class PlayersGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int activePlayerIndex;

    @OneToMany(mappedBy = "playersGroup", cascade = CascadeType.PERSIST)
    private List<Player> players;

    public static PlayersGroup createPlayersGroup(Integer playersNumber) {
        final PlayersGroup playersGroup = new PlayersGroup();

        final List<Player> players = new ArrayList<>(playersNumber);
        for (int i = 0; i < playersNumber; i++) {
            players.add(
                    Player.createPlayer(playersGroup)
            );
        }

        playersGroup.setPlayers(players);
        playersGroup.setActivePlayerIndex(0);

        return playersGroup;
    }

    public Player moveToNextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        return getActivePlayer();
    }

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public List<Mancala> getFinalMancalaStates() {
        return players.stream()
                .map(Player::collectStonesToMancala)
                .toList();
    }

    public boolean hasFinishedPlayer() {
        return players.stream().anyMatch(Player::isFinished);
    }

    public List<Player> getPlayers() {
        return players;
    }

    public PlayersGroupDto toDto() {
        return new PlayersGroupDto()
                .id(id)
                .activePlayerIndex(activePlayerIndex)
                .players(players.stream().map(Player::toDto).toList());
    }
}
