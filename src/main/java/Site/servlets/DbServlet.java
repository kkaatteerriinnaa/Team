package Site.servlets;

import com.google.gson.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import Site.dao.CallMeDao;
import Site.dto.enitities.CallMe;
import Site.dto.models.GetCallsModel;
import Site.services.db.DbProvider;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@Singleton
public class DbServlet extends HttpServlet {
    private final DbProvider dbProvider;
    private final String dbPrefix;
    private final CallMeDao callMeDao;
    private final Logger logger;

    @Inject
    public DbServlet(DbProvider dbProvider, @Named("db-prefix") String dbPrefix, CallMeDao callMeDao, Logger logger) {
        this.dbProvider = dbProvider;
        this.dbPrefix = dbPrefix;
        this.callMeDao = callMeDao;
        this.logger = logger;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // метод, який запускається перед тим, як буде здійснено "розподіл" за HTTP методами
        switch (req.getMethod().toUpperCase()) {
            case "COPY":
                doCopy(req, resp);
                break;
            case "LINK":
                doLink(req, resp);
                break;
            case "PATCH":
                doPatch(req, resp);
                break;
//            case "PURGE":
//                break;
//            case "UNLINK":
//                break;
//            case "MOVE":
//                break;
            default:
                super.service(req, resp);
        }

    }

    private boolean isDigit(String value) {
        List<Character> chars = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
        for (int i = 0; i < value.length(); i++) {
            if (!chars.contains(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    protected void doLink(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //old-version
        /*resp.setHeader("Content-Type", "application/json");
        String contentType = req.getContentType();
        if (contentType == null || !contentType.startsWith("application/json")) {
            resp.setStatus(415);
            resp.getWriter().print("\"Unsupported Media Type : 'application/json' only\"");
            return;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json = "";
        JsonObject result = new JsonObject();
        try (InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());
        } catch (IOException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            result.addProperty("message", ex.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
        JsonObject data;
        try {
            data = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException ex) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON. Object required\"");
            return;
        }
        String callId;
        try {
            callId = data.get("call-id").getAsString();
        } catch (Exception ignored) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON data: required non-null 'call-id'\"");
            return;
        }
        if (!isDigit(callId)) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON data: 'call-id' is not id\"");
            return;
        }
        String sql = "UPDATE " + dbPrefix + "call_me " +
                "SET call_moment = NOW() " +
                "WHERE id = " + callId + ";";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.execute();
        } catch (SQLException ex) {
            logger.log(Level.WARNING, ex.getMessage() + " " + sql);
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
        result.addProperty("status", "204");
        result.addProperty("statusMessage", "CallMe_moment succesfully updated");
        resp.getWriter().print(result.toString());*/
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if (callId == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parametr 'call-id'\"");
            return;
        }
        CallMe item = callMeDao.getById(callId, true);
        if (item == null) {
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parametr 'call-id'\"");
            return;
        }
        if (item.getDeleteMoment() == null) {
            resp.setStatus(422);
            resp.getWriter().print("\"Uncprocessable Content: Item was processed early\"");
            return;
        }
        if (callMeDao.restore(item)) {
            resp.setStatus(202);
            item.setDeleteMoment(null);
            Gson gson = new GsonBuilder().serializeNulls().create();
            resp.getWriter().print(gson.toJson(item));
        } else {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
        }
    }

    protected void doCopy(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        List<CallMe> calls;
        String allowDelete = req.getParameter("allow-delete");
        GetCallsModel callsModel = new GetCallsModel();
        if (allowDelete == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid Request parameter. Required parametr: 'allow-delete'\"");
            return;
        } else {
            if (allowDelete.equals("true") || allowDelete.equals("false")) {
                calls = allowDelete.equals("true") ? callMeDao.getAll(true) : callMeDao.getAll();
                callsModel.setAllowDelete(allowDelete.equals("true"));
                callsModel.setCalls(calls);
            } else {
                resp.setStatus(400);
                resp.getWriter().print("\"Invalid Request parameter. Parametr 'allow-delete' must contains 'true' or 'false'\"");
                return;
            }
        }
        Gson gson = new GsonBuilder().serializeNulls().create();
        resp.getWriter().print(gson.toJson(callsModel));
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String callId = req.getParameter("call-id");
        if (callId == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parametr 'call-id'\"");
            return;
        }
        CallMe item = callMeDao.getById(callId);
        if (item == null) {
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parametr 'call-id'\"");
            return;
        }
        if (item.getCallMoment() != null) {
            resp.setStatus(422);
            resp.getWriter().print("\"Uncprocessable Content: Item was processed early\"");
            return;
        }
        if (callMeDao.updateCallMoment(item)) {
            resp.setStatus(202);
            resp.getWriter().print(new Gson().toJson(item));
        } else {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String connectionStatus;
        try {
            dbProvider.getConnection();
            connectionStatus = "Connection OK";
        } catch (RuntimeException ex) {
            connectionStatus = "Connection error: " + ex.getMessage();
        }
        req.setAttribute("connectionStatus", connectionStatus);
        req.setAttribute("page-body", "db.jsp");
        req.getRequestDispatcher("WEB-INF/_layout.jsp")
                .forward(req, resp); // ~ return View()
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Реакція на кнопку "create" - створюємо таблицю БД "замовлення дзвінків"
        String status;
        String message;
        String sql = "CREATE TABLE " + dbPrefix + "call_me (" +
                "`id` BIGINT UNSIGNED PRIMARY KEY DEFAULT (UUID_SHORT())," +
                "`name` VARCHAR(64)," +
                "`phone` CHAR(13) NOT NULL COMMENT '+38 098 765 43 21'," +
                "`moment` DATETIME DEFAULT CURRENT_TIMESTAMP," +
                "`call_moment` DATETIME NULL," +
                "`delete_moment` DATETIME NULL" +
                ") ENGINE = InnoDB DEFAULT CHARSET = UTF8";
        try (Statement statement = dbProvider.getConnection().createStatement()) {
            statement.executeUpdate(sql);
            status = "OK";
            message = "Table created";
        } catch (SQLException ex) {
            status = "error";
            message = ex.getMessage();
        }
        JsonObject result = new JsonObject();
        result.addProperty("status", status);
        result.addProperty("message", message);
        resp.getWriter().print(result.toString());
    }

