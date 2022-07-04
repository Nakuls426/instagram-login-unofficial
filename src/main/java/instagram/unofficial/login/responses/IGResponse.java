package instagram.unofficial.login.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import instagram.unofficial.login.models.IGBaseModel;
import lombok.EqualsAndHashCode;


@Data
public class IGResponse extends IGBaseModel {
    private String status;
    @JsonIgnore
    private int statusCode;
    private String message;
    private boolean spam;
    private boolean lock;
    private String feedback_title;
    private String feedback_message;
    private String error_type;
    private String checkpoint_url;
}