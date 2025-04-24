package de.vdvcount.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.vdvcount.app.R;
import de.vdvcount.app.common.OnItemClickListener;
import de.vdvcount.app.databinding.CountedStopTimeItemBinding;
import de.vdvcount.app.model.CountedStopTime;

public class CountedStopTimeListAdapter extends RecyclerView.Adapter<CountedStopTimeListAdapter.ViewHolder> {

    private List<CountedStopTime> countedStopTimeList;
    private OnItemClickListener<CountedStopTime> onItemClickListener;

    public CountedStopTimeListAdapter() {
        this.countedStopTimeList = new ArrayList<>();
    }

    public void setCountedStopTimeList(List<CountedStopTime> countedStopTimeList) {
        this.countedStopTimeList = countedStopTimeList;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<CountedStopTime> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountedStopTimeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.counted_stop_time_item, parent, false);
        return new CountedStopTimeListAdapter.ViewHolder(binding, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CountedStopTime obj = this.countedStopTimeList.get(position);
        holder.setCountedStopTime(obj);
        holder.setOnItemClickListener(this.onItemClickListener);
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, this.getItemCount());
    }

    @Override
    public int getItemCount() {
        return this.countedStopTimeList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CountedStopTimeItemBinding itemBinding;
        private PassengerCountingEventListAdapter passengerCountingEventListAdapter;

        public ViewHolder(CountedStopTimeItemBinding itemBinding, int viewType) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            this.passengerCountingEventListAdapter = new PassengerCountingEventListAdapter();

            this.itemBinding.viewTimeLine.initLine(viewType);
            this.itemBinding.lstPassengerCountingEvents.setAdapter(this.passengerCountingEventListAdapter);
        }

        public void setCountedStopTime(CountedStopTime obj) {
            this.itemBinding.setCountedStopTime(obj);

            if (obj.getDepartureTimestamp() != null && obj.getDepartureTimestamp().getTime() > 0) {
                this.itemBinding.setDisplayedTimestamp(obj.getDepartureTimestamp());
            } else {
                this.itemBinding.setDisplayedTimestamp(obj.getArrivalTimestamp());
            }

            this.itemBinding.executePendingBindings();

            this.passengerCountingEventListAdapter.setPassengerCountingEventList(obj.getPassengerCountingEvents());
        }

        public void setOnItemClickListener(final OnItemClickListener<CountedStopTime> listener) {
            this.itemBinding.getRoot().setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(this.itemBinding.getCountedStopTime());
                }
            });
        }
    }
}
