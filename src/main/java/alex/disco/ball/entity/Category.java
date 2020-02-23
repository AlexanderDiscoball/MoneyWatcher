package alex.disco.ball.entity;

public enum Category {

    ALL("Все категории"),
    TRANSPORT("Транспорт"),
    FOOD("Еда"),
    CLOTHES("Одежда"),
    ELSE("Другое");

    private String title;

    Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getName(){
        return super.name();
    }

    @Override
    public String toString() {
        return title;
    }
}

