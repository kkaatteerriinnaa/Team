package Site.services.formparse;

import org.apache.commons.fileupload.FileItem;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface FormParseResult {
    Map<String, String> getFields();

    Map<String, FileItem> getFiles();

    HttpServletRequest getRequest();
}
