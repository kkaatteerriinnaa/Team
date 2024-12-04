package Site.services.db;

public interface DbProvider {
    java.sql.Connection getConnection();
}
