package org.tube.player.about;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class LicenseFragmentHelper extends AsyncTask<Object, Void, Integer> {

    private Context context;
    private License license;

    @Override
    protected Integer doInBackground(Object... objects) {
        context = (Context) objects[0];
        license = (License) objects[1];
        return 1;
    }

    @Override
    protected void onPostExecute(Integer result){
        String webViewData = getFormattedLicense(context, license);
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(license.getName());

        WebView wv = new WebView(context);
        wv.loadData(webViewData, "text/html; charset=UTF-8", null);

        alert.setView(wv);
        alert.setNegativeButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    /**
     * @param context the context to use
     * @param license the license
     * @return String which contains a HTML formatted license page styled according to the context's theme
     */
    public static String getFormattedLicense(Context context, License license) {
        if(context == null) {
            throw new NullPointerException("context is null");
        }
        if(license == null) {
            throw new NullPointerException("license is null");
        }

        String licenseContent = "";
        String webViewData = null;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(context.getAssets().open(license.getFilename()), "UTF-8"));
            String str;
            while ((str = in.readLine()) != null) {
                licenseContent += str;
            }
            in.close();

            // split the HTML file and insert the stylesheet into the HEAD of the file
            String[] insert = licenseContent.split("</head>");
        } catch (Exception e) {
            throw new NullPointerException("could not get license file:" );
        }
        return webViewData;
    }


    /**
     * Cast R.color to a hexadecimal color value
     * @param context the context to use
     * @param color the color number from R.color
     * @return a six characters long String with hexadecimal RGB values
     */
    public static String getHexRGBColor(Context context, int color) {
        return context.getResources().getString(color).substring(3);
    }

}