    //    private CallMeValidationModel validateCallMeForm(JsonObject data) {
//        CallMeValidationModel result = new CallMeValidationModel();
//        String name = data.get("name").getAsString();
//        String phone = data.get("phone").getAsString();
//        if (name == null || "".equals(name)) {
//            result.setValid(false);
//            result.setNameMessage("Ім'я не може бути порожнім");
//        } else if (!Pattern.matches("^[а-яА-Яa-zA-ZіІїЇ]+$", name)) {
//            result.setValid(false);
//            result.setNameMessage("Ім'я не відповідає шаблону, тільки букви, без пробілів");
//            result.setNameField(name);
//        }
//        if (phone == null || "".equals(phone)) {
//            result.setValid(false);
//            result.setNameMessage("Телефон не може бути порожнім");
//        } else if (!Pattern.matches("^\\+38\\s?(\\(\\d{3}\\)|\\d{3})\\s?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2}$", phone)) {
//            result.setValid(false);
//            result.setNameMessage("Телефон не відповідає шаблону: +38099xxxxxxx або +38(099)xxx-xx-xx");
//            result.setPhoneField(phone);
//        }
//        return result;
//    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // insert - додати до БД. Дані передаються як JSON у тілі запиту
        resp.setContentType("application/json");
        String contentType = req.getContentType();
        if (contentType == null || !contentType.startsWith("application/json")) {
            resp.setStatus(415);
            resp.getWriter().print("\"Unsupported Media Type : 'application/json' only\"");
            return;
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        String json = "";
        JsonObject result = new JsonObject();
        try (InputStream body = req.getInputStream()) {
            while ((len = body.read(buffer)) > 0) {
                bytes.write(buffer, 0, len);
            }
            json = bytes.toString(StandardCharsets.UTF_8.name());
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            result.addProperty("message", ex.getMessage());
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
        JsonObject data;
        try {
            data = JsonParser.parseString(json).getAsJsonObject();
        } catch (JsonSyntaxException | IllegalStateException ex) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON. Object required\"");
            return;
        }
        String name, phone;
        try {
            name = data.get("name").getAsString();
            phone = data.get("phone").getAsString();
        } catch (Exception ignored) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid JSON data: required non-null 'name' and 'phone' fields\"");
            return;
        }
        if (!Pattern.matches("^\\+38\\s?(\\(\\d{3}\\)|\\d{3})\\s?\\d{3}(-|\\s)?\\d{2}(-|\\s)?\\d{2}$", phone)) {
            resp.setStatus(400);
            resp.getWriter().print("\"Invalid 'phone' field: required '+\\d{12}' format\"");
            return;
        }
        phone = phone.replaceAll("[\\s()-]+", "");

        String sql = "INSERT INTO " + dbPrefix + "call_me (name, phone) " +
                "VALUES ( ?, ? )";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            prep.setString(1, name); // ! у JDBC відлік від 1
            prep.setString(2, phone);// 2 - другий плейсхолдер "?"
            prep.execute();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage() + " " + sql);
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
        sql = "SELECT id FROM java201_call_me ORDER BY moment DESC LIMIT 1";
        String lastId = "";
        try (PreparedStatement prep = dbProvider.getConnection().prepareStatement(sql)) {
            ResultSet qResult = prep.executeQuery();
            if (qResult.next()) {
                lastId = qResult.getString("id");
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage() + " " + sql);
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
            return;
        }
        resp.setStatus(201);
        result.addProperty("name", name);
        result.addProperty("phone", phone);
        result.addProperty("status", "created");
        result.addProperty("last_id", lastId);
        resp.getWriter().print(result.toString());
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        String callId = req.getParameter("call-id");
        if (callId == null) {
            resp.setStatus(400);
            resp.getWriter().print("\"Missing required parametr 'call-id'\"");
            return;
        }
        CallMe item = callMeDao.getById(callId
        );
        if (item == null) {
            resp.setStatus(404);
            resp.getWriter().print("\"Item not found for given parametr 'call-id'\"");
            return;
        }
        if (item.getDeleteMoment() != null) {
            resp.setStatus(422);
            resp.getWriter().print("\"Uncprocessable Content: Item was processed early\"");
            return;
        }
        if (callMeDao.delete(item)) {
            resp.setStatus(202);
            resp.getWriter().print("\"Operation completed\"");
        } else {
            resp.setStatus(500);
            resp.getWriter().print("\"Server error. Details on server's logs\"");
        }
    }
}
