package com.leiting.mission.ui.fragment;

import com.leiting.mission.get.DownloadManager;
import com.leiting.mission.service.MissionManagerService;

public class AllMissionsFragment extends MissionsFragment {

    @Override
    protected DownloadManager setupDownloadManager(MissionManagerService.DMBinder binder) {
        return binder.getDownloadManager();
    }
}
