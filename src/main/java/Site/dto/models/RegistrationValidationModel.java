package Site.dto.models;

public class RegistrationValidationModel {
    private boolean isValid;
    private String nameMessage;
    private String loignMessage;
    private String dateMessage;

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    private String emailMessage;

    public RegistrationValidationModel() {
        this.nameMessage = "";
        this.loignMessage = "";
        this.dateMessage = "";
        this.emailMessage = "";
        this.isValid = true;
    }

    public String getNameMessage() {
        return nameMessage;
    }

    public void setNameMessage(String nameMessage) {
        this.nameMessage = nameMessage;
    }

    public String getLoignMessage() {
        return loignMessage;
    }

    public void setLoignMessage(String loignMessage) {
        this.loignMessage = loignMessage;
    }

    public String getEmailMessage() {
        return emailMessage;
    }

    public void setEmailMessage(String emailMessage) {
        this.emailMessage = emailMessage;
    }

    public String getDateMessage() {
        return dateMessage;
    }

    public void setDateMessage(String dateMessage) {
        this.dateMessage = dateMessage;
    }
}
