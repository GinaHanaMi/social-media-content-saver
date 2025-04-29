package com.example.socialmediacontentsaver.mainView.feedActivityClasses;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.socialmediacontentsaver.R;
import com.example.socialmediacontentsaver.databaseHelpers.AppDatabaseHelper;
import com.example.socialmediacontentsaver.databaseHelpers.ContentDatabaseHelper;
import com.example.socialmediacontentsaver.models.ContentModel;
import com.example.socialmediacontentsaver.receiveData.ReceiveDataActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FragmentOne extends Fragment implements FeedRecyclerViewInterface {
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
        setupActionOnFeedContentDialog();

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

        adapter = new FeedActivityRecyclerViewAdapter(requireContext(), contentModels, this);
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

    @Override
    public void onFeedItemClick(int position) {
        View dialogView = getLayoutInflater().inflate(R.layout.feed_content_dialog, null);

        Button feedDialogOpenContentButton = dialogView.findViewById(R.id.feedDialogOpenContentButton);
        Button feedDialogEditContentButton = dialogView.findViewById(R.id.feedDialogEditContentButton);
        Button feedDialogShareContentButton = dialogView.findViewById(R.id.feedDialogShareContentButton);
        Button feedDialogDeleteContentButton = dialogView.findViewById(R.id.feedDialogDeleteContentButton);

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        dialog.show();

        feedDialogOpenContentButton.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(contentModels.get(position).getLink()));
            startActivity(browserIntent);
            dialog.dismiss();
        });

        feedDialogEditContentButton.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "feedEdit",
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

        feedDialogShareContentButton.setOnClickListener(view -> {
            Intent sendContentURLIntent = new Intent();
            sendContentURLIntent.setAction(Intent.ACTION_SEND);
            sendContentURLIntent.putExtra(Intent.EXTRA_TEXT, contentModels.get(position).getLink());
            sendContentURLIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendContentURLIntent, null);
            startActivity(shareIntent);

            dialog.dismiss();
        });

        feedDialogDeleteContentButton.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "feedDelete",
                    Toast.LENGTH_LONG).show();
            dialog.dismiss();
        });

    }
    private void setupActionOnFeedContentDialog() {
    }
}
