package com.sm.mancala.domain.player;

import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.web.model.PlayersGroupDto;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PlayersGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int activePlayerIndex;

    @OneToMany(mappedBy = "playersGroup", cascade = CascadeType.PERSIST)
    private List<Player> players;

    public Player getActivePlayer() {
        return players.get(activePlayerIndex);
    }

    public Player moveToNextPlayer() {
        activePlayerIndex = (activePlayerIndex + 1) % players.size();
        return getActivePlayer();
    }

    public List<Mancala> getFinalMancalaStates() {
        final Long activePlayerId = getActivePlayer().getId();
        return players.stream()
                .filter(player -> !player.getId().equals(activePlayerId))
                .map(Player::collectStonesToMancala)
                .toList();
    }

    public PlayersGroupDto toDto() {
        return new PlayersGroupDto()
                .id(id)
                .activePlayerIndex(activePlayerIndex)
                .players(players.stream().map(Player::toDto).toList());
    }
}
