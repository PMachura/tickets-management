package machura.przemyslaw.mobees.ticketsmanagement.application;

import machura.przemyslaw.mobees.ticketsmanagement.common.TimeProvider;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
class TimeProviderImpl implements TimeProvider {
    @Override
    public LocalDate now() {
        return LocalDate.now();
    }
}
