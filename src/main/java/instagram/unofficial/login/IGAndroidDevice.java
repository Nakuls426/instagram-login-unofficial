package instagram.unofficial.login;

import lombok.Data;

import java.io.Serializable;

@Data
public class IGAndroidDevice extends IGDevice implements Serializable {

    private static final long serialVersionUID = -885748975689L;
    private final String androidVersion;
    private final String androidRelease;
    private final String dpi;
    private final String displayResolution;
    private final String manufacturer;
    private final String model;
    private final String device;
    private final String cpu;
    public static final String CAPABILITIES = "3brTvw==";


    public IGAndroidDevice(String formatted) {
        super(toUserAgent(formatted), CAPABILITIES, null);
        String[] format = formatted.split("; ");
        this.androidVersion = format[0].split("/")[0];
        this.androidRelease = format[0].split("/")[1];
        this.dpi = format[1];
        this.displayResolution = format[2];
        this.manufacturer = format[3];
        this.model = format[4];
        this.device = format[5];
        this.cpu = format[6];
    }

    private static String toUserAgent(String formatted) {
        return String.format("Instagram %s Android (%s; %s)",
                IGConstants.APP_VERSION,
                formatted,
                IGConstants.LOCALE);
    }

    public static final IGAndroidDevice[] GOOD_DEVICE = {
            // Device : OnePlus 7
            new IGAndroidDevice(
                    "28/9; 420dpi; 1080x2260; OnePlus; GM1903; OnePlus7; qcom; sv_SE; 164094539")

    };

}
