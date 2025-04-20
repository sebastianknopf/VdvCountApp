package de.vdvcount.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.vdvcount.app.R;
import de.vdvcount.app.common.OnItemClickListener;
import de.vdvcount.app.databinding.PassengerCountingEventItemBinding;
import de.vdvcount.app.model.PassengerCountingEvent;

public class PassengerCountingEventListAdapter extends RecyclerView.Adapter<PassengerCountingEventListAdapter.ViewHolder> {

   private List<PassengerCountingEvent> passengerCountingEventList;
   private OnItemClickListener<PassengerCountingEvent> onItemClickListener;

   public PassengerCountingEventListAdapter() {
      this.passengerCountingEventList = new ArrayList<>();
   }

   public void setPassengerCountingEventList(List<PassengerCountingEvent> passengerCountingEventList) {
      this.passengerCountingEventList = passengerCountingEventList;
      this.notifyDataSetChanged();
   }

   public void setOnItemClickListener(OnItemClickListener<PassengerCountingEvent> onItemClickListener) {
      this.onItemClickListener = onItemClickListener;
   }

   @NonNull
   @Override
   public PassengerCountingEventListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      PassengerCountingEventItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.passenger_counting_event_item, parent, false);
      return new PassengerCountingEventListAdapter.ViewHolder(binding);
   }

   @Override
   public void onBindViewHolder(@NonNull PassengerCountingEventListAdapter.ViewHolder holder, int position) {
      PassengerCountingEvent obj = this.passengerCountingEventList.get(position);
      holder.setPassengerCountingEvent(obj);
      holder.setOnItemClickListener(this.onItemClickListener);
   }

   @Override
   public int getItemCount() {
      return this.passengerCountingEventList.size();
   }

   static class ViewHolder extends RecyclerView.ViewHolder {

      public PassengerCountingEventItemBinding itemBinding;

      public ViewHolder(PassengerCountingEventItemBinding itemBinding) {
         super(itemBinding.getRoot());
         this.itemBinding = itemBinding;
      }

      public void setPassengerCountingEvent(PassengerCountingEvent obj) {
         this.itemBinding.setPassengerCountingEvent(obj);
         this.itemBinding.executePendingBindings();
      }

      public void setOnItemClickListener(final OnItemClickListener<PassengerCountingEvent> listener) {
         this.itemBinding.getRoot().setOnClickListener(view -> {
            if (listener != null) {
               listener.onItemClick(this.itemBinding.getPassengerCountingEvent());
            }
         });
      }
   }

}
