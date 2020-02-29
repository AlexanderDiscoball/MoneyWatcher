package alex.disco.ball.entity;

import java.time.LocalDate;

public class IncomeContainer {

    private boolean okClicked;
    private LocalDate date;
    private Integer income;

    public IncomeContainer(LocalDate date, Integer income, boolean okClicked) {
        this.date = date;
        this.income = income;
        this.okClicked = okClicked;
    }

    public LocalDate getDate() {
        return date;
    }

    public Integer getIncome() {
        return income;
    }

    public boolean isOkClicked() {
        return okClicked;
    }
}
