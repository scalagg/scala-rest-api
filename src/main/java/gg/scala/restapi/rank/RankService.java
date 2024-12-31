package gg.scala.restapi.rank;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RankService {
    private final RankRepository rankRepository;

    public RankService(RankRepository rankRepository) {
        this.rankRepository = rankRepository;
    }

    public Rank save(Rank rank) {
        return rankRepository.save(rank);
    }

    public Optional<Rank> getById(String id) {
        return rankRepository.findById(id);
    }

    public List<Rank> getAll() {
        return rankRepository.findAll();
    }
}
