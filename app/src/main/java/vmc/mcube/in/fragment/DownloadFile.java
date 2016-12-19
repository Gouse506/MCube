package vmc.mcube.in.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import vmc.mcube.in.activity.HomeActivity;
import vmc.mcube.in.utils.ConnectivityReceiver;
import vmc.mcube.in.utils.Tag;

/**
 * Created by gousebabjan on 23/8/16.
 */
public class DownloadFile extends Fragment implements Tag {

    private DownloadFileTask DCallbacks;
    private Download mTask;
    private String urlfile;
    private HomeActivity downloadTask;

    public DownloadFile() {
    }

    public interface DownloadFileTask {

        void ondownloadFilePreExecute();

        void ondownloadFileProgressUpdate(int percent);

        void ondownloadFileCancelled();

        void ondownloadFilePostExecute(File file);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            DCallbacks = (DownloadFileTask) context;
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        urlfile = getArguments().getString("FILE");
       // Log.d("PATH", urlfile);
        mTask= new Download();
        mTask.execute();

    }


    @Override
    public void onDetach() {
        super.onDetach();
        DCallbacks = null;
    }
    public void onCancelTask () {

        //DCallbacks = null;
        if(!mTask.isCancelled()) {
            mTask.cancel(true);
        }
        DCallbacks.ondownloadFileCancelled();
    }


    private class Download extends AsyncTask<Void, String, File> {
        @Override
        protected void onPreExecute() {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFilePreExecute();
            }
        }


        @Override
        protected File doInBackground(Void... ignore) {
            File folder=null;
            int count;
            try {

                folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "data"+File.separator + "mcubeShare");
                if (!folder.exists()) {
                  folder.mkdirs();}
                    Log.d("PATH", folder+" ");

                    // Do something on success
                    URL url = new URL(STREAM_MCUBE + urlfile);
                    URLConnection conexion = url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = new FileOutputStream(folder.getAbsolutePath() + "/" + urlfile);

                    Log.d("PATH", folder.getAbsolutePath() + "/" + urlfile);
                    byte data[] = new byte[1024];

                    long total = 0;

                    while ((count = input.read(data)) != -1) {
                        total += count;
                        // Thread.sleep(500);
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                    }

                    output.flush();
                    output.close();
                    input.close();



            } catch (Exception e) {
                Log.d("FAIL DOWNLOAD", e.getMessage().toString());
            }
            return new File(folder.getAbsolutePath() + "/" + urlfile);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFileProgressUpdate(Integer.parseInt(progress[0]));
            }
        }

        @Override
        protected void onCancelled() {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFileCancelled();
            }
        }

        @Override
        protected void onPostExecute(File file) {
            if (DCallbacks != null) {
                DCallbacks.ondownloadFilePostExecute(file);
            }

        }

    }
}
