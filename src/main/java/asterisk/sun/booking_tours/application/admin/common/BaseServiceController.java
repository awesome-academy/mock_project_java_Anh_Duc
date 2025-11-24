package asterisk.sun.booking_tours.application.admin.common;

public abstract class BaseServiceController<R> {
    protected R repository;

    protected BaseServiceController(R repository) {
        this.repository = repository;
    }
}
