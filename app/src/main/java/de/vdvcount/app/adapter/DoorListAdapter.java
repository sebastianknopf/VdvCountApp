package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.vdvcount.app.R;
import de.vdvcount.app.common.OnItemSelectListener;
import de.vdvcount.app.databinding.DoorItemBinding;

public class DoorListAdapter extends RecyclerView.Adapter<DoorListAdapter.ViewHolder> {

    private List<String> doorList;
    private List<Integer> selectedDoorIndices;

    private OnItemSelectListener<String> onItemSelectListener;

    public DoorListAdapter() {
        this.doorList = new ArrayList<>();
        this.selectedDoorIndices = new ArrayList<>();
    }

    public void setOnItemSelectListener(OnItemSelectListener<String> onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    public void setDoorList(List<String> doorList) {
        this.doorList = doorList;
        this.selectedDoorIndices = new ArrayList<>();

        this.notifyDataSetChanged();
    }

    public List<String> getSelectedDoorList() {
        List<String> selectedDoorList = new ArrayList<>();

        Collections.sort(this.selectedDoorIndices);
        for (int index : this.selectedDoorIndices) {
            selectedDoorList.add(this.doorList.get(index));
        }

        return selectedDoorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        DoorItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.door_item, parent, false);
        return new DoorListAdapter.ViewHolder(parent.getContext(), binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String obj = this.doorList.get(position);
        holder.setDoorId(obj);
        holder.setOnItemSelectListener((item, selected) -> {
            if (selected) {
                this.selectedDoorIndices.add(item);
            } else {
                this.selectedDoorIndices.remove(item);
            }

            if (this.onItemSelectListener != null) {
                this.onItemSelectListener.onItemSelected(this.doorList.get(item), selected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.doorList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        private final DoorItemBinding itemBinding;
        private OnItemSelectListener<Integer> onItemSelectListener;

        public ViewHolder(Context context, DoorItemBinding itemBinding) {
            super(itemBinding.getRoot());

            this.context = context;
            this.itemBinding = itemBinding;

            this.itemBinding.cbxDoorSelected.setOnCheckedChangeListener((compoundButton, checked) -> {
                int index = this.getAdapterPosition();
                if (index != RecyclerView.NO_POSITION && this.onItemSelectListener != null) {
                    this.onItemSelectListener.onItemSelected(index, checked);
                }
            });

            this.itemBinding.getRoot().setOnClickListener(view -> {
                this.itemBinding.cbxDoorSelected.toggle();
            });
        }

        public void setDoorId(String doorId) {
            this.itemBinding.setDoorId(doorId);
        }

        public void setOnItemSelectListener(OnItemSelectListener<Integer> onItemSelectListener) {
            this.onItemSelectListener = onItemSelectListener;
        }
    }
}
