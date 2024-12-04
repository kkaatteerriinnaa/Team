package Site.dto.models;

import org.apache.commons.fileupload.FileItem;
import Site.services.formparse.FormParseResult;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class RegFormModel {

    // region fields
    private String name;
    private String login;
    private String password;
    private String repeat;
    private String email;
    private Date birthdate;
    private boolean isAgree;
    private String avatar; // filename for avatar

    // endregion
    public RegFormModel(FormParseResult result) throws ParseException {
        Map<String, String> fields = result.getFields();
        this.setName(fields.get("reg-name"));
        this.setLogin(fields.get("reg-login"));
        this.setPassword(fields.get("reg-password"));
        this.setRepeat(fields.get("reg-repeat"));
        this.setEmail(fields.get("reg-email"));
        this.setIsAgree(fields.get("reg-rules"));
        this.setBirthdate(fields.get("reg-birthdate"));
//
//        if (files.containsKey("reg-avatar")) {
//            // є переданий файл, обробляємо його
//            this.setAvatar(files.get("reg-avatar"));
//        }
        this.setAvatar(result);
    }

//    public RegFormModel(HttpServletRequest request) throws ParseException {
//        this.setName(request.getParameter("reg-name"));
//        this.setLogin(request.getParameter("reg-login"));
//        this.setPassword(request.getParameter("reg-password"));
//        this.setRepeat(request.getParameter("reg-repeat"));
//        this.setEmail(request.getParameter("reg-email"));
//        this.setIsAgree(request.getParameter("reg-rules"));
//        this.setBirthdate(request.getParameter("reg-birthdate"));
//    }

    public String getAvatar() {
        return avatar;
    }

    public RegistrationValidationModel getErrorMessages() {
        RegistrationValidationModel result = new RegistrationValidationModel();
        //boolean isValid = true;
        if (name == null || "".equals(name)) {
            result.setValid(false);
            result.setNameMessage("Ім'я не може бути порожнім");
        }
        if (login == null || "".equals(login)) {
            result.setValid(false);
            result.setLoignMessage("Логін не може бути порожнім");
        } else if (!Pattern.matches("^[a-zA-Z0-9]+$", login)) {
            result.setValid(false);
            result.setLoignMessage("Логін не відповідає шаблону: тільки літери та цифри без пробілів");
        }
        if (email == null || "".equals(email)) {
            result.setValid(false);
            result.setEmailMessage("Email не може бути порожнім");
        }
        if (getBirthdateAsString().isEmpty()) {
            result.setValid(false);
            result.setDateMessage("Дата народження не може бути порожньою");
        }
        if (result.isValid()) {
            return null;
        } else return result;
    }

    private String getExtension(String path) {
        for (int index = path.length() - 1; index > 0; index--) {
            if (path.charAt(index) == '.') {
                return path.substring(index);
            }
        }
        return "";
    }

    // region accessors
    private void setAvatar(FormParseResult result) throws ParseException {
        List<String> allowExt = Arrays.asList(".jpg", ".jpeg", ".png", ".pic", ".pict");
        Map<String, FileItem> files = result.getFiles();
        if (!files.containsKey("reg-avatar") || files.get("reg-avatar").getSize() == 0) {
            this.avatar = null;
            return;
        }

        // є переданий файл, обробляємо його
        FileItem item = files.get("reg-avatar");
        // директорія завантаження (./ - це директорія сервера (Tomcat))
        String targetDir =
                result.getRequest()
                        .getServletContext() // контекст - "оточення" сервлету, з якого дізнаємось файлові шляхи
                        .getRealPath("./upload/avatar/");

        String submitedFilename = item.getName();
        // Визначити тип файлу (розширення) та перевірити не перелік дозволенних
        String ext = getExtension(submitedFilename);
        if (!allowExt.contains(ext)) {
            throw new ParseException("File have not allow extension", 0);
        }
        //String ext = submitedFilename.substring(submitedFilename.lastIndexOf('.'));
        String savedFilename;
        File savedFile;
        do {
            savedFilename = UUID.randomUUID().toString().substring(0, 8) + ext;
            savedFile = new File(targetDir, savedFilename);
        } while (savedFile.exists());
        // завантажуємо файл
        try {
            item.write(savedFile);
        } catch (Exception e) {
            throw new ParseException("File upload error", 0);
        }
        this.avatar = savedFilename;
    }

    public String getBirthdateAsString() {
        if (getBirthdate() == null) {
            return "";
        }
        return formDateFormat.format(getBirthdate());
    }

    public void setBirthdate(String birthdate) throws ParseException {
        if (birthdate == null || "".equals(birthdate)) {
            this.birthdate = null;
        } else {
            this.birthdate = formDateFormat.parse(birthdate);
        }
    }

    public void setIsAgree(String isAgree) {
        this.isAgree = "on".equalsIgnoreCase(isAgree) || "true".equalsIgnoreCase(isAgree);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public boolean isAgree() {
        return isAgree;
    }

    public void setIsAgree(boolean agree) {
        isAgree = agree;
    }

    // endregion
    private static final SimpleDateFormat formDateFormat = new SimpleDateFormat("yyyy-MM-dd");
}
