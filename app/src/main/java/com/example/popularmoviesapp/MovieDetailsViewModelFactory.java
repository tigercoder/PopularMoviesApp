package com.example.popularmoviesapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovieDetailsViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    final private int mFavoriteId;

    MovieDetailsViewModelFactory(int favoriteId) {
        mFavoriteId = favoriteId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieDetailsViewModel(mFavoriteId);
    }
}
