package com.example.computernetworksmidterm.entity;

public enum DateDuration {
    ONE_MONTH(1),
    THREE_MONTHS(3),
    SIX_MONTHS(6),
    ONE_YEAR(12);

    private final int months;

    DateDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }
}
