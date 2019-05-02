package com.nat.hw6;

import android.app.Application;

import com.nat.hw6.database.FavouritesNews;
import com.nat.hw6.database.NewsItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class NewsViewModel extends AndroidViewModel {
    private NewsRepository repository;
    private LiveData<List<NewsItem>> allNews;
    private LiveData<List<NewsItem>> allFavouritesNews;
//    private Disposable disposableInsert;
//    private Disposable disposableDelete;

    public NewsViewModel(@NonNull Application application) {
        super(application);

        repository = new NewsRepository(application);
        allNews = LiveDataReactiveStreams.fromPublisher(
                        repository.getAllNews()
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
        );
        allFavouritesNews = LiveDataReactiveStreams.fromPublisher(
                repository.getAllFavouritesNews()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public void insertFavourites(FavouritesNews favouritesNews) {
        Single.just(favouritesNews)
                .doOnSuccess(value -> repository.insertFavourite(value))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public LiveData<Boolean> isFavorite(int id) {
        return LiveDataReactiveStreams.fromPublisher(
                repository.getFavouriteNews(id)
                        .isEmpty()
                        .toFlowable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
        );
    }

    public void deleteFavourite(int newsIdToDelete) {
        Single.just(newsIdToDelete)
                .doOnSuccess(value -> repository.deleteFavourite(value))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    public LiveData<List<NewsItem>> getAllNews() {
        return allNews;
    }

    public LiveData<List<NewsItem>> getAllFavouritesNews () {
        return allFavouritesNews;
    }

//    @Override
//    protected void onCleared() {
//        super.onCleared();
//        if (disposableInsert != null) {
//            disposableInsert.dispose();
//        }
//        if (disposableDelete != null) {
//            disposableDelete.dispose();
//        }
//    }
}
