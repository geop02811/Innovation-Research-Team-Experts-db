package org.innov.expertdb.models;

public enum YearsOfExperience {
    ZERO_TO_TWO("0-2 years"),
    THREE_TO_FIVE("3-5 years"),
    SIX_TO_TEN("6-10 years"),
    TEN_PLUS("10+ years");
    
    private final String displayValue;
    
    YearsOfExperience(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
}