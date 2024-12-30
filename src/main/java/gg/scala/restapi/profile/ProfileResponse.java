package gg.scala.restapi.profile;

import gg.scala.restapi.profile.grant.Grant;
import gg.scala.restapi.rank.Rank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.bson.Document;
import org.springframework.lang.Nullable;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class ProfileResponse extends Profile {
    private final @Nullable List<Grant> activeGrants;
    private final @Nullable Rank rank;
    private final @Nullable Document webSettings;
    private final @Nullable List<String> effectivePermissions;

    public ProfileResponse(Profile profile, List<Grant> activeGrants, Rank rank, Document webSettings, @Nullable List<String> effectivePermissions) {
        super(profile.getUniqueId(), profile.getIgnoring(), profile.getIpAddress(), profile.getMetadata(), profile.getName(), profile.getPastIpAddresses(), profile.getSpecificPermissions(), profile.getTimestamp());
        this.activeGrants = activeGrants;
        this.rank = rank;
        this.webSettings = webSettings;
        this.effectivePermissions = effectivePermissions;
    }
}
