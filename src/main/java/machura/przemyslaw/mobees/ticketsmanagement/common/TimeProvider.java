package machura.przemyslaw.mobees.ticketsmanagement.common;

import java.time.LocalDate;

public interface TimeProvider {
    LocalDate now();
}
