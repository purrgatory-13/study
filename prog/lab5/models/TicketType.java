package org.example.models;

/** Types of tickets */
public enum TicketType {
    VIP(0),
    USUAL(1),
    BUDGETARY(2),
    CHEAP(3);

    private final int index;

    TicketType(int index) {
        this.index = index;
    }

    public int getIndex() {
        return this.index;
    }

    /**
     * Returns the TicketType corresponding to the given index
     * @param index the index of the ticket type
     * @return the TicketType with the specified index
     * @throws IllegalArgumentException if no TicketType with the given index exists
     */
    public static TicketType getByIndex(int index) {
        for (TicketType type : values()) {
            if (type.index == index) {
                return type;
            }
        }
        throw new IllegalArgumentException("Нет типа билетов с индексом " + index);
    }
}