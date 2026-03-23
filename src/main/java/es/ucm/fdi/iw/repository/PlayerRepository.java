package es.ucm.fdi.iw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.ucm.fdi.iw.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}