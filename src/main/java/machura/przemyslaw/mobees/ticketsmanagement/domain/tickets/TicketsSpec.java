package machura.przemyslaw.mobees.ticketsmanagement.domain.tickets;

import lombok.ToString;

import java.util.Optional;
import java.util.stream.Stream;


@ToString
public class TicketsSpec {
    private final int totalTicketsPool;
    private int reducedTicketsPool;
    private int reservedTickets;

    private TicketsSpec(int totalTicketsPool, int reducedTicketsPool, int reservedTickets) {
        this.totalTicketsPool = totalTicketsPool;
        this.reducedTicketsPool = reducedTicketsPool;
        this.reservedTickets = reservedTickets;
    }

    public static TicketsSpec withEmptyTicketsPool(){
        return new TicketsSpec(0,0,0);
    }

    public static Builder builder() {
        return Builder.create();
    }

    public int incrementReducedTicketsPoolAndReturnDiff(int requestedIncrementValue){
        if(requestedIncrementValue <= 0) return 0;

        int incrementCompliesWithNotReservedTickets = Math.min(requestedIncrementValue, getNotReservedTickets());
        int  newReducedTicketsPool = Math.min(reducedTicketsPool + incrementCompliesWithNotReservedTickets, totalTicketsPool);
        int diff = newReducedTicketsPool - reducedTicketsPool;
        reducedTicketsPool = newReducedTicketsPool;
        return diff;
    }

    public int decrementReducedTicketsPoolAndReturnDiff(int requestedDecrementValue){
        if(requestedDecrementValue <= 0) return 0;

        int decrementCompliesWithNotReservedTickets = Math.min(requestedDecrementValue, getNotReservedTickets());
        int newReducedTicketsPool = Math.max(0, reducedTicketsPool - decrementCompliesWithNotReservedTickets);
        int diff = newReducedTicketsPool - reducedTicketsPool;
        reducedTicketsPool = newReducedTicketsPool;
        return diff;
    }

    public int getNotReservedTickets(){
        return getTotalTicketsPool() - getReservedTickets();
    }

    public int getTotalTicketsPool() {
        return totalTicketsPool;
    }

    public int getReducedTicketsPool() {
        return reducedTicketsPool;
    }

    public int getReservedTickets() {
        return reservedTickets;
    }



    public static final class Builder {
        private int totalTicketsPool;
        private int reducedTicketsPool;
        private int reservedTickets;

        private Builder() {
        }

        public static Builder create() {
            return new Builder();
        }

        public Builder totalTicketsPool(int totalTicketsPool) {
            this.totalTicketsPool = totalTicketsPool;
            return this;
        }

        public Builder reducedTicketsPool(int reducedTicketsPool) {
            this.reducedTicketsPool = reducedTicketsPool;
            return this;
        }

        public Builder reservedTickets(int reservedTickets) {
            this.reservedTickets = reservedTickets;
            return this;
        }

        public Optional<TicketsSpec> build() {
            return areParamsValid() ?
                    Optional.of(new TicketsSpec(totalTicketsPool, reducedTicketsPool, reservedTickets))
                    : Optional.empty();
        }

        public TicketsSpec buildWithoutValidation(){
            return new TicketsSpec(totalTicketsPool, reducedTicketsPool, reservedTickets);
        }

        private boolean areParamsValid() {
            return Stream.of(totalTicketsPool, reducedTicketsPool, reservedTickets)
                    .allMatch(value -> value >= 0)
                    && totalTicketsPool >= reservedTickets
                    && totalTicketsPool >= reducedTicketsPool;
        }
    }
}
