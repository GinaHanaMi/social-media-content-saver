package com.example.socialmediacontentsaver.mainView.feedActivityClasses;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.models.ContentModel;

import java.util.ArrayList;

public class FragmentOne extends Fragment {
    ContentDatabaseHelper contentDatabase;
    FeedActivityRecyclerViewAdapter adapter;
    ArrayList<ContentModel> contentModels = new ArrayList<>();
    SearchView feedSearchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(requireContext());
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();
        contentDatabase = new ContentDatabaseHelper(db);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FeedPopulateLayoutWithFolders();
    }

    public void FeedPopulateLayoutWithFolders() {
        contentModels.clear();
        Cursor res = contentDatabase.getAllContent();

        RecyclerView recyclerView = getView().findViewById(R.id.feedRecyclerView);
        feedSearchView = getView().findViewById(R.id.feedSearchView); // ✅ Correctly assigning the SearchView

        feedSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                feedFilter(newText);
                return true;
            }
        });

        while (res.moveToNext()) {
            contentModels.add(new ContentModel(
                    res.getString(0), res.getString(1), res.getString(2),
                    res.getString(3), res.getString(4), res.getString(5), res.getString(6)));
        }

        adapter = new FeedActivityRecyclerViewAdapter(requireContext(), contentModels);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }


    private void feedFilter(String newText) {
        ArrayList<ContentModel> feedFilteredList = new ArrayList<>();
        for (ContentModel singleItem : contentModels) {
            if (singleItem.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getDescription().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getPlatform().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getSave_date().toLowerCase().contains(newText.toLowerCase())) {
                feedFilteredList.add(singleItem);
            }
        }
        adapter.feedFilterList(feedFilteredList);
    }
}
