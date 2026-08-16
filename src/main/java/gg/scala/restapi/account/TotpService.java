package gg.scala.restapi.account;

import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

/**
 * @author Luna
 */
@Service
public class TotpService {
    private static final int PERIOD_SECONDS = 30;
    private static final int DIGITS = 6;
    private final SecureRandom random = new SecureRandom();
    private final Base32 base32 = new Base32();

    public String createSecret() {
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return base32.encodeToString(bytes).replace("=", "");
    }

    public boolean verify(String secret, String code) {
        if (secret == null || code == null || !code.matches("\\d{6}")) {
            return false;
        }
        long step = System.currentTimeMillis() / 1000 / PERIOD_SECONDS;
        for (long offset = -1; offset <= 1; offset++) {
            if (generate(secret, step + offset).equals(code)) {
                return true;
            }
        }
        return false;
    }

    private String generate(String secret, long step) {
        try {
            byte[] key = base32.decode(secret);
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key, "HmacSHA1"));
            byte[] hash = mac.doFinal(ByteBuffer.allocate(Long.BYTES).putLong(step).array());
            int offset = hash[hash.length - 1] & 15;
            int binary = ((hash[offset] & 127) << 24)
                    | ((hash[offset + 1] & 255) << 16)
                    | ((hash[offset + 2] & 255) << 8)
                    | (hash[offset + 3] & 255);
            return String.format("%0" + DIGITS + "d", binary % 1_000_000);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException(exception);
        }
    }
}
