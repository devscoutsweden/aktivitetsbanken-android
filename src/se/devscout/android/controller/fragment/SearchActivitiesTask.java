package se.devscout.android.controller.fragment;

import android.os.AsyncTask;
import se.devscout.server.api.ActivityBank;
import se.devscout.server.api.ActivityFilter;
import se.devscout.server.api.model.Activity;

import java.util.List;

public class SearchActivitiesTask extends AsyncTask<Void, Void, List<? extends Activity>> {
    private ActivityBank mRepo;
    private ActivityFilter mFilter;

    public SearchActivitiesTask(ActivityBank repo, ActivityFilter filter) {
        mRepo = repo;
        mFilter = filter;
    }

    @Override
    protected List<? extends Activity> doInBackground(Void... voids) {
        return mRepo.find(mFilter);
    }
}
