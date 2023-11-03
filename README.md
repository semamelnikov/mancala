# Mancala game service

## Description:
This service is a web application written in Java programming language using the Spring Boot framework. 
The service provides an API with which you can play a board game called Mancala. The game is designed 
for 2 people who take turns making moves and moving stones from one pit to another according to 
certain rules - they can be found [here](https://www.scholastic.com/content/dam/teachers/blogs/alycia-zimmerman/migrated-files/mancala_rules.pdf).

From the point of view of service implementation, the logic of moves and changes in the state of 
the game board is encapsulated in domain objects, which themselves implement the business logic and 
are responsible for their correct behavior. This makes them look like Rich domain model objects - 
objects without no-args constructors, public setters or getters in order to prevent inconsistent 
object instantiation and to keep encapsulation from breaking.

Player interaction with the game, game constraints validations, the next player or winner 
determination is implemented in the service layer in the more usual, Anemic domain model, way.

## Prerequisites:

* Docker CLI and Docker Compose V2 installed

## Startup:

To start the application, please execute the following command from the root of the project:

```
docker compose up -d
```

This command will build and run Spring Boot based application, and run Postgresql along with it.

Expected result:

```
[+] Running 3/3
 ⠿ Network mancala_default    Created                                                                                                                                                                         0.1s
 ⠿ Container mancala_db       Started                                                                                                                                                                         0.6s
 ⠿ Container mancala-service  Started 
```

To stop the application, please execute:

```
docker compose down
```

## Ports and accessibility

### Spring Boot service

The application occupies port `8080` on your host machine, so you will be able to send requests
to `http://localhost:8080/`

Mancala game API description is auto generated via Swagger. Swagger UI is accessible by the
link: ```http://localhost:8080/swagger-ui/index.html```

### Potsgresql

PostgreSQL's `5432` port is mapped to `5590` on the host machine. Please, use the following data to
establish DB connection (e.g. via DB client):

```
host = localhost
port = 5590
db-name = mancala_db
username = postgres
password = postgres
```

## Main entities

**Players group**: entity that unites different players into one gaming group.

**Player**: entity that personifies a player, each player knows which players group he belongs to.

**Board**: entity that is the playing field.

**Pit**: one of the main entities of the playing field. The pit can be of two types - **cup** or **mancala**.
Each pit in the system belongs to some board and some player. Also, each pit stores a certain number 
of stones, which are the main source of game points.

**Game**: the core entity of the game, which is the link between the players group and the game board.
The game has several statuses: it can be **ACTIVE**, and it can also end with the **WIN** or **DRAW** statuses.

## Endpoints:

There are two main endpoints that should be used to execute game related operations:

1. `POST /games` - to create a game instance based on defined parameters.

Request example:

```json
{
"playersNumber": 2,
"stonesPerCup": 6
}
```

Response example:

```json
{
    "id": 1,
    "status": "ACTIVE",
    "board": {
        "id": 1,
        "lastCupIndex": 12,
        "pits": [
            {
                "id": 1,
                "playerId": 1,
                "boardNumberForPlayer": 1,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 2,
                "playerId": 1,
                "boardNumberForPlayer": 2,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 3,
                "playerId": 1,
                "boardNumberForPlayer": 3,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 4,
                "playerId": 1,
                "boardNumberForPlayer": 4,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 5,
                "playerId": 1,
                "boardNumberForPlayer": 5,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 6,
                "playerId": 1,
                "boardNumberForPlayer": 6,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 7,
                "playerId": 1,
                "boardNumberForPlayer": 7,
                "stoneCount": 0,
                "type": "MANCALA"
            },
            {
                "id": 8,
                "playerId": 2,
                "boardNumberForPlayer": 1,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 9,
                "playerId": 2,
                "boardNumberForPlayer": 2,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 10,
                "playerId": 2,
                "boardNumberForPlayer": 3,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 11,
                "playerId": 2,
                "boardNumberForPlayer": 4,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 12,
                "playerId": 2,
                "boardNumberForPlayer": 5,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 13,
                "playerId": 2,
                "boardNumberForPlayer": 6,
                "stoneCount": 6,
                "type": "CUP"
            },
            {
                "id": 14,
                "playerId": 2,
                "boardNumberForPlayer": 7,
                "stoneCount": 0,
                "type": "MANCALA"
            }
        ]
    },
    "playersGroup": {
        "id": 1,
        "activePlayerIndex": 0,
        "players": [
            {
                "id": 1
            },
            {
                "id": 2
            }
        ]
    }
}
```

2. `POST /games/move` - to make a game move as a player.

Request example:

```json
{
  "gameId": 1,
  "playerId": 1,
  "cupNumber": 1
}
```

Response example:

```json
{
    "activePlayerId": 2,
    "winPlayerId": null,
    "game": {
        "id": 1,
        "status": "ACTIVE",
        "board": {
            "id": 1,
            "lastCupIndex": 12,
            "pits": [
                {
                    "id": 1,
                    "playerId": 1,
                    "boardNumberForPlayer": 1,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 2,
                    "playerId": 1,
                    "boardNumberForPlayer": 2,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 3,
                    "playerId": 1,
                    "boardNumberForPlayer": 3,
                    "stoneCount": 0,
                    "type": "CUP"
                },
                {
                    "id": 4,
                    "playerId": 1,
                    "boardNumberForPlayer": 4,
                    "stoneCount": 7,
                    "type": "CUP"
                },
                {
                    "id": 5,
                    "playerId": 1,
                    "boardNumberForPlayer": 5,
                    "stoneCount": 7,
                    "type": "CUP"
                },
                {
                    "id": 6,
                    "playerId": 1,
                    "boardNumberForPlayer": 6,
                    "stoneCount": 7,
                    "type": "CUP"
                },
                {
                    "id": 7,
                    "playerId": 1,
                    "boardNumberForPlayer": 7,
                    "stoneCount": 1,
                    "type": "MANCALA"
                },
                {
                    "id": 8,
                    "playerId": 2,
                    "boardNumberForPlayer": 1,
                    "stoneCount": 7,
                    "type": "CUP"
                },
                {
                    "id": 9,
                    "playerId": 2,
                    "boardNumberForPlayer": 2,
                    "stoneCount": 7,
                    "type": "CUP"
                },
                {
                    "id": 10,
                    "playerId": 2,
                    "boardNumberForPlayer": 3,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 11,
                    "playerId": 2,
                    "boardNumberForPlayer": 4,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 12,
                    "playerId": 2,
                    "boardNumberForPlayer": 5,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 13,
                    "playerId": 2,
                    "boardNumberForPlayer": 6,
                    "stoneCount": 6,
                    "type": "CUP"
                },
                {
                    "id": 14,
                    "playerId": 2,
                    "boardNumberForPlayer": 7,
                    "stoneCount": 0,
                    "type": "MANCALA"
                }
            ]
        },
        "playersGroup": {
            "id": 1,
            "activePlayerIndex": 1,
            "players": [
                {
                    "id": 1
                },
                {
                    "id": 2
                }
            ]
        }
    }
}
```