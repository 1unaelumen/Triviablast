package es.ucm.fdi.iw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.ucm.fdi.iw.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}