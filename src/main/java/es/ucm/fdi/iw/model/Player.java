package es.ucm.fdi.iw.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_gen")
    @SequenceGenerator(name = "player_gen", sequenceName = "player_seq", allocationSize = 1)
    private long id;

    @Column(nullable = false)
    private int points;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;
}
