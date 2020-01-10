package com.bcabuddies.blogair.welcome;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class WelcomeViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private Context context;
    private View baseLayout;

    public WelcomeViewModelFactory(Context context, View baseLayout) {
        this.context = context;
        this.baseLayout = baseLayout;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new WelcomeViewModel(context, baseLayout);
    }
}
