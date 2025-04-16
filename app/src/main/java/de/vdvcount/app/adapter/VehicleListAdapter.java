package de.vdvcount.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import androidx.annotation.NonNull;
import de.vdvcount.app.databinding.VehicleItemBinding;
import de.vdvcount.app.model.Vehicle;

class VehicleListAdapter extends ArrayAdapter<Vehicle> {

    private final LayoutInflater inflater;

    public VehicleListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        
        this.inflater = LayoutInflater.from(context);
    }

    public void setVehicles(List<Vehicle> vehicleList) {
        this.clear();

        for (Vehicle vehicle : vehicleList) {
            this.insert(vehicle, this.getCount());
        }

        this.notifyDataSetChanged();
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
}
