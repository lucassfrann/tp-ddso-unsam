package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Digitalization;
import java.util.Optional;

public interface DigitalizationRepository {

    Digitalization save(Digitalization digitalization);

    Optional<Digitalization> findById(Long id);
}
