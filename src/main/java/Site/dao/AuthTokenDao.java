package Site.dao;

import com.google.gson.JsonParser;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import Site.dto.enitities.AuthToken;
import Site.dto.enitities.User;
import Site.services.db.DbProvider;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class AuthTokenDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;
    private final UserDao userDao;

    @Inject
    public AuthTokenDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger, UserDao userDao) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
        this.userDao = userDao;
    }

    public AuthToken getTokenByBearer(String bearer) {
        String jti;
        try {
            jti = JsonParser.parseString(
                    new String(Base64.getUrlDecoder().decode(bearer.getBytes())
                    )).getAsJsonObject().get("jti").getAsString();
        } catch (Exception ex) {
            System.err.println("bearer parse error: " + ex.getMessage() + " " + bearer);
            return null;
        }
        String sql = "SELECT BIN_TO_UUID(t.jti) AS jti, t.sub, t.iat, t.exp, u.`login` AS nik FROM " + dbPrefix + "auth_tokens t" +
                " JOIN " + dbPrefix + "users u ON u.id = t.sub" +
                " WHERE t.jti = UUID_TO_BIN(?) AND t.exp > CURRENT_TIMESTAMP ";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, jti);
            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return new AuthToken(resultSet);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + "---" + sql);
        }
        return null;
    }

    public AuthToken getTokenByCredentials(String login, String password) {
        User user = userDao.getUserByCredentials(login, password);
        if (user == null) {
            return null;
        }
        // алгоритм: якщо у користувача є активний токен, то повернути його, інакше - новий
        AuthToken authToken = this.getActiveToken(user);
        if (authToken != null) {
            return authToken;
        }
        // формуємо новий токен
        authToken = new AuthToken();
        authToken.setIat(getDbTimestamp());
        authToken.setExp(new Date(authToken.getIat().getTime() + 1000 * 60 * 60 * 24));
        authToken.setSub(user.getId());
        authToken.setJti(UUID.randomUUID().toString());

        String sql = "INSERT INTO " + dbPrefix + "auth_tokens (jti, sub, iat, exp)" +
                " VALUES(UUID_TO_BIN(?), ?, ?, ?)";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, authToken.getJti());
            prep.setString(2, authToken.getSub());
            prep.setTimestamp(3, new Timestamp(authToken.getIat().getTime()));
            prep.setTimestamp(4, new Timestamp(authToken.getExp().getTime()));
            prep.executeUpdate();
            return authToken;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + "---" + sql);
        }
        return null;
    }

    private String getDbIdentity() {
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT UUID_TO_BIN(UUID())");
            resultSet.next();
            return resultSet.getString(1);
        } catch (Exception ignored) {
            return null;
        }
    }

    private Date getDbTimestamp() {
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery("SELECT CURRENT_TIMESTAMP");
            resultSet.next();
            return new Date(resultSet.getTimestamp(1).getTime());
        } catch (Exception ignored) {
            return null;
        }
    }

    public AuthToken getActiveToken(User user) {
        if (user == null) {
            return null;
        }
        String sql = "SELECT BIN_TO_UUID(a.jti) AS jti, " +
                "a.sub, a.iat, a.exp" +
                " FROM " + dbPrefix + "auth_tokens a " +
                " WHERE a.exp > CURRENT_TIMESTAMP AND a.sub = ?";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, user.getId());
            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return new AuthToken(resultSet);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + "---" + sql);
        }
        return null;
    }

    public boolean install() {
        String sql = "CREATE TABLE " + dbPrefix + "auth_tokens (" +
                "jti BINARY(16) PRIMARY KEY DEFAULT(UUID_TO_BIN(UUID())), " +
                "sub BIGINT UNSIGNED NOT NULL COMMENT 'user-id', " +
                "exp DATETIME NOT NULL, " +
                "iat DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ") ENGINE = INNODB, DEFAULT CHARSET = utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + "---" + sql);
        }
        return false;
    }
}
