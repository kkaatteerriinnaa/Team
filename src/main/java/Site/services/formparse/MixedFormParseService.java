package Site.services.formparse;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class MixedFormParseService implements FormParseService {
    private static final int MEMORY_TRESHOLD = 10 * 1024 * 1024;// 10 Mbyte
    private static final int MAX_FILE_SIZE = 20 * 1024 * 1024;// 20 Mbyte
    private static final int MAX_FORM_SIZE = 40 * 1024 * 1024;// 40 Mbyte
    private final ServletFileUpload fileUpload;

    @Inject
    public MixedFormParseService() {
        // Початкові налаштування для запобіганя вразливості переповнення диску
        DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
        // максимальний розмір файлу, що залишається у пам'яті
        fileItemFactory.setSizeThreshold(MEMORY_TRESHOLD);
        // місце для збереження тимчасових завантажень файлів (системна - TMP)
        fileItemFactory.setRepository(new File(System.getProperty("java.io.tmpdir")));
        fileUpload = new ServletFileUpload(fileItemFactory);
        fileUpload.setFileSizeMax(MAX_FILE_SIZE);
        fileUpload.setSizeMax(MAX_FORM_SIZE);
    }

    @Override
    public FormParseResult parse(HttpServletRequest request) {
        // готуємо коллекції для результатів
        final Map<String, String> fields = new HashMap<>();
        final Map<String, FileItem> files = new HashMap<>();
        final HttpServletRequest req = request;
        // визначаємо тип запиту (multipart/urlencoded)
        boolean isMultipart = request
                .getHeader("Content-Type")
                .startsWith("multipart/form-data");
        // кодування закладене у CharsetFillter
        String charsetName = (String) req.getAttribute("charsetName");
        if (charsetName == null) { // для перенесення в інші проєкти
            charsetName = StandardCharsets.UTF_8.name();
        }
        if (isMultipart) {
            // засоби commons.upload
            try {
                for (FileItem item : fileUpload.parseRequest(req)) {
                    // всі частини (parts) запиту подаються узагальненими FileItem
                    if (item.isFormField()) { // це текстове поле форми
                        fields.put(
                                item.getFieldName(),
                                item.getString(charsetName));
                    } else { // файлове поле
                        files.put(
                                item.getFieldName(),
                                item
                        );
                    }
                }
            } catch (FileUploadException | UnsupportedEncodingException ex) {
                throw new RuntimeException(ex); // TODO: replace by logger
            }
        } else { // urlencoded
            // servlet-api засоби вилучення пераметрів
            Enumeration<String> paramNames = req.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                fields.put(name, req.getParameter(name));
            }
        }
        return new FormParseResult() {
            @Override
            public Map<String, String> getFields() {
                return fields;
            }

            @Override
            public Map<String, FileItem> getFiles() {
                return files;
            }

            @Override
            public HttpServletRequest getRequest() {
                return req;
            }

        };
    }
}
