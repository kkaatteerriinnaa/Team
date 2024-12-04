package Site.services.kdf;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import Site.services.hash.HashService;

/**
 * Computes derived key using Digest Hash Service
 */
@Singleton
public class DigestHashKdfService implements KdfService {
    private final HashService hashService;

    @Inject
    public DigestHashKdfService(@Named("Digest-Hash") HashService hashService) {
        this.hashService = hashService;
    }

    @Override
    public String getDerivedKey(String password, String salt) {
        return hashService.hash(salt + password + salt);
    }
}
