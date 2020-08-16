package machura.przemyslaw.mobees.ticketsmanagement;

import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.SalesPeriodsPersistenceService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class DataInitializer {

    private final SalesPeriodsPersistenceService salesPeriodsPersistenceService;

    @EventListener(ApplicationReadyEvent.class)
    private void initialize() {
//        SalesPeriod salesPeriod = SalesPeriod.quarterly(LocalDate.now(), 300);
//        salesPeriod.getFootballMatchTickets().forEach(match -> System.out.println("MATCH \n" + match));
//        SalesPeriod saved = salesPeriodsService.createAll(salesPeriod);
//        Optional<SalesPeriod> found = salesPeriodsService.findById(saved.getId());
//        System.out.println("Initialization finished");
    }
}
