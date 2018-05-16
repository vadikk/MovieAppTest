package com.example.vadym.movieapp.dagger.favorite;

import com.example.vadym.movieapp.mvp.favorite.FavoriteContract;
import com.example.vadym.movieapp.mvp.favorite.FavoriteModelImpl;
import com.example.vadym.movieapp.mvp.favorite.FavoritePresenterImpl;
import com.example.vadym.movieapp.room.MovieListModel;
import com.google.firebase.firestore.DocumentReference;

import dagger.Module;
import dagger.Provides;

@Module
public class MvpFavoriteModule {

    private FavoriteContract.FavoriteView view;

    public MvpFavoriteModule(FavoriteContract.FavoriteView view) {
        this.view = view;
    }

    @Provides
    FavoriteContract.FavoriteView getView(){
        return view;
    }

    @Provides
    FavoriteContract.FavoriteModel getModel(DocumentReference firestoreDB, MovieListModel viewModel){
        return new FavoriteModelImpl(firestoreDB, viewModel);
    }

    @Provides
    FavoriteContract.FavoritePresenter getPresenter(FavoriteContract.FavoriteView view, FavoriteContract.FavoriteModel model){
        return new FavoritePresenterImpl(view,model);
    }
}
