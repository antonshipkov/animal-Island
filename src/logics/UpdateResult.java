package logics;

public class UpdateResult {

    private final long elapsed;
    private final int rabbitsTotal;
    private final int wolvesTotal;
    private final int foxTotal;
    private final int horseTotal;

    public UpdateResult (long elapsed ,int rabbitsTotal ,int wolvesTotal ,int foxTotal ,int horseTotal) {

        this.elapsed = elapsed;
        this.rabbitsTotal = rabbitsTotal;
        this.wolvesTotal = wolvesTotal;
        this.foxTotal = foxTotal;
        this.horseTotal = horseTotal;
    }

    public long getElapsed ( ) {
        return elapsed;
    }

    public int getRabbitsTotal ( ) {
        return rabbitsTotal;
    }

    public int getWolvesTotal ( ) {
        return wolvesTotal;
    }

    public int getFoxTotal ( ) { return foxTotal; }

    public int getHorseTotal ( ) { return horseTotal; }
}
