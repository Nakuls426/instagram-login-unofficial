package instagram.unofficial.login.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class User extends Profile {
    @JsonProperty("is_business")
    private boolean is_business;
    private int media_count;
    private int follower_count;
    private int following_count;
    private String biography;
    private boolean has_placed_orders;
    private String allow_mention_setting;
    private String external_url;
    private List<ProfilePic> hd_profile_pic_versions;
    private ProfilePic hd_profile_pic_url_info;
    private int account_type;
    private String external_lynx;

    @Data
    public static class ProfilePic implements Serializable {
        public String url;
        public int width;
        public int height;
    }
}
