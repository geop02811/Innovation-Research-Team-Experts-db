package org.innov.expertdb.models;

public enum Availability {
    FULL_TIME, 
    PART_TIME, 
    SHORT_TERM("Short-term (<3 months)"),
    MEDIUM_TERM("Medium-term (3-12 months)"),
    LONG_TERM("Long-term (>1 year)");
    
    private final String displayValue;
    
    Availability() {
        this.displayValue = this.name();
    }
    
    Availability(String displayValue) {
        this.displayValue = displayValue;
    }
    
    public String getDisplayValue() {
        return displayValue;
    }
}