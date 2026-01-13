package com.example.gymmateapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutHistoryAdapter extends RecyclerView.Adapter<WorkoutHistoryAdapter.WorkoutViewHolder> {

    private ArrayList<Workout> workouts;
    private OnWorkoutLongClickListener listener;

    // ✅ Interface for long click
    public interface OnWorkoutLongClickListener {
        void onLongClick(Workout workout);
    }

    // ✅ Constructor with listener
    public WorkoutHistoryAdapter(ArrayList<Workout> workouts, OnWorkoutLongClickListener listener) {
        this.workouts = workouts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_history, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {

        Workout workout = workouts.get(position);

        holder.tvWorkoutName.setText(workout.getName());
        holder.tvReps.setText("Reps: " + workout.getReps());
        holder.tvSets.setText("Sets: " + workout.getSets());

        // ✅ Long press to delete
        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onLongClick(workout);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return workouts.size();
    }

    static class WorkoutViewHolder extends RecyclerView.ViewHolder {

        TextView tvWorkoutName, tvReps, tvSets;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            tvWorkoutName = itemView.findViewById(R.id.tvWorkoutName);
            tvReps = itemView.findViewById(R.id.tvReps);
            tvSets = itemView.findViewById(R.id.tvSets);
        }
    }
}
