package com.example.socialmediafirstaidapplication;

import android.content.Context;
import android.location.Geocoder;
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
    private OnRequestListener mOnRequestListener;


    public FirstAidRequestAdapter(Context mCtx, List<FirstAidRequest> firstAidRequestList, OnRequestListener onRequestListener) {
        this.mCtx = mCtx;
        this.firstAidRequestList = firstAidRequestList;
        this.mOnRequestListener = onRequestListener;
    }

    @NonNull
    @Override
    public FirstAidRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_requests_recycler_view, null);
        return new FirstAidRequestViewHolder(view, mOnRequestListener);
    }

    @Override
    public void onBindViewHolder(@NonNull FirstAidRequestViewHolder holder, int position) {
        FirstAidRequest request = firstAidRequestList.get(position);
        holder.requestIdTV.setText("Request ID: " + request.getId());
        holder.situationTV.setText(request.getSituation());
        holder.nameTV.setText("Name: " + request.getName()) ;
        holder.currentStatusTV.setText(request.getStatus() == 1 ?  "Submitted" : (request.getStatus() == 2 ? "Accepted" : "Resolved") );
        holder.addressTV.setText("Address: " + request.getFormattedAddress());
    }

    @Override
    public int getItemCount() {
        return firstAidRequestList.size();
    }

    class FirstAidRequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView situationTV, nameTV, currentStatusTV, addressTV, requestIdTV;
        OnRequestListener onRequestListener;

        public FirstAidRequestViewHolder(@NonNull View itemView, OnRequestListener onRequestListener) {
            super(itemView);

            requestIdTV = itemView.findViewById(R.id.requestIdTV);
            situationTV = itemView.findViewById(R.id.situationTV);
            nameTV = itemView.findViewById(R.id.nameTV);
            currentStatusTV = itemView.findViewById(R.id.currentStatusTV);
            addressTV = itemView.findViewById(R.id.addressTV);
            this.onRequestListener = onRequestListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onRequestListener.onRequestClick(getAdapterPosition());
        }
    }

    public interface OnRequestListener {
        void onRequestClick(int position);
    }
}
