package com.fengtuan.videoanchor.event;

public class GameResultEvent {
    private String gameType ;
    private String period ;
    private String newPeriod ;
    private String[] resultPoint ;

    public GameResultEvent(String gameType, String period, String newPeriod, String[] resultPoint) {
        this.gameType = gameType;
        this.period = period;
        this.newPeriod = newPeriod;
        this.resultPoint = resultPoint;
    }

    public String getGameType() {
        return gameType;
    }

    public String getPeriod() {
        return period;
    }

    public String getNewPeriod() {
        return newPeriod;
    }

    public String[] getResultPoint() {
        return resultPoint;
    }
}
