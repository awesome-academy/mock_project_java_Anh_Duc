package asterisk.sun.booking_tours.application.admin.common.dto;

public class Breadcrumb {
    private final String label;
    private final String url;
    private final String icon;

    public Breadcrumb(String label, String url, String icon) {
        this.label = label;
        this.url = url;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getUrl() {
        return url;
    }
}
