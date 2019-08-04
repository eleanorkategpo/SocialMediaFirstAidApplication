package com.example.socialmediafirstaidapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FirstAidRequestAdapter extends RecyclerView.Adapter<FirstAidRequestAdapter.FirstAidRequestViewHolder>{

    private Context mCtx;
    private List<FirstAidRequest> firstAidRequestList;

    public FirstAidRequestAdapter(Context mCtx, List<FirstAidRequest> firstAidRequestList) {
        this.mCtx = mCtx;
        this.firstAidRequestList = firstAidRequestList;
    }

    @NonNull
    @Override
    public FirstAidRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_requests_recycler_view, null);
        return new FirstAidRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAidRequestViewHolder holder, int position) {
        FirstAidRequest request = firstAidRequestList.get(position);
        holder.situationTV.setText(request.getSituation());
       holder.nameTV.setText(/*request.getName()*/ "Temporary Name") ;
        holder.currentStatusTV.setText(request.getStatus() == 1 ?  "Submitted" : (request.getStatus() == 2 ? "Accepted" : "Resolved") );
        holder.addressTV.setText(request.getLongitude() + " " + request.getLatitude());
    }

    @Override
    public int getItemCount() {
        return firstAidRequestList.size();
    }

    class FirstAidRequestViewHolder extends RecyclerView.ViewHolder {

        TextView situationTV, nameTV, currentStatusTV, addressTV;

        public FirstAidRequestViewHolder(@NonNull View itemView) {
            super(itemView);

            situationTV = itemView.findViewById(R.id.situationTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            currentStatusTV = itemView.findViewById(R.id.currentStatusTV);
            addressTV = itemView.findViewById(R.id.addressTV);
        }
    }
}
