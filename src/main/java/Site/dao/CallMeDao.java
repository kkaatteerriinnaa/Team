package Site.dao;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import Site.dto.enitities.CallMe;
import Site.services.db.DbProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CallMeDao {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final Logger logger;

    @Inject
    public CallMeDao(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.logger = logger;
    }

    public List<CallMe> getAll() {
        return getAll(false);
    }

    public List<CallMe> getAll(boolean includeDeleted) {
        List<CallMe> ret = new ArrayList<>();
        String sql = "SELECT C.* FROM " + dbPrefix + "call_me C";
        if (!includeDeleted) {
            sql += " WHERE C.delete_moment IS NULL";
        }
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                ret.add(new CallMe(resultSet));
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + "---" + sql);
//            System.err.println(ex.getMessage() + " " + sql);
        }
        return ret;
    }

    public CallMe getById(String id) {
        return getById(id, false);
    }

    public CallMe getById(String id, boolean includeDeleted) {
        if (id == null) {
            return null;
        }
        String sql = "SELECT * FROM " + dbPrefix + "call_me C WHERE C.id = ?";
        if (!includeDeleted) {
            sql += " AND C.delete_moment IS NULL";
        }
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, id);
            ResultSet resultSet = prep.executeQuery();
            if (resultSet.next()) {
                return new CallMe(resultSet);
            }
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return null;
    }

    public boolean updateCallMoment(CallMe item) {
        if (item == null || item.getId() == null) {
            return false;
        }

        String sql = "SELECT CURRENT_TIMESTAMP";
        Timestamp moment;
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();
            moment = resultSet.getTimestamp(1);
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
            return false;
        }
        sql = "UPDATE " + dbPrefix + "call_me SET call_moment = ? WHERE id = ?";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setTimestamp(1, moment);
            prep.setString(2, item.getId());
            prep.executeUpdate();
            item.setCallMoment(new Date(moment.getTime()));
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return false;
    }

    public boolean delete(CallMe item) {
        return delete(item, true);
    }

    public boolean delete(CallMe item, boolean softDelete) {
        if (item == null || item.getId() == null) {
            return false;
        }
        String sql = softDelete
                ? "UPDATE " + dbPrefix + "call_me SET delete_moment = CURRENT_TIMESTAMP WHERE id = ?"
                : "DELETE FROM " + dbPrefix + "call_me WHERE id = ?";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, item.getId());
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return false;
    }

    public boolean restore(CallMe item) {
        if (item == null || item.getId() == null) {
            return false;
        }
        String sql = "UPDATE " + dbPrefix + "call_me SET delete_moment = NULL WHERE id = ?";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, item.getId());
            prep.executeUpdate();
            return true;
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " --- " + sql);
        }
        return false;
    }
}
/*
DAO - Data Access Object - елементи DAL (Data Access Layer) - об'єкти призначені
для роботи із сутностями, переведення роботи з БД до об'єктів Java та їх колекцій.

 */
