package gg.scala.restapi.anticheat;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnticheatService {
    private final AnticheatRepository anticheatRepository;

    public AnticheatService(AnticheatRepository anticheatRepository) {
        this.anticheatRepository = anticheatRepository;
    }

    public List<AnticheatAlertResult> getAlerts(String uuid) {
        Map<String, AnticheatAlertResult> resultMap = new HashMap<>();

        List<AnticheatLog> logs = anticheatRepository.findAllByUuid(uuid);
        for (AnticheatLog log : logs) {
            String[] split = log.getCheckdata().split(":");
            String checkName = split[0] + " " + split[1];

            if (resultMap.containsKey(checkName)) {
                resultMap.computeIfPresent(checkName, (k, result) -> new AnticheatAlertResult(checkName, result.count() + 1));
            } else {
                resultMap.put(checkName, new AnticheatAlertResult(checkName, 1));
            }
        }

        return resultMap.values().stream().toList();
    }
}
