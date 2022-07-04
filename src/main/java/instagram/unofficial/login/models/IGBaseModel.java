package instagram.unofficial.login.models;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class IGBaseModel {
    @JsonAnySetter
    private final Map<String, Object> $extra_properties = new HashMap<>();


    @JsonAnyGetter
    public Map<String, Object> getExtraProperties() {
        return this.$extra_properties;
    }

    @JsonIgnore
    public Object get(String key) {
        return $extra_properties.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends IGBaseModel> T put(String key, Object val) {
        System.out.println(key+val);
        this.$extra_properties.put(key, val);

        return (T) this;
    }
}
