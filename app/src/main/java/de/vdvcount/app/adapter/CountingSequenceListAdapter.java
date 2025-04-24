package de.vdvcount.app.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.CountingSequenceItemBinding;
import de.vdvcount.app.model.CountingSequence;

public class CountingSequenceListAdapter extends RecyclerView.Adapter<CountingSequenceListAdapter.ViewHolder> {

    private List<CountingSequence> countingSequenceList;

    public CountingSequenceListAdapter() {
        this.countingSequenceList = new ArrayList<>();
    }

    public void setCountingSequenceList(List<CountingSequence> passengerCountingEventList) {
        this.countingSequenceList = passengerCountingEventList;
        this.notifyDataSetChanged();
    }

    public List<CountingSequence> getCountingSequenceList() {
        return this.countingSequenceList;
    }

    @NonNull
    @Override
    public CountingSequenceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CountingSequenceItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.counting_sequence_item, parent, false);
        return new CountingSequenceListAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CountingSequenceListAdapter.ViewHolder holder, int position) {
        CountingSequence obj = this.countingSequenceList.get(position);
        holder.setCountingSequence(obj);
    }

    @Override
    public int getItemCount() {
        return this.countingSequenceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        private CountingSequence countingSequence;
        public CountingSequenceItemBinding itemBinding;

        public ViewHolder(CountingSequenceItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding = itemBinding;
        }

        public void setCountingSequence(CountingSequence obj) {
            this.countingSequence = obj;

            this.itemBinding.setCountingSequence(obj);
            this.itemBinding.executePendingBindings();

            this.itemBinding.btnDecreaseIn.setOnClickListener(view -> {
                int in = this.countingSequence.getIn();
                if (in > 0) {
                    this.countingSequence.setIn(--in);
                }

                this.updateInOutView();
            });

            this.itemBinding.btnIncreaseIn.setOnClickListener(view -> {
                int in = this.countingSequence.getIn();
                this.countingSequence.setIn(++in);

                this.updateInOutView();
            });

            this.itemBinding.btnDecreaseOut.setOnClickListener(view -> {
                int out = this.countingSequence.getOut();
                if (out > 0) {
                    this.countingSequence.setOut(--out);
                }

                this.updateInOutView();
            });

            this.itemBinding.btnIncreaseOut.setOnClickListener(view -> {
                int out = this.countingSequence.getOut();
                this.countingSequence.setOut(++out);

                this.updateInOutView();
            });

            this.updateInOutView();
        }

        private void updateInOutView() {
            this.itemBinding.edtIn.setText(String.valueOf(this.countingSequence.getIn()));
            this.itemBinding.edtOut.setText(String.valueOf(this.countingSequence.getOut()));
        }
    }
}
