package com.nat.hw6.ui.recycler_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.nat.hw6.R;
import com.nat.hw6.database.DateItem;
import com.nat.hw6.database.NewsItem;
import com.nat.hw6.database.Item;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Item> news = new ArrayList<Item>();
    private LinearLayout newsItem;
    private NewsActivityCallback newsActivityCallback;

    public NewsAdapter(NewsActivityCallback newsActivityCallback) {
        this.newsActivityCallback = newsActivityCallback;
    }

    public void refreshData(ArrayList<Item> newData){
        news.clear();
        news = newData;
        notifyDataSetChanged();
    }

    @Override
    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        switch (viewType) {
            case Item.TYPE_NEWS: {
                View view = LayoutInflater.from(context).inflate(R.layout.news, parent, false);
                final NewsViewHolder newsViewHolder = new NewsViewHolder(view);

                newsItem = newsViewHolder.newsItem;
                newsItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int pos = newsViewHolder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            Item item = news.get(pos);
                            newsActivityCallback.startNewsActivity(item);
                        }
                    }
                });

                return newsViewHolder;
            }
            case Item.TYPE_DATE: {
                View view = LayoutInflater.from(context).inflate(R.layout.date, parent, false);
                return new DateViewHolder(view);
            }
            default:
                throw new IllegalArgumentException("Unknown viewType=" + viewType);

        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
        switch (viewType) {
            case Item.TYPE_NEWS: {
                NewsItem newsItem = (NewsItem) news.get(position);
                NewsViewHolder vh = (NewsViewHolder) holder;
                vh.textTitle.setText(newsItem.getTitle());
                vh.textDesc.setText(newsItem.getDescription());
                break;
            }
            case Item.TYPE_DATE: {
                DateItem date = (DateItem) news.get(position);
                DateViewHolder vh = (DateViewHolder) holder;
                vh.textDate.setText(date.getDate());
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown viewType=" + viewType);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return news.get(position) instanceof NewsItem
                ? Item.TYPE_NEWS
                : Item.TYPE_DATE;
    }

    @Override
    public int getItemCount() {
        return news.size();
    }

    public NewsItem getNewsAt(int position) {
        return (NewsItem) news.get(position);
    }

}
