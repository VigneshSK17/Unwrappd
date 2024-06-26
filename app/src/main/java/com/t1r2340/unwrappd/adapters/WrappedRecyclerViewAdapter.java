package com.t1r2340.unwrappd.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.t1r2340.unwrappd.PrevWrappedActivity;
import com.t1r2340.unwrappd.R;
import com.t1r2340.unwrappd.models.api.Wrapped;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

public class WrappedRecyclerViewAdapter extends RecyclerView.Adapter<WrappedRecyclerViewAdapter.WrappedViewHolder> {

    private Context context;
    private List<Wrapped> wrappeds;
    private boolean isPremium;

    /**
     * Constructor for WrappedRecyclerViewAdapter
     * @param context the context of the adapter
     * @param wrappeds the list of wrappeds to display
     * @param isPremium whether the user is premium or not
     */
    public WrappedRecyclerViewAdapter(Context context, List<Wrapped> wrappeds, boolean isPremium) {
        this.context = context;
        this.wrappeds = wrappeds;
        this.isPremium = isPremium;
        Log.d("WRAPPED_RECYCLER_VIEW", "" + this.wrappeds.size());
    }

    @NonNull
    @Override
    public WrappedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.all_wrappeds_button_layout, parent, false);

        Log.d("WRAPPED_RECYCLER_VIEW_2", "" + wrappeds.size());

        return new WrappedViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return wrappeds.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull WrappedViewHolder holder, int position) {
        Wrapped wrapped = wrappeds.get(position);
        Log.d("WRAPPED_RECYCLER_VIEW", wrapped.toString());

        setCardColor(holder, wrapped.getColor());

        StringBuilder title = new StringBuilder();
        title.append("Your Wrapped for ");
        title.append(DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.US).format(wrapped.getGeneratedAt()));

        holder.wrappedTextView.setText(title.toString());

        holder.card.setOnClickListener(v -> {
            Intent intent = new Intent(context, PrevWrappedActivity.class);
            intent.putExtra("wrappedTitle", title.toString());
            intent.putExtra("wrapped", wrapped);
            intent.putExtra("isPremium", isPremium);
            context.startActivity(intent);
        });
    }

    public void setWrappeds(List<Wrapped> wrappeds) {
        this.wrappeds = wrappeds;
        Log.d("WRAPPED_RECYCLER_VIEW_SET", "" + this.wrappeds.size());
        notifyDataSetChanged();
    }

    private void setCardColor(@NonNull WrappedViewHolder holder, int color) {
        holder.card.setCardBackgroundColor(context.getColor(color));
    }

    public static class WrappedViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView wrappedTextView;

        public WrappedViewHolder(@NonNull View itemView) {
            super(itemView);

            this.card = itemView.findViewById(R.id.wrappedCardView);
            this.wrappedTextView = itemView.findViewById(R.id.wrappedTextView);
        }
    }



}
