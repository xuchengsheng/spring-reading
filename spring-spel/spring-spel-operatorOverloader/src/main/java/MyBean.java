public class MyBean {

    private int age;

    public MyBean(int age) {
        this.age = age;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "MyBean{" +
                "age=" + age +
                '}';
    }
}
