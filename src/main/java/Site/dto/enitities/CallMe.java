package Site.dto.enitities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class CallMe {
    private String id;
    private String name;
    private String phone;
    private Date moment;
    private Date callMoment;
    private Date deleteMoment;

    public CallMe(ResultSet resultSet) throws SQLException {
        this.setId(resultSet.getString("id"));
        this.setName(resultSet.getString("name"));
        this.setPhone(resultSet.getString("phone"));
        this.setMoment(new Date(resultSet.getTimestamp("moment").getTime()));

        Timestamp callMoment = resultSet.getTimestamp("call_moment");
        this.setCallMoment(callMoment == null ? null : new Date(callMoment.getTime()));
        Timestamp deleteMoment = resultSet.getTimestamp("delete_moment");
        this.setDeleteMoment(deleteMoment == null ? null : new Date(deleteMoment.getTime()));
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getMoment() {
        return moment;
    }

    public void setMoment(Date moment) {
        this.moment = moment;
    }

    public Date getCallMoment() {
        return callMoment;
    }

    public void setCallMoment(Date callMoment) {
        this.callMoment = callMoment;
    }

    public Date getDeleteMoment() {
        return deleteMoment;
    }

    public void setDeleteMoment(Date deleteMoment) {
        this.deleteMoment = deleteMoment;
    }
}
