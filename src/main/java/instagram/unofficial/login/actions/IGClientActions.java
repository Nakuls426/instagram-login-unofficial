package instagram.unofficial.login.actions;

import instagram.unofficial.login.IGClient;
import instagram.unofficial.login.actions.users.UsersAction;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

@Accessors(fluent = true, prefix = "_")
@Getter
public class IGClientActions {
    //private UploadAction _upload;
    //private TimelineAction _timeline;
    //private StoryAction _story;
    private UsersAction _users;
    //private SimulateAction _simulate;
    //private IgtvAction _igtv;
    //private AccountAction _account;
    //private SearchAction _search;
    //private StatusAction _status;


    @SneakyThrows
    public IGClientActions(IGClient client) {
        for (Field field : this.getClass().getDeclaredFields())
            if (field.getName().startsWith("_"))
                field.set(this, field.getType().getConstructor(IGClient.class).newInstance(client));
    }
}
