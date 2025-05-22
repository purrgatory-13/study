package org.example.models;

/** Types of venues for events */
public enum VenueType {
    LOFT(0),
    THEATRE(1),
    CINEMA(2),
    MALL(3);
    private final int index;

    VenueType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    /**
     * Returns the VenueType corresponding to the given index
     * @param index the index of the venue type
     * @return the VenueType with the specified index
     * @throws IllegalArgumentException if no VenueType with the given index exists
     */
    public static VenueType getByIndex(int index) {
        for (VenueType type : values()) {
            if (type.index == index) {
                return type;
            }
        }
        throw new IllegalArgumentException("Нет типа билетов с индексом " + index);
    }
    }