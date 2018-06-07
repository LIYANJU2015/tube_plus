package org.tube.player.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;

import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.tube.player.report.ErrorActivity;
import org.tube.player.report.UserAction;
import org.tube.player.util.Constants;
import org.tube.player.util.KioskTranslator;

public class ContentSettingsFragment extends BasePreferenceFragment {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(org.tube.player.R.xml.content_settings);

        final ListPreference mainPageContentPref =  (ListPreference) findPreference(getString(org.tube.player.R.string.main_page_content_key));
        mainPageContentPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValueO) {
                        final String newValue = newValueO.toString();

                        final String mainPrefOldValue =
                                defaultPreferences.getString(getString(org.tube.player.R.string.main_page_content_key), "blank_page");
                        final String mainPrefOldSummary = getMainPagePrefSummery(mainPrefOldValue, mainPageContentPref);

                        if(newValue.equals(getString(org.tube.player.R.string.kiosk_page_key))) {
                            SelectKioskFragment selectKioskFragment = new SelectKioskFragment();
                            selectKioskFragment.setOnSelectedLisener(new SelectKioskFragment.OnSelectedLisener() {
                                @Override
                                public void onKioskSelected(String kioskId, int service_id) {
                                    defaultPreferences.edit()
                                            .putInt(getString(org.tube.player.R.string.main_page_selected_service), service_id).apply();
                                    defaultPreferences.edit()
                                            .putString(getString(org.tube.player.R.string.main_page_selectd_kiosk_id), kioskId).apply();
                                    String serviceName = "";
                                    try {
                                        serviceName = NewPipe.getService(service_id).getServiceInfo().name;
                                    } catch (ExtractionException e) {
                                        onError(e);
                                    }
                                    String kioskName = KioskTranslator.getTranslatedKioskName(kioskId,
                                            getContext());

                                    String summary =
                                            String.format(getString(org.tube.player.R.string.service_kiosk_string),
                                                    serviceName,
                                                    kioskName);

                                    mainPageContentPref.setSummary(summary);
                                }
                            });
                            selectKioskFragment.setOnCancelListener(new SelectKioskFragment.OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    mainPageContentPref.setSummary(mainPrefOldSummary);
                                    mainPageContentPref.setValue(mainPrefOldValue);
                                }
                            });
                            selectKioskFragment.show(getFragmentManager(), "select_kiosk");
                        } else if(newValue.equals(getString(org.tube.player.R.string.channel_page_key))) {
                            SelectChannelFragment selectChannelFragment = new SelectChannelFragment();
                            selectChannelFragment.setOnSelectedLisener(new SelectChannelFragment.OnSelectedLisener() {
                                @Override
                                public void onChannelSelected(String url, String name, int service) {
                                    defaultPreferences.edit()
                                            .putInt(getString(org.tube.player.R.string.main_page_selected_service), service).apply();
                                    defaultPreferences.edit()
                                            .putString(getString(org.tube.player.R.string.main_page_selected_channel_url), url).apply();
                                    defaultPreferences.edit()
                                            .putString(getString(org.tube.player.R.string.main_page_selected_channel_name), name).apply();

                                    mainPageContentPref.setSummary(name);
                                }
                            });
                            selectChannelFragment.setOnCancelListener(new SelectChannelFragment.OnCancelListener() {
                                @Override
                                public void onCancel() {
                                    mainPageContentPref.setSummary(mainPrefOldSummary);
                                    mainPageContentPref.setValue(mainPrefOldValue);
                                }
                            });
                            selectChannelFragment.show(getFragmentManager(), "select_channel");
                        } else {
                            mainPageContentPref.setSummary(getMainPageSummeryByKey(newValue));
                        }

                        defaultPreferences.edit().putBoolean(Constants.KEY_MAIN_PAGE_CHANGE, true).apply();

                        return true;
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        final String mainPageContentKey = getString(org.tube.player.R.string.main_page_content_key);
        final Preference mainPagePref = findPreference(getString(org.tube.player.R.string.main_page_content_key));
        final String bpk = getString(org.tube.player.R.string.blank_page_key);
        if(defaultPreferences.getString(mainPageContentKey, bpk)
                .equals(getString(org.tube.player.R.string.channel_page_key))) {
            mainPagePref.setSummary(defaultPreferences.getString(getString(org.tube.player.R.string.main_page_selected_channel_name), "error"));
        } else if(defaultPreferences.getString(mainPageContentKey, bpk)
                .equals(getString(org.tube.player.R.string.kiosk_page_key))) {
            try {
                StreamingService service = NewPipe.getService(
                        defaultPreferences.getInt(
                                getString(org.tube.player.R.string.main_page_selected_service), 0));

                String kioskName = KioskTranslator.getTranslatedKioskName(
                                defaultPreferences.getString(
                                        getString(org.tube.player.R.string.main_page_selectd_kiosk_id), "Trending"),
                        getContext());

                String summary =
                        String.format(getString(org.tube.player.R.string.service_kiosk_string),
                                service.getServiceInfo().name,
                                kioskName);

                mainPagePref.setSummary(summary);
            } catch (Exception e) {
                onError(e);
            }
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/
    private String getMainPagePrefSummery(final String mainPrefOldValue, final ListPreference mainPageContentPref) {
        if(mainPrefOldValue.equals(getString(org.tube.player.R.string.channel_page_key))) {
            return defaultPreferences.getString(getString(org.tube.player.R.string.main_page_selected_channel_name), "error");
        } else {
            return mainPageContentPref.getSummary().toString();
        }
    }

    private int getMainPageSummeryByKey(final String key) {
        if(key.equals(getString(org.tube.player.R.string.blank_page_key))) {
            return org.tube.player.R.string.blank_page_summary;
        } else if(key.equals(getString(org.tube.player.R.string.kiosk_page_key))) {
            return org.tube.player.R.string.kiosk_page_summary;
        } else if(key.equals(getString(org.tube.player.R.string.feed_page_key))) {
            return org.tube.player.R.string.feed_page_summary;
        } else if(key.equals(getString(org.tube.player.R.string.subscription_page_key))) {
            return org.tube.player.R.string.subscription_page_summary;
        } else if(key.equals(getString(org.tube.player.R.string.channel_page_key))) {
            return org.tube.player.R.string.channel_page_summary;
        }
        return org.tube.player.R.string.blank_page_summary;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Error
    //////////////////////////////////////////////////////////////////////////*/

    protected boolean onError(Throwable e) {
        final Activity activity = getActivity();
        ErrorActivity.reportError(activity, e,
                activity.getClass(),
                null,
                ErrorActivity.ErrorInfo.make(UserAction.UI_ERROR,
                        "none", "", org.tube.player.R.string.app_ui_crash));
        return true;
    }
}
