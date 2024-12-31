package gg.scala.restapi.profile.punishment;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PunishmentService {
    private final PunishmentRepository punishmentRepository;

    public PunishmentService(PunishmentRepository punishmentRepository) {
        this.punishmentRepository = punishmentRepository;
    }

    public List<Punishment> getPunishments(String uuid) {
        return punishmentRepository.findAllByTarget(uuid);
    }

    public List<Punishment> getActivePunishments(String uuid) {
        return punishmentRepository.findAllByTarget(uuid)
                .stream()
                .filter(punishment -> punishment.getRemovedAt() == -1)
                .toList();
    }
}
