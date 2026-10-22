class UG {
    private String name;

    public UG(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

public class Main {
    public static void main(String[] args) {
        UG ug_example = new UG("Damian");
        UG ug_example2 = new UG("Mati");
        ug_example2.setName("Mordini");
        System.out.println("Hello World");
        System.out.println(ug_example.getName());
        System.out.println(ug_example2.getName());
    }
}

