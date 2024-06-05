package model.characters.payments;

public enum Payment {

    MAINTENANCE_EXPENSE,
    FOOD_EXPENSE,
    ARMY_EXPENSE,

    EXPECTED_SALARY_INCOME,
    MEADOWLANDS_INCOME,
    ALLOY_MINE_INCOME,
    GOLD_MINE_INCOME,
    SLAVE_FACILITY_INCOME,
    MYSTIC_MINE_INCOME,
    WORKER_CENTER_INCOME,
    UTILITY_INCOME, EXPECTED_CLICKER_INCOME;


    @Override
    public String toString() {
        return switch (this) {
            case MAINTENANCE_EXPENSE -> "Maintenance Expense";
            case FOOD_EXPENSE -> "Monthly Food Expense";
            case ARMY_EXPENSE -> "Army Expense";
            case EXPECTED_SALARY_INCOME -> "Expected Salary Income";
            case MEADOWLANDS_INCOME -> "Meadowlands Income";
            case ALLOY_MINE_INCOME -> "Alloy Mine Income";
            case GOLD_MINE_INCOME -> "Gold Mine Income";
            case SLAVE_FACILITY_INCOME -> "Slave Facility Income";
            case MYSTIC_MINE_INCOME -> "Mystic Mine Expected Income";
            case WORKER_CENTER_INCOME -> "Worker Center Income";
            case UTILITY_INCOME -> "Utility income";
            case EXPECTED_CLICKER_INCOME -> "Expected Clicker Income";
        };
    }

}
