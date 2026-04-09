package model;

public class Person implements Displayable {

    protected int id;

    public String name;

    String registrationDate;

    public Person(String name) {
        this.id = id;
        this.name = name;
        this.registrationDate = new java.util.Date().toString();
    }

    @Override
    public void showDetails() {
        System.out.println("Person ID: " + id + " | Name: " + name);
    }
}