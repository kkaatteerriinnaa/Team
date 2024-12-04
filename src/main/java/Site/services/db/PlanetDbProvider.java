package Site.services.db;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.inject.Singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

@Singleton
public class PlanetDbProvider implements DbProvider {
    private Connection connection;

    @Override
    public Connection getConnection() {
        if (connection == null) {
            // load configuration
            // locate file - get stream - parse JSON - get as object
            JsonObject config;
            try (Reader reader = new InputStreamReader(
                    Objects.requireNonNull(
                            this.getClass().getClassLoader()
                                    .getResourceAsStream("db_config.json")))) {

                config = JsonParser.parseReader(reader).getAsJsonObject();

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            } catch (NullPointerException ex) {
                throw new RuntimeException("Resource not found");
            }
            JsonObject planetSacle = config.get("DataProviders")
                    .getAsJsonObject()
                    .get("PlanetScale")
                    .getAsJsonObject();
            try {
                // реєстрація драйвера для протоколу jdbc:mysql:// (два варіанти)
                //Class.forName("com.mysql.cj.jdbc.Driver");
                DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
                // підключаємось
                this.connection = DriverManager.getConnection(
                        planetSacle.get("url").getAsString(),
                        planetSacle.get("user").getAsString(),
                        planetSacle.get("password").getAsString());
            } catch (SQLException ex) {

            }
        }
        return this.connection;
    }
}
