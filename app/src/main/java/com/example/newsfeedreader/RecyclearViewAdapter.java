package com.example.newsfeedreader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclearViewAdapter extends RecyclerView.Adapter<RecyclearViewAdapter.ViewHolder>{
    private static final String TAG = "RecyclearViewAdapter";

    private Context context;
    private ArrayList<NewsItem> newsItems = new ArrayList<>();

    public RecyclearViewAdapter() {
    }

    public RecyclearViewAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_item,parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: started");
        holder.txtDate.setText(newsItems.get(position).getDate());
        holder.txtTitle.setText(newsItems.get(position).getTitle());
        holder.txtDesc.setText(newsItems.get(position).getDesc());

        holder.parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", newsItems.get(position).getLink());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtDate, txtDesc, txtTitle;
        private CardView parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtDate = (TextView)itemView.findViewById(R.id.textDate);
            txtDesc = (TextView)itemView.findViewById(R.id.texDesc);
            txtTitle = (TextView)itemView.findViewById(R.id.txtTitle);
            parent = (CardView)itemView.findViewById(R.id.parent);
        }
    }

    public void setNewsItems(ArrayList<NewsItem> newsItems) {
        this.newsItems = newsItems;
        notifyDataSetChanged();
    }
}
