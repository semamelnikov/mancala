package com.sm.mancala.domain.player;

import com.sm.mancala.domain.pit.Cup;
import com.sm.mancala.domain.pit.Mancala;
import com.sm.mancala.exception.GameRuleException;
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
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter(value = AccessLevel.PRIVATE)
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

    public static Player createPlayer(PlayersGroup playersGroup) {
        final Player player = new Player();
        player.setPlayersGroup(playersGroup);
        return player;
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

    public Cup getCupByNumber(int cupNumber) {
        if (cupNumber < 1 || cupNumber > cups.size()) {
            throw new GameRuleException(
                    String.format("Cup number '%s' is out of range", cupNumber)
            );
        }
        return cups.get(cupNumber - 1);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Mancala getMancala() {
        return mancala;
    }

    public void setPits(List<Cup> cups, Mancala mancala) {
        this.cups = cups;
        this.mancala = mancala;
    }

    public PlayerDto toDto() {
        return new PlayerDto().id(id);
    }
}
