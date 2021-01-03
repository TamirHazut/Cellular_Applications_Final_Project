package com.example.lets_plan.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.lets_plan.logic.DataHandler;

public abstract class Fragment_Container extends Fragment_Base {
    protected int containerViewId;
    protected boolean hasChildFragments;

    private void switchChildFragments(int containerViewId, Fragment fragment) {
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(containerViewId, fragment, fragment.getClass().getSimpleName())
                    .addToBackStack(getClass().getSimpleName())
                    .commit();
        }
    }

    protected abstract void changeButtonViews();

    protected abstract Fragment_Base createFragment();

    protected void changeCurrentView() {
        if (hasChildFragments) {
            DataHandler.getInstance().getRotateLoading().start();
            changeButtonViews();
            switchChildFragments(this.containerViewId, createFragment());
        }
    }

    protected void setContainerViewId(int containerViewId) {
        this.containerViewId = containerViewId;
    }

    protected void setHasChildFragments(boolean hasChildFragments) {
        this.hasChildFragments = hasChildFragments;
    }
}
