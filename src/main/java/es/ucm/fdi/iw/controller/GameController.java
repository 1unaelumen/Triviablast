package es.ucm.fdi.iw.controller;

import org.springframework.web.bind.annotation.*;
import es.ucm.fdi.iw.model.*;
import es.ucm.fdi.iw.repository.*;

@RestController
@RequestMapping("/game")
public class GameController {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    public GameController(GameRepository gameRepository, PlayerRepository playerRepository, UserRepository userRepository) {
    this.gameRepository = gameRepository;
    this.playerRepository = playerRepository;
    this.userRepository = userRepository;
    }

    @GetMapping("/create")
    public String createGame(@RequestParam Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Game game = new Game();
        game.setNumPlayers(1);
        game.setGameState("WAITING");

        game.setNumQuestions(10);
        game.setDifficulty("MEDIUM");
        game.setHost(user);

        gameRepository.save(game);

        return game.getCode();
    }

    @GetMapping("/join")
    public String joinGame(@RequestParam String code, @RequestParam Long userId) {

        Game game = gameRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Player player = new Player();
        User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found"));

        player.setUser(user);
        player.setGame(game);
        player.setPoints(0);

        playerRepository.save(player);

        game.setNumPlayers(game.getNumPlayers() + 1);
        gameRepository.save(game);

        return "Joined!";
    }
}