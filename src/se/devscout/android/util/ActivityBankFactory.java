package se.devscout.android.util;

import android.content.Context;
import se.devscout.android.model.repo.sql.SQLiteActivityRepo;
import se.devscout.server.api.ActivityBank;

public class ActivityBankFactory {
    private ActivityBankFactory() {
    }

    public static ActivityBank getInstance(Context context) {
        return SQLiteActivityRepo.getInstance(context);
    }
}
