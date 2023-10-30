package com.sm.mancala.domain.player;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter(value = PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PlayersGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int activePlayerIndex;

    @OneToMany(mappedBy = "playersGroup", cascade = CascadeType.PERSIST)
    private List<Player> players;

    public static PlayersGroup create(int playersNumber) {
        final PlayersGroup playersGroup = new PlayersGroup();
        playersGroup.activePlayerIndex = 0;
        playersGroup.players = new ArrayList<>();

        for (int i = 0; i < playersNumber; i++) {
            final Player player = Player.createPlayer(UUID.randomUUID(), playersGroup);
            playersGroup.players.add(player);
        }

        return playersGroup;
    }

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public Player moveToNextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        return getActivePlayer();
    }

    public List<Player> getPlayers() {
        return players;
    }
}
