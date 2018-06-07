package org.tube.player.fragments.list.playlist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.ads.Ad;

import org.tube.player.App;
import org.schabi.newpipe.extractor.ListExtractor;
import org.schabi.newpipe.extractor.NewPipe;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.playlist.PlaylistInfo;
import org.schabi.newpipe.extractor.stream.StreamInfoItem;
import org.tube.player.fragments.list.BaseListInfoFragment;
import org.tube.player.info_list.InfoItemDialog;
import org.tube.player.playlist.PlayQueue;
import org.tube.player.playlist.PlaylistPlayQueue;
import org.tube.player.playlist.SinglePlayQueue;
import org.tube.player.report.UserAction;
import org.tube.player.util.Constants;
import org.tube.player.util.ExtractorHelper;
import org.tube.player.util.FBAdUtils;
import org.tube.player.util.NavigationHelper;
import org.tube.player.BaseFragment;
import org.tube.player.util.AnimationUtils;

import io.reactivex.Single;

import static org.tube.player.util.AnimationUtils.animateView;

public class PlaylistFragment extends BaseListInfoFragment<PlaylistInfo> {

    /*//////////////////////////////////////////////////////////////////////////
    // Views
    //////////////////////////////////////////////////////////////////////////*/

    private View headerRootLayout;
    private TextView headerTitleView;
    private View headerUploaderLayout;
    private TextView headerUploaderName;
    private ImageView headerUploaderAvatar;
    private TextView headerStreamCount;
    private View playlistCtrl;

    private View headerPlayAllButton;
    private View headerPopupButton;
    private View headerBackgroundButton;

