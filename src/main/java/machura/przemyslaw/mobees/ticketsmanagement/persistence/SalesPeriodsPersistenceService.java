package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.mappers.SalesPeriodMapper;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchDAO;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.FootballMatchTicketsDAO;
import machura.przemyslaw.mobees.ticketsmanagement.persistence.model.SalesPeriodDAO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesPeriodsPersistenceService {

    private static final String DEFAULT_ERROR_MESSAGE = "Sales periods persistence service problem";

    private final SalesPeriodsRepository salesPeriodsRepository;
    private final FootballMatchRepository footballMatchRepository;
    private final FootballMatchTicketsRepository footballMatchTicketsRepository;

    public Either<Failure, SalesPeriod> createAll(SalesPeriod salesPeriod) {
        return Try.ofSupplier(() -> createOrUpdateAllUnchecked(salesPeriod))
                .toEither()
                .mapLeft(throwable -> Failure.from(DEFAULT_ERROR_MESSAGE));
    }

    public Either<Failure, SalesPeriod> updateAll(SalesPeriod salesPeriod) {
        return Try.ofSupplier(() -> createOrUpdateAllUnchecked(salesPeriod))
                .toEither()
                .mapLeft(throwable -> Failure.from(DEFAULT_ERROR_MESSAGE));
    }

    public Either<Failure, List<SalesPeriod>> findByQuarterRange(LocalDate date) {
        return Try.ofSupplier(() -> findByQuarterRangeUnchecked(date))
                .toEither()
                .mapLeft(throwable -> Failure.from(DEFAULT_ERROR_MESSAGE));
    }

    private List<SalesPeriod> findByQuarterRangeUnchecked(LocalDate date) {
        return salesPeriodsRepository.findByQuarterRange(date).stream()
                .map(SalesPeriodMapper::map)
                .collect(Collectors.toList());
    }

    @Transactional
    protected SalesPeriod createOrUpdateAllUnchecked(SalesPeriod salesPeriod) {
        SalesPeriodDAO salesPeriodDAO = SalesPeriodMapper.map(salesPeriod);
        List<FootballMatchTicketsDAO> footballMatchTicketsDAO = salesPeriodDAO.getFootballMatchTickets();
        List<FootballMatchDAO> matchesDAO = footballMatchTicketsDAO.stream()
                .map(FootballMatchTicketsDAO::getFootballMatch)
                .collect(Collectors.toList());

        footballMatchRepository.saveAll(matchesDAO);
        SalesPeriodDAO saved = salesPeriodsRepository.save(salesPeriodDAO);
        footballMatchTicketsDAO.forEach(matchTickets -> matchTickets.setSalesPeriod(saved));
        footballMatchTicketsRepository.saveAll(footballMatchTicketsDAO);

        return SalesPeriodMapper.map(saved);
    }
}
