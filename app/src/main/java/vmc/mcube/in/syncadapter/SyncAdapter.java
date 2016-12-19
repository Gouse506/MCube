/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package vmc.mcube.in.syncadapter;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import vmc.mcube.in.downloads.ReportDownload;
import vmc.mcube.in.model.Params;
import vmc.mcube.in.utils.Tag;
import vmc.mcube.in.utils.Utils;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements Tag {


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);


    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        String authkey = Utils.getFromPrefs(getContext(), AUTHKEY, "N/A");
        String limit = Utils.getFromPrefs(getContext(), "recordLimit", "10");

        Log.d("TEST21", "Beginning network synchronization");


        new ReportDownload(new Params(TRACK, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

        new ReportDownload(new Params(IVRS, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

        new ReportDownload(new Params(X, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

        new ReportDownload(new Params(LEAD, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

        new ReportDownload(new Params(MTRACKER, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

        new ReportDownload(new Params(FOLLOWUP, 0, limit, authkey, "0", false, true)).executeOnExecutor(ReportDownload.SERIAL_EXECUTOR);

    }


}




