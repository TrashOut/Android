package me.trashout.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.trashout.R;
import me.trashout.model.Organization;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Organization> mData = new ArrayList<>();
    private LayoutInflater mInflater;

    public CompanyAdapter(Context context, List<Organization> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_company, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position).getName();
        holder.companyTextView.setText(animal);
    }

    // total number of cells
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyTextView;

        ViewHolder(View itemView) {
            super(itemView);
            companyTextView = itemView.findViewById(R.id.txt_company_name);
        }
    }
}