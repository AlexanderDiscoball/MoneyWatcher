package alex.disco.ball.entity;

import java.time.LocalDate;

public class HandleTimeContainer {

    private LocalDate startLocalDate;
    private LocalDate endLocalDate;
    private Category selectedCategory;
    private boolean okClicked = false;

    public HandleTimeContainer(LocalDate startLocalDate, LocalDate endLocalDate) {
        this(startLocalDate,endLocalDate,Category.ALL,false);
    }

    public HandleTimeContainer(LocalDate startLocalDate, LocalDate endLocalDate, Category selectedCategory, boolean okClicked) {
        this.startLocalDate = startLocalDate;
        this.endLocalDate = endLocalDate;
        this.okClicked = okClicked;
        this.selectedCategory = selectedCategory;
    }

    public LocalDate getStartLocalDate() {
        return startLocalDate;
    }

    public LocalDate getEndLocalDate() {
        return endLocalDate;
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    public void setOkClicked(boolean okClicked) {
        this.okClicked = okClicked;
    }
}
