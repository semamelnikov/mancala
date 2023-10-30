package com.sm.mancala;

import com.sm.mancala.domain.game.Game;
import com.sm.mancala.service.GameService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MancalaApplication implements CommandLineRunner {

    private final GameService gameService;

    public MancalaApplication(GameService gameService) {
        this.gameService = gameService;
    }

    public static void main(String[] args) {
        SpringApplication.run(MancalaApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Game game = gameService.createGame();
        Game found = gameService.getGameByCustomId(game.getCustomId());

        System.out.println(found.getCustomId());
        System.out.println(found.getField().getScorePerPlayer());
    }
}
