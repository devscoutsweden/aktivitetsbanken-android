package se.devscout.android.util;

import android.content.Context;
import se.devscout.android.model.repo.SQLiteActivityRepo;
import se.devscout.server.api.ActivityBank;

public class ActivityBankFactory {
    private ActivityBankFactory() {
    }

    public static ActivityBank create(Context context) {
        return SQLiteActivityRepo.getInstance(context);
    }
}
