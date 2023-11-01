package com.sm.mancala.domain.player;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.web.model.PlayerDto;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "players_group_id")
    private PlayersGroup playersGroup;

    @OneToMany(mappedBy = "player", fetch = FetchType.LAZY)
    @OrderBy("boardIndex ASC")
    private List<Cup> cups;

    @OneToOne(mappedBy = "player", fetch = FetchType.LAZY)
    private Mancala mancala;

    public Player(PlayersGroup playersGroup) {
        this.playersGroup = playersGroup;
    }

    public boolean isFinished() {
        return cups.stream().allMatch(Cup::isEmpty);
    }

    public Mancala collectStonesToMancala() {
        for (final Cup cup : cups) {
            mancala.sowStones(cup.pickUpStones());
        }
        return mancala;
    }

    public PlayerDto toDto() {
        return new PlayerDto().id(id);
    }
}
