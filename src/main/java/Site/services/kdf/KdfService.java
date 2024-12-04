package Site.services.kdf;

public interface KdfService {
    String getDerivedKey(String password, String salt);
}
