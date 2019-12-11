package com.example.javaimagefilter;


import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.javaimagefilter.Adapter.ThumbnailAdapter;
import com.example.javaimagefilter.Interface.FiltersListFragmentListener;
import com.example.javaimagefilter.Utils.BitmapUtils;
import com.example.javaimagefilter.Utils.SpacesItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.zomato.photofilters.FilterPack;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;
import com.zomato.photofilters.utils.ThumbnailsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersListFragment extends BottomSheetDialogFragment implements FiltersListFragmentListener{

    RecyclerView thumbnailRecyclerView;
    ThumbnailAdapter thumbnailAdapter;
    List<ThumbnailItem> thumbnailItems;
    FiltersListFragmentListener filtersListFragmentListener;

    static FiltersListFragment filtersListFragmentInstance;
    static Bitmap bitmap;

    public static FiltersListFragment getInstance(Bitmap bitmapSave){
        bitmap = bitmapSave;

        if(filtersListFragmentInstance == null){
            filtersListFragmentInstance = new FiltersListFragment();
        }
        return filtersListFragmentInstance;
    }

    public void setFiltersListFragmentListener(FiltersListFragmentListener filtersListFragmentListener) {
        this.filtersListFragmentListener = filtersListFragmentListener;
    }

    public FiltersListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView =  inflater.inflate(R.layout.fragment_filters_list, container, false);
        thumbnailItems = new ArrayList<>();
        thumbnailAdapter = new ThumbnailAdapter(thumbnailItems, this, getActivity());

        thumbnailRecyclerView = (RecyclerView)itemView.findViewById(R.id.recycler_view);
        thumbnailRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        thumbnailRecyclerView.setItemAnimator(new DefaultItemAnimator());

        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        thumbnailRecyclerView.addItemDecoration(new SpacesItemDecoration(space));
        thumbnailRecyclerView.setAdapter(thumbnailAdapter);

        displayThumbnail(bitmap);

        return itemView;
    }

    public void displayThumbnail(final Bitmap bitmap) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Bitmap thumbImg;
                if(bitmap == null){
                    thumbImg = BitmapUtils.getBitmapFromAssets(getActivity(), MainActivity.pictureName, 100, 100);
                }

                else{
                    thumbImg = Bitmap.createScaledBitmap(bitmap, 100, 100, false);
                }

                if(thumbImg == null){
                    return;
                }
                ThumbnailsManager.clearThumbs();
                thumbnailItems.clear();

                // add normal bitmap first
                ThumbnailItem thumbnailItem = new ThumbnailItem();
                thumbnailItem.image = thumbImg;
                thumbnailItem.filterName = "Normal";
                ThumbnailsManager.addThumb(thumbnailItem);

                List<Filter> filters = FilterPack.getFilterPack(getActivity());
                for(Filter filter : filters){
                    ThumbnailItem tI = new ThumbnailItem();
                    tI.image = thumbImg;
                    tI.filter = filter;
                    tI.filterName = filter.getName();
                    ThumbnailsManager.addThumb(tI);
                }

                thumbnailItems.addAll(ThumbnailsManager.processThumbs(getActivity()));
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        thumbnailAdapter.notifyDataSetChanged();
                    }
                });
            }
        };
        new Thread(r).start();
    }

    @Override
    public void onFilterSelected(Filter filter) {
        if(filtersListFragmentListener != null){
            filtersListFragmentListener.onFilterSelected(filter);
        }
    }
}
