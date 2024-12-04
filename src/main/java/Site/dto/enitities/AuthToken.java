package Site.dto.enitities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class AuthToken {
    private String jti; // ~id
    private String sub; // user-id
    private Date exp;   // expires (дата закінчення)
    private Date iat;   // issued at (дата видачі)
    private String nik; // getter only ~ navigation

    public AuthToken() {
    }

    public AuthToken(ResultSet resultSet) throws SQLException {
        this.setJti(resultSet.getString("jti"));
        this.setSub(resultSet.getString("sub"));
        Timestamp moment = resultSet.getTimestamp("exp");
        this.setExp(moment == null ? null : new Date(moment.getTime()));
        moment = resultSet.getTimestamp("iat");
        this.setIat(moment == null ? null : new Date(moment.getTime()));
        try {
            this.nik = resultSet.getString("nik");
        } catch (Exception ignored) {
        }
    }

    public String getNik() {
        return nik;
    }

    public String getJti() {
        return jti;
    }

    public void setJti(String jti) {
        this.jti = jti;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public Date getExp() {
        return exp;
    }

    public void setExp(Date exp) {
        this.exp = exp;
    }

    public Date getIat() {
        return iat;
    }

    public void setIat(Date iat) {
        this.iat = iat;
    }
}
/*
Ідея - назвати поля сутності як у стандарті JWT
iss	  Issuer	Identifies principal that issued the JWT.
sub	  Subject	Identifies the subject of the JWT.
aud	  Audience	Identifies the recipients that the JWT is intended for. Each principal intended to process the JWT must identify itself with a value in the audience claim. If the principal processing the claim does not identify itself with a value in the aud claim when this claim is present, then the JWT must be rejected.
exp	  Expiration Time	Identifies the expiration time on and after which the JWT must not be accepted for processing. The value must be a NumericDate:[9] either an integer or decimal, representing seconds past 1970-01-01 00:00:00Z.
nbf	  Not Before	Identifies the time on which the JWT will start to be accepted for processing. The value must be a NumericDate.
iat	  Issued at	Identifies the time at which the JWT was issued. The value must be a NumericDate.
jti	  JWT ID	Case-sensitive unique identifier of the token even among different issuers.
 */
