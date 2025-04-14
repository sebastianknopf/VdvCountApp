package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.vdvcount.app.R;
import de.vdvcount.app.common.OnItemClickListener;
import de.vdvcount.app.databinding.DepartureItemBinding;
import de.vdvcount.app.model.Departure;

public class DepartureListAdapter extends RecyclerView.Adapter<DepartureListAdapter.ViewHolder> {

    private List<Departure> departureList;
    private OnItemClickListener<Departure> onItemClickListener;

    public DepartureListAdapter() {
        this.departureList = new ArrayList<Departure>();
    }

    public DepartureListAdapter(List<Departure> departureList) {
        this.departureList = departureList;
    }

    public void setDepartureList(List<Departure> departureList) {
        this.departureList = departureList;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<Departure> listener) {
        if (listener != null) {
            this.onItemClickListener = listener;
        }
    }

    @NonNull
    @Override
    public DepartureListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DepartureItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.departure_item, parent, false);
        return new DepartureListAdapter.ViewHolder(parent.getContext(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull DepartureListAdapter.ViewHolder holder, int position) {
        Departure obj = this.departureList.get(position);
        holder.setDeparture(obj, position);
        holder.setOnItemClickListener(this.onItemClickListener);
    }

    @Override
    public int getItemCount() {
        return this.departureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private Departure departure;
        private final DepartureItemBinding itemBinding;

        public ViewHolder(Context context, DepartureItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.context = context;
            this.itemBinding = itemBinding;
        }

        public void setDeparture(Departure station, int index) {
            this.departure = station;
            this.itemBinding.setDeparture(this.departure);
        }

        public void setOnItemClickListener(OnItemClickListener<Departure> listener) {
            this.itemBinding.getRoot().setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(this.itemBinding.getDeparture());
                }
            });
        }
    }
}
