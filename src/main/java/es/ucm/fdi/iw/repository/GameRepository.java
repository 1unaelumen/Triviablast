package es.ucm.fdi.iw.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import es.ucm.fdi.iw.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByCode(String code);
}