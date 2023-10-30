package com.sm.mancala.domain.player;

import static lombok.AccessLevel.PRIVATE;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.UUID;
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

    private UUID customId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    private Player(UUID customId, PlayersGroup playersGroup) {
        this.customId = customId;
        this.playersGroup = playersGroup;
    }

    public static Player createPlayer(UUID businessId, PlayersGroup playersGroup) {
        return new Player(businessId, playersGroup);
    }

    public UUID getCustomId() {
        return customId;
    }
}
