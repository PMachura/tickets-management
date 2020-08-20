package machura.przemyslaw.mobees.ticketsmanagement.persistence;

import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import machura.przemyslaw.mobees.ticketsmanagement.common.Failure;
import machura.przemyslaw.mobees.ticketsmanagement.common.Success;
import machura.przemyslaw.mobees.ticketsmanagement.domain.matches.FootballMatch;
import machura.przemyslaw.mobees.ticketsmanagement.domain.salesperiods.SalesPeriod;
import machura.przemyslaw.mobees.ticketsmanagement.domain.tickets.FootballMatchTickets;
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

    @Transactional
    public Either<Failure, SalesPeriod> createAll(SalesPeriod salesPeriod) {
        return Try.ofSupplier(() -> createOrUpdateAllUnchecked(salesPeriod))
                .toEither()
                .mapLeft(throwable -> Failure.from(DEFAULT_ERROR_MESSAGE));
    }

    @Transactional
    public Either<Failure, Success> deleteAll(SalesPeriod salesPeriod) {
        return Try.of(() -> deleteAllUnchecked(salesPeriod))
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
    protected Success deleteAllUnchecked(SalesPeriod salesPeriod){
        List<Long> matchTicketsIds = salesPeriod.getFootballMatchTickets().stream()
                .map(FootballMatchTickets::getId).collect(Collectors.toList());
        List<Long> matchesIds = salesPeriod.getFootballMatchTickets().stream()
                .map(FootballMatchTickets::getFootballMatch)
                .map(FootballMatch::getId)
                .collect(Collectors.toList());

        footballMatchTicketsRepository.deleteByIdIn(matchTicketsIds);
        footballMatchRepository.deleteByIdIn(matchesIds);
        salesPeriodsRepository.deleteById(salesPeriod.getId());
        return Success.from("Sales period " + salesPeriod.getStartDate() + " : " + salesPeriod.getEndDate() + " deleted");
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
