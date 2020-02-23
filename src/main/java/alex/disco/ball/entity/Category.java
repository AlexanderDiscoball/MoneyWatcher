package alex.disco.ball.entity;

public enum Category {

    ALL("��� ���������"),
    TRANSPORT("���������"),
    FOOD("���"),
    CLOTHES("������"),
    ELSE("������");

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

