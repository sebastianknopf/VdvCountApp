package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import de.vdvcount.app.model.Departure;

public class StopListAdapter extends RecyclerView.Adapter<StopListAdapter.ViewHolder> {

    private List<Departure> departureList;

    public StopListAdapter() {
        this.departureList = new ArrayList<>();
    }

    public StopListAdapter(List<Departure> departureList) {
        this.departureList = departureList;
    }

    public void setDepartureList(List<Departure> departureList) {
        this.departureList = departureList;
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
        /*TicketItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.ticket_item, parent, false);
        return new StopListAdapter.ViewHolder(parent.getContext(), binding);*/
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Departure obj = this.departureList.get(position);
        /*holder.setTicket(obj, position);
        holder.setOnManualValidationListener(this.manualValidationListener);*/
    }

    @Override
    public int getItemCount() {
        return this.departureList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private Departure departure;
        //private final TicketItemBinding itemBinding;
        //private OnManualValidationListener manualValidationListener;

        public ViewHolder(Context context/*, TicketItemBinding itemBinding*/) {
            super(null);

            this.context = context;
            //this.itemBinding = itemBinding;
        }

        public void setDeparture(Departure departure, int index) {
            this.departure = departure;
        }

        /*public void setOnManualValidationListener(OnManualValidationListener listener) {
            this.manualValidationListener = listener;
        }*/
    }
}
