package com.example.socialmediacontentsaver.mainView.foldersActivityClasses;

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
import com.example.socialmediacontentsaver.databaseHelpers.FolderDatabaseHelper;
import com.example.socialmediacontentsaver.models.ContentModel;
import com.example.socialmediacontentsaver.models.FolderModel;

import java.util.ArrayList;


public class FragmentTwo extends Fragment {
    FolderDatabaseHelper mainFolderDatabase;
    FoldersActivityRecyclerViewAdapter mainAdapter;
    ArrayList<FolderModel> mainFoldersModels = new ArrayList<>();
    SearchView foldersSearchView;

    @Override
    public void onResume() {
        super.onResume();
        FoldersPopulateLayoutWithFolders();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        AppDatabaseHelper appDatabaseHelper = new AppDatabaseHelper(requireContext());
        SQLiteDatabase db = appDatabaseHelper.getWritableDatabase();
        mainFolderDatabase = new FolderDatabaseHelper(db);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        FoldersPopulateLayoutWithFolders();
    }

    public void FoldersPopulateLayoutWithFolders() {
        mainFoldersModels.clear();
        Cursor res = mainFolderDatabase.getAllFolders();

        RecyclerView mainRecyclerView = getView().findViewById(R.id.folderRecyclerView);
        foldersSearchView = getView().findViewById(R.id.folderSearchView);

        foldersSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                foldersFilter(newText);
                return true;
            }
        });

        while (res.moveToNext()) {
            mainFoldersModels.add(new FolderModel(res.getString(0), res.getString(1), res.getString(2), res.getString(3), res.getString(4)));
        }

        mainAdapter = new FoldersActivityRecyclerViewAdapter(requireContext(), mainFoldersModels);

        mainRecyclerView.setAdapter(mainAdapter);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
    }
    private void foldersFilter(String newText) {
        ArrayList<FolderModel> foldersFilteredList = new ArrayList<>();
        for (FolderModel singleItem : mainFoldersModels) {
            if (singleItem.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getDescription().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getTitle().toLowerCase().contains(newText.toLowerCase()) ||
                    singleItem.getCreated_at().toLowerCase().contains(newText.toLowerCase())) {
                foldersFilteredList.add(singleItem);
            }
        }
        mainAdapter.foldersFilterList(foldersFilteredList);
    }
}