    public static PlaylistFragment getInstance(int serviceId, String url, String name) {
        PlaylistFragment instance = new PlaylistFragment();
        instance.setInitialData(serviceId, url, name);
        return instance;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // LifeCycle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FBAdUtils.interstitialLoad(Constants.FB_CHANPING_HIGH_AD, new FBAdUtils.FBInterstitialAdListener(){
            @Override
            public void onInterstitialDismissed(Ad ad) {
                super.onInterstitialDismissed(ad);
                FBAdUtils.destoryInterstitial();
            }
        });
        return inflater.inflate(org.tube.player.R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (FBAdUtils.isInterstitialLoaded()) {
            FBAdUtils.showInterstitial();
        }
        FBAdUtils.destoryInterstitial();
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Init
    //////////////////////////////////////////////////////////////////////////*/

    protected View getListHeader() {
        headerRootLayout = activity.getLayoutInflater().inflate(org.tube.player.R.layout.playlist_header, itemsList, false);
        headerTitleView = headerRootLayout.findViewById(org.tube.player.R.id.playlist_title_view);
        headerUploaderLayout = headerRootLayout.findViewById(org.tube.player.R.id.uploader_layout);
        headerUploaderName = headerRootLayout.findViewById(org.tube.player.R.id.uploader_name);
        headerUploaderAvatar = headerRootLayout.findViewById(org.tube.player.R.id.uploader_avatar_view);
        headerStreamCount = headerRootLayout.findViewById(org.tube.player.R.id.playlist_stream_count);
        playlistCtrl = headerRootLayout.findViewById(org.tube.player.R.id.playlist_control);

        headerPlayAllButton = headerRootLayout.findViewById(org.tube.player.R.id.playlist_ctrl_play_all_button);
        headerPopupButton = headerRootLayout.findViewById(org.tube.player.R.id.playlist_ctrl_play_popup_button);
        headerBackgroundButton = headerRootLayout.findViewById(org.tube.player.R.id.playlist_ctrl_play_bg_button);

        if (!App.isBgPlay()) {
            headerRootLayout.findViewById(org.tube.player.R.id.anchorLeft).setVisibility(View.GONE);
            headerBackgroundButton.setVisibility(View.GONE);
        }

        return headerRootLayout;
    }

    @Override
    public boolean onSmallItem() {
        return true;
    }

    @Override
    protected void initViews(View rootView, Bundle savedInstanceState) {
        super.initViews(rootView, savedInstanceState);

        infoListAdapter.useMiniItemVariants(true);
    }

    @Override
    protected void showStreamDialog(final StreamInfoItem item) {
        final Context context = getContext();
        final Activity activity = getActivity();
        if (context == null || context.getResources() == null || getActivity() == null) return;

        final String[] commands = new String[]{
                context.getResources().getString(org.tube.player.R.string.enqueue_on_background),
                context.getResources().getString(org.tube.player.R.string.enqueue_on_popup),
                context.getResources().getString(org.tube.player.R.string.start_here_on_main),
                context.getResources().getString(org.tube.player.R.string.start_here_on_background),
                context.getResources().getString(org.tube.player.R.string.start_here_on_popup),
        };

        final DialogInterface.OnClickListener actions = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final int index = Math.max(infoListAdapter.getItemsList().indexOf(item), 0);
                switch (i) {
                    case 0:
                        NavigationHelper.enqueueOnBackgroundPlayer(context, new SinglePlayQueue(item));
                        break;
                    case 1:
                        NavigationHelper.enqueueOnPopupPlayer(activity, new SinglePlayQueue(item));
                        break;
                    case 2:
                        NavigationHelper.playOnMainPlayer(context, getPlayQueue(index));
                        break;
                    case 3:
                        NavigationHelper.playOnBackgroundPlayer(context, getPlayQueue(index));
                        break;
                    case 4:
                        NavigationHelper.playOnPopupPlayer(activity, getPlayQueue(index));
                        break;
                    default:
                        break;
                }
            }
        };

        new InfoItemDialog(getActivity(), item, commands, actions).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (DEBUG) Log.d(TAG, "onCreateOptionsMenu() called with: menu = [" + menu + "], inflater = [" + inflater + "]");
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(org.tube.player.R.menu.menu_playlist, menu);
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Load and handle
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected Single<ListExtractor.NextItemsResult> loadMoreItemsLogic() {
        return ExtractorHelper.getMorePlaylistItems(serviceId, url, currentNextItemsUrl);
    }

    @Override
    protected Single<PlaylistInfo> loadResult(boolean forceLoad) {
        return ExtractorHelper.getPlaylistInfo(serviceId, url, forceLoad);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case org.tube.player.R.id.menu_item_openInBrowser:
                openUrlInBrowser(url);
                break;
            case org.tube.player.R.id.menu_item_share: {
                shareUrl(name, url);
                break;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    /*//////////////////////////////////////////////////////////////////////////
    // Contract
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void showLoading() {
        super.showLoading();
        AnimationUtils.animateView(headerRootLayout, false, 200);
        AnimationUtils.animateView(itemsList, false, 100);

        BaseFragment.imageLoader.cancelDisplayTask(headerUploaderAvatar);
        AnimationUtils.animateView(headerUploaderLayout, false, 200);
    }

    @Override
    public void handleResult(@NonNull final PlaylistInfo result) {
        super.handleResult(result);

        AnimationUtils.animateView(headerRootLayout, true, 100);
        AnimationUtils.animateView(headerUploaderLayout, true, 300);
        headerUploaderLayout.setOnClickListener(null);
        if (!TextUtils.isEmpty(result.getUploaderName())) {
            headerUploaderName.setText(result.getUploaderName());
            if (!TextUtils.isEmpty(result.getUploaderUrl())) {
                headerUploaderLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NavigationHelper.openChannelFragment(getFragmentManager(), result.getServiceId(), result.getUploaderUrl(), result.getUploaderName());
                    }
                });
            }
        }

        playlistCtrl.setVisibility(View.VISIBLE);

        BaseFragment.imageLoader.displayImage(result.getUploaderAvatarUrl(), headerUploaderAvatar, BaseFragment.DISPLAY_AVATAR_OPTIONS);
        headerStreamCount.setText(getResources().getQuantityString(org.tube.player.R.plurals.videos, (int) result.stream_count, (int) result.stream_count));

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_PLAYLIST, NewPipe.getNameOfService(result.getServiceId()), result.getUrl(), 0);
        }

        headerPlayAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper.playOnMainPlayer(activity, getPlayQueue());
            }
        });
        headerPopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper.playOnPopupPlayer(activity, getPlayQueue());
            }
        });
        headerBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavigationHelper.playOnBackgroundPlayer(activity, getPlayQueue());
            }
        });
    }

    private PlayQueue getPlayQueue() {
        return getPlayQueue(0);
    }

    private PlayQueue getPlayQueue(final int index) {
        return new PlaylistPlayQueue(
                currentInfo.getServiceId(),
                currentInfo.getUrl(),
                currentInfo.getNextStreamsUrl(),
                infoListAdapter.getItemsList(),
                index
        );
    }

    @Override
    public void handleNextItems(ListExtractor.NextItemsResult result) {
        super.handleNextItems(result);

        if (!result.getErrors().isEmpty()) {
            showSnackBarError(result.getErrors(), UserAction.REQUESTED_PLAYLIST, NewPipe.getNameOfService(serviceId)
                    , "Get next page of: " + url, 0);
        }
    }

    /*//////////////////////////////////////////////////////////////////////////
    // OnError
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    protected boolean onError(Throwable exception) {
        if (super.onError(exception)) return true;

        int errorId = exception instanceof ExtractionException ? org.tube.player.R.string.parsing_error : org.tube.player.R.string.general_error;
        onUnrecoverableError(exception, UserAction.REQUESTED_PLAYLIST, NewPipe.getNameOfService(serviceId), url, errorId);
        return true;
    }

    /*//////////////////////////////////////////////////////////////////////////
    // Utils
    //////////////////////////////////////////////////////////////////////////*/

    @Override
    public void setTitle(String title) {
        super.setTitle(title);
        headerTitleView.setText(title);
    }
}