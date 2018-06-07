package us.shandian.giga.ui.fragment;

import us.shandian.giga.get.DownloadManager;
import us.shandian.giga.service.MissionManagerService;

public class AllMissionsFragment extends MissionsFragment {

    @Override
    protected DownloadManager setupDownloadManager(MissionManagerService.DMBinder binder) {
        return binder.getDownloadManager();
    }
}
