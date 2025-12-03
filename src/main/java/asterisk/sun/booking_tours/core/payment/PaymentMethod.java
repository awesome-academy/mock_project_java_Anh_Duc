package asterisk.sun.booking_tours.core.payment;

public enum PaymentMethod {
    CASH("Cash", "fas fa-money-bill", "badge-success"),
    BANK_TRANSFER("Bank Transfer", "fas fa-exchange-alt", "badge-primary"),
    INTERNET_BANKING("Internet Banking", "fas fa-university", "badge-info"),
    CREDIT_CARD("Credit Card", "fas fa-credit-card", "badge-primary");

    private final String label;
    private final String icon;
    private final String badgeClass;

    PaymentMethod(String label, String icon, String badgeClass) {
        this.label = label;
        this.icon = icon;
        this.badgeClass = badgeClass;
    }

    public String getLabel() {
        return label;
    }

    public String getIcon() {
        return icon;
    }

    public String getBadgeClass() {
        return badgeClass;
    }
}
