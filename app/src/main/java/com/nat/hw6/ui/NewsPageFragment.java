package com.nat.hw6.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nat.hw6.NewsViewModel;
import com.nat.hw6.R;
import com.nat.hw6.database.NewsItem;
import com.nat.hw6.database.Item;
import com.nat.hw6.ui.recycler_view.NewsActivityCallback;
import com.nat.hw6.ui.recycler_view.NewsAdapter;
import com.nat.hw6.ui.recycler_view.NewsItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



public class NewsPageFragment extends Fragment {
    public static final String NEWS_ARG = "items_arg";
    public static final String DEL_ARG = "del_arg";
    private static final String NEW_USER = "new_user_fragment";

    public NewsViewModel newsViewModel;

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private ArrayList<Item> news;


    public static NewsPageFragment newInstance(boolean last, boolean deletable) {
        Bundle args = new Bundle();
        args.putBoolean(NEWS_ARG, last);
        args.putBoolean(DEL_ARG, deletable);
        NewsPageFragment fragment = new NewsPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
            if (getArguments().getBoolean(NEWS_ARG)) {
                newsViewModel.getAllNews().observe(this, observer);
            } else {
                newsViewModel.getAllFavouritesNews().observe(this, observer);
            }
            news = getArguments().getParcelableArrayList(NEWS_ARG);
        }
        setRetainInstance(true);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity()!= null) {
            sharedPreferences = getActivity().getSharedPreferences(MainActivity.PREF, Context.MODE_PRIVATE);
            if (!sharedPreferences.contains(NEW_USER)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(NEW_USER, false);
                editor.apply();

                Toast.makeText(getContext(), R.string.instruction_swipe, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment, container, false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.addItemDecoration(
                new NewsItemDecoration(getResources().getDrawable(R.drawable.divider), 24, 48)
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        newsAdapter = new NewsAdapter(new NewsActivityCallback() {
            @Override
            public void startNewsActivity(Item item) {
                Context context = getContext();
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra(NewsActivity.NEWS_TAG, item);
                context.startActivity(intent);
            }
        });
        recyclerView.setAdapter(newsAdapter);

        if (getArguments().getBoolean(DEL_ARG)) {

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                @Override
                public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public int getSwipeDirs(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                    if (newsAdapter.getItemViewType(viewHolder.getAdapterPosition()) == Item.TYPE_DATE) {
                        return 0;
                    }
                    return super.getSwipeDirs(recyclerView, viewHolder);
                }

                @Override
                public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                    int newsIdToDelete = newsAdapter.getNewsAt(viewHolder.getAdapterPosition()).getId();
                    newsViewModel.deleteFavourite(newsIdToDelete);
                }
            });

            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
        return view;
    }

    private Observer<List<NewsItem>> observer = new Observer<List<NewsItem>>() {
        @Override
        public void onChanged(List<NewsItem> newsItems) {
            news = Utils.prepareData(newsItems);
            newsAdapter.refreshData(news);
        }
    };

}
