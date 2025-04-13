package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.R;
import de.vdvcount.app.databinding.StopItemBinding;
import de.vdvcount.app.model.Station;

public class StopListAdapter extends RecyclerView.Adapter<StopListAdapter.ViewHolder> {

    private List<Station> stationList;

    public StopListAdapter() {
        this.stationList = new ArrayList<>();
    }

    public StopListAdapter(List<Station> stationList) {
        this.stationList = stationList;
    }

    public void setStationList(List<Station> stationList) {
        this.stationList = stationList;
        this.notifyDataSetChanged();
    }

    /*public void setManualValidationListener(OnManualValidationListener listener) {
        if (listener != null) {
            this.manualValidationListener = listener;
        }
    }*/

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        StopItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.stop_item, parent, false);
        return new StopListAdapter.ViewHolder(parent.getContext(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Station obj = this.stationList.get(position);
        holder.setStation(obj, position);
        //holder.setOnManualValidationListener(this.manualValidationListener);*/
    }

    @Override
    public int getItemCount() {
        return this.stationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private Station station;
        private final StopItemBinding itemBinding;
        //private OnManualValidationListener manualValidationListener;

        public ViewHolder(Context context, StopItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.context = context;
            this.itemBinding = itemBinding;
        }

        public void setStation(Station station, int index) {
            this.station = station;
            this.itemBinding.setStation(this.station);
        }

        /*public void setOnManualValidationListener(OnManualValidationListener listener) {
            this.manualValidationListener = listener;
        }*/
    }
}
