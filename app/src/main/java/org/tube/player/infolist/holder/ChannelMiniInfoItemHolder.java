package org.tube.player.infolist.holder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import org.schabi.newpipe.extractor.InfoItem;
import org.schabi.newpipe.extractor.channel.ChannelInfoItem;
import org.tube.player.infolist.InfoItemBuilder;
import org.tube.player.util.Localization;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChannelMiniInfoItemHolder extends InfoItemHolder {
    public final CircleImageView itemThumbnailView;
    public final TextView itemTitleView;
    public final TextView itemAdditionalDetailView;

    ChannelMiniInfoItemHolder(InfoItemBuilder infoItemBuilder, int layoutId, ViewGroup parent) {
        super(infoItemBuilder, layoutId, parent);

        itemThumbnailView = itemView.findViewById(org.tube.player.R.id.itemThumbnailView);
        itemTitleView = itemView.findViewById(org.tube.player.R.id.itemTitleView);
        itemAdditionalDetailView = itemView.findViewById(org.tube.player.R.id.itemAdditionalDetails);
    }

    public ChannelMiniInfoItemHolder(InfoItemBuilder infoItemBuilder, ViewGroup parent) {
        this(infoItemBuilder, org.tube.player.R.layout.list_channel_mini_item, parent);
    }

    @Override
    public void updateFromItem(final InfoItem infoItem) {
        if (!(infoItem instanceof ChannelInfoItem)) return;
        final ChannelInfoItem item = (ChannelInfoItem) infoItem;

        itemTitleView.setText(item.getName());
        itemAdditionalDetailView.setText(getDetailLine(item));

        itemBuilder.getImageLoader()
                .displayImage(item.thumbnail_url, itemThumbnailView, ChannelInfoItemHolder.DISPLAY_THUMBNAIL_OPTIONS);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemBuilder.getOnChannelSelectedListener() != null) {
                    itemBuilder.getOnChannelSelectedListener().selected(item);
                }
            }
        });
    }

    protected String getDetailLine(final ChannelInfoItem item) {
        String details = "";
        if (item.subscriber_count >= 0) {
            details += Localization.shortSubscriberCount(itemBuilder.getContext(), item.subscriber_count);
        }
        return details;
    }

    /**
     * Display options for channel thumbnails
     */
    public static final DisplayImageOptions DISPLAY_THUMBNAIL_OPTIONS =
            new DisplayImageOptions.Builder()
                    .cloneFrom(BASE_DISPLAY_IMAGE_OPTIONS)
                    .showImageOnLoading(org.tube.player.R.drawable.buddy_channel_item)
                    .showImageForEmptyUri(org.tube.player.R.drawable.buddy_channel_item)
                    .showImageOnFail(org.tube.player.R.drawable.buddy_channel_item)
                    .build();
}
