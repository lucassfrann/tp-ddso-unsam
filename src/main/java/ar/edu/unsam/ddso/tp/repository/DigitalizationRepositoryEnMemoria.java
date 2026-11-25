package ar.edu.unsam.ddso.tp.repository;

import ar.edu.unsam.ddso.tp.model.Digitalization;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("test")
public class DigitalizationRepositoryEnMemoria implements DigitalizationRepository {

    private final Map<Long, Digitalization> data = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @Override
    public Digitalization save(Digitalization digitalization) {
        if (digitalization.getId() == null) {
            digitalization.setId(nextId.getAndIncrement());
        }
        data.put(digitalization.getId(), digitalization);
        return digitalization;
    }

    @Override
    public Optional<Digitalization> findById(Long id) {
        return Optional.ofNullable(data.get(id));
    }
}
