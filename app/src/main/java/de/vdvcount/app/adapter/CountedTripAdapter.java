package de.vdvcount.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import de.vdvcount.app.R;
import de.vdvcount.app.common.OnItemClickListener;
import de.vdvcount.app.databinding.AdditionalStopItemBinding;
import de.vdvcount.app.databinding.CountedStopTimeItemBinding;
import de.vdvcount.app.model.AdditionalStop;
import de.vdvcount.app.model.CountedStopTime;
import de.vdvcount.app.model.CountedTrip;
import de.vdvcount.app.model.ICountable;
import de.vdvcount.app.model.PassengerCountingEvent;

public class CountedTripAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static int ITEM_TYPE_COUNTED_STOP_TIME = 997;
    private final static int ITEM_TYPE_ADDITIONAL_STOP = 998;

    private List<ICountable> countedTripStopsList;
    private OnItemClickListener<CountedStopTime> onItemClickListener;

    public CountedTripAdapter() {
        this.countedTripStopsList = new ArrayList<>();
    }

    public void setCountedTrip(CountedTrip countedTrip) {
        this.countedTripStopsList = new ArrayList<>();
        for (CountedStopTime countedStopTime : countedTrip.getCountedStopTimes()) {
            this.countedTripStopsList.add(countedStopTime);

            List<PassengerCountingEvent> unmatchedPassengerCountingEvents = countedTrip
                    .getUnmatchedPassengerCountingEvents()
                    .stream()
                    .filter(passengerCountingEvent -> passengerCountingEvent.getAfterStopSequence() == countedStopTime.getSequence())
                    .collect(Collectors.toList());

            for (PassengerCountingEvent unmatchedPassengerCountingEvent : unmatchedPassengerCountingEvents) {
                AdditionalStop additionalStop = new AdditionalStop();
                additionalStop.setPassengerCountingEvent(unmatchedPassengerCountingEvent);

                this.countedTripStopsList.add(additionalStop);
            }
        }

        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener<CountedStopTime> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // view types 1 and 2 are indicators for timelineview start (1) and end (2)
        // first and last element must always be a CountedStopTime object, no AdditionalStop!
        if (viewType == ITEM_TYPE_COUNTED_STOP_TIME || viewType == 1 || viewType == 2) {
            CountedStopTimeItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.counted_stop_time_item, parent, false);
            return new CountedStopTimeViewHolder(binding, viewType);
        } else if (viewType == ITEM_TYPE_ADDITIONAL_STOP) {
            AdditionalStopItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.additional_stop_item, parent, false);
            return new AdditionalStopViewHolder(binding);
        } else {
            throw new RuntimeException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ICountable obj = this.countedTripStopsList.get(position);

        if (holder instanceof CountedStopTimeViewHolder) {
            CountedStopTimeViewHolder countedStopTimeViewHolder = (CountedStopTimeViewHolder) holder;
            countedStopTimeViewHolder.setCountedStopTime((CountedStopTime) obj);
            countedStopTimeViewHolder.setOnItemClickListener(this.onItemClickListener);
        } else if (holder instanceof AdditionalStopViewHolder) {
            AdditionalStopViewHolder additionalStopViewHolder = (AdditionalStopViewHolder) holder;
            additionalStopViewHolder.setAdditionalStop((AdditionalStop) obj);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == this.getItemCount() - 1) {
            return TimelineView.getTimeLineViewType(position, this.getItemCount());
        } else {
            ICountable countable = this.countedTripStopsList.get(position);
            if (countable instanceof CountedStopTime) {
                return ITEM_TYPE_COUNTED_STOP_TIME;
            } else if (countable instanceof AdditionalStop) {
                return ITEM_TYPE_ADDITIONAL_STOP;
            } else {
                return 0;
            }
        }
    }

    @Override
    public int getItemCount() {
        return this.countedTripStopsList.size();
    }

    static class CountedStopTimeViewHolder extends RecyclerView.ViewHolder {

        private CountedStopTimeItemBinding itemBinding;
        private PassengerCountingEventListAdapter passengerCountingEventListAdapter;

        public CountedStopTimeViewHolder(CountedStopTimeItemBinding itemBinding, int viewType) {
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

    static class AdditionalStopViewHolder extends RecyclerView.ViewHolder {

        private AdditionalStopItemBinding itemBinding;
        private PassengerCountingEventListAdapter passengerCountingEventListAdapter;

        public AdditionalStopViewHolder(AdditionalStopItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
            this.passengerCountingEventListAdapter = new PassengerCountingEventListAdapter();

            this.itemBinding.lstPassengerCountingEvents.setAdapter(this.passengerCountingEventListAdapter);
        }

        public void setAdditionalStop(AdditionalStop obj) {
            this.itemBinding.setAdditionalStop(obj);
            this.itemBinding.executePendingBindings();

            this.passengerCountingEventListAdapter.setPassengerCountingEventList(obj.getPassengerCountingEvents());
        }
    }
}
