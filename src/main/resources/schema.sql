DROP TABLE IF EXISTS pit;
DROP TABLE IF EXISTS game;
DROP TABLE IF EXISTS board;
DROP TABLE IF EXISTS player;
DROP TABLE IF EXISTS players_group;

CREATE TABLE players_group
(
    id                  BIGSERIAL PRIMARY KEY,
    active_player_index INTEGER NOT NULL
);

CREATE TABLE player
(
    id               BIGSERIAL PRIMARY KEY,
    players_group_id BIGINT NOT NULL,
    FOREIGN KEY (players_group_id) REFERENCES players_group (id)
);

CREATE TABLE board
(
    id             BIGSERIAL PRIMARY KEY,
    last_cup_index INTEGER NOT NULL
);

CREATE TABLE game
(
    id               BIGSERIAL PRIMARY KEY,
    board_id         BIGINT  NOT NULL,
    players_group_id BIGINT  NOT NULL,
    status           VARCHAR(20) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES board (id),
    FOREIGN KEY (players_group_id) REFERENCES players_group (id)
);

CREATE TABLE pit
(

    id                      BIGSERIAL PRIMARY KEY,
    board_index             INTEGER     NOT NULL,
    board_number_for_player INTEGER     NOT NULL,
    stone_count             INTEGER     NOT NULL,
    board_id                BIGINT      NOT NULL,
    player_id               BIGINT,
    pit_type                VARCHAR(20) NOT NULL,
    FOREIGN KEY (board_id) REFERENCES board (id),
    FOREIGN KEY (player_id) REFERENCES player (id)
);
