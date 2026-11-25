package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Digitalization;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

@Profile({"local", "prod"})
public interface DigitalizationRepositoryJpa
        extends JpaRepository<Digitalization, Long>, DigitalizationRepository {}
