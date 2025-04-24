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

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.models.ContentModel;

import java.util.ArrayList;

public class fragmentOne extends Fragment {
    ContentDatabaseHelper contentDatabase;
    feedActivityRecyclerViewAdapter adapter;
    ArrayList<ContentModel> contentModels = new ArrayList<>();

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

        while (res.moveToNext()) {
            contentModels.add(new ContentModel(res.getString(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4), res.getString(5), res.getString(6)));
        }

        adapter = new feedActivityRecyclerViewAdapter(requireContext(), contentModels);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
}
