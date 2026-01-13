package com.example.gymmateapplication;

public class Workout {
    private int id;
    private String name;
    private int reps;
    private int sets;

    public Workout(int id, String name, int reps, int sets) {
        this.id = id;
        this.name = name;
        this.reps = reps;
        this.sets = sets;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getReps() { return reps; }
    public int getSets() { return sets; }

}
