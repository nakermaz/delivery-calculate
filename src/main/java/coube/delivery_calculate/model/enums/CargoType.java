package coube.delivery_calculate.model.enums;

public enum CargoType {
    FRAGILE("Хрупкий", 10),
    OVERSIZED("Крупногабаритный", 25),
    STANDARD("Стандартный", 0);

    private final String description;
    private final int precent;

    CargoType(String description, int precent) {
        this.description = description;
        this.precent = precent;
    }

    public String getDescription() {
        return description;
    }

    public int getPrecent() {
        return precent;
    }
}
