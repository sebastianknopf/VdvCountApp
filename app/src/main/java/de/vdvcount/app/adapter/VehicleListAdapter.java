package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import de.vdvcount.app.R;
import de.vdvcount.app.databinding.VehicleItemBinding;
import de.vdvcount.app.model.Vehicle;

public class VehicleListAdapter extends ArrayAdapter<Vehicle> implements Filterable {

    private List<Vehicle> vehicleList;
    private final LayoutInflater inflater;
    private final Filter filter;

    public VehicleListAdapter(@NonNull Context context) {
        super(context, R.layout.vehicle_item);

        this.vehicleList = new ArrayList<>();
        this.inflater = LayoutInflater.from(context);

        this.filter = new Filter() {
            @Override
            public String convertResultToString(Object resultValue) {
                return ((Vehicle)resultValue).getName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint != null) {
                    ArrayList<Vehicle> suggestions = new ArrayList<Vehicle>();
                    for (Vehicle vehicle : vehicleList) {
                        if (vehicle.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            suggestions.add(vehicle);
                        }
                    }

                    results.values = suggestions;
                    results.count = suggestions.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();

                if (results != null && results.count > 0) {
                    addAll((ArrayList<Vehicle>) results.values);
                } else {
                    addAll(vehicleList);
                }

                notifyDataSetChanged();
            }
        };
    }

    public void setVehicles(List<Vehicle> vehicleList) {
        this.clear();

        for (Vehicle vehicle : vehicleList) {
            this.insert(vehicle, this.getCount());
        }

        this.notifyDataSetChanged();

        this.vehicleList = vehicleList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VehicleItemBinding dataBinding;

        if (convertView == null) {
            dataBinding = VehicleItemBinding.inflate(this.inflater, parent, false);
            convertView = dataBinding.getRoot();
            convertView.setTag(dataBinding);
        } else {
            dataBinding = (VehicleItemBinding) convertView.getTag();
        }

        Vehicle vehicle = this.getItem(position);

        dataBinding.setVehicle(vehicle);
        dataBinding.executePendingBindings();

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return this.filter;
    }
}
