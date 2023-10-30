package com.sm.mancala.domain.player;

import static lombok.AccessLevel.PRIVATE;

import com.sm.mancala.web.model.PlayerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter(value = PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    private Player(PlayersGroup playersGroup) {
        this.playersGroup = playersGroup;
    }

    public static Player createPlayer(PlayersGroup playersGroup) {
        return new Player(playersGroup);
    }

    public Long getId() {
        return id;
    }

    public PlayerDto toDto() {
        return new PlayerDto().id(id);
    }
}
