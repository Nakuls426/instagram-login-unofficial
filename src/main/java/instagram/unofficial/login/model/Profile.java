package instagram.unofficial.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import instagram.unofficial.login.models.IGBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;


import java.io.Serializable;
import java.util.List;


@Data
public class Profile extends IGBaseModel implements Serializable {
    private static final long serialVersionUID = -892648357982l;
    @EqualsAndHashCode.Include
    private Long pk;
    private String username;
    private String full_name;
    @JsonProperty("is_private")
    private boolean is_private;
    private String profile_pic_url;
    private String allow_mention_setting;
    private String profile_pic_id;
    private int media_count;
    private int account_type;
    private String external_url;
    private boolean has_placed_orders;
    private int follower_count;
    private int following_count;
    private List<User.ProfilePic> hd_profile_pic_versions;
    private User.ProfilePic hd_profile_pic_url_info;
    @JsonProperty("is_verified")
    private boolean is_verified;
    private boolean has_anonymous_profile_picture;
}
