package com.example.screenrecorderv2.ui.video;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.ads.control.AdmobHelp;
import com.example.screenrecorderv2.R;
import com.example.screenrecorderv2.base.BaseFragment;
import com.example.screenrecorderv2.base.rx.RxBusType;
import com.example.screenrecorderv2.databinding.FragmentVideoBinding;
import com.example.screenrecorderv2.ui.adapter.VideoAdapter;
import com.example.screenrecorderv2.ui.main.MainActivity;
import com.example.screenrecorderv2.ui.trimvideo.VideoTrimActivity;
import com.example.screenrecorderv2.ui.trimvideo.trimlib.VideoFile;
import com.example.screenrecorderv2.utils.Config;
import com.example.screenrecorderv2.utils.PreferencesHelper;
import com.example.screenrecorderv2.utils.Storage;
import com.example.screenrecorderv2.utils.Toolbox;
import com.example.screenrecorderv2.utils.encoder.Mp4toGIFConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoFragment extends BaseFragment<FragmentVideoBinding> {
    private static final int REQUEST_PERMISSION_DELETE = 376;
    private VideoAdapter videoAdapter;
    private int pos;
    private Handler handler;

    @Override
    protected void initView() {
        handler = new Handler();
        videoAdapter = new VideoAdapter(new ArrayList<>(), requireContext(), handler);
        videoAdapter.setCallBackVideo(new VideoAdapter.CallBackVideo() {
            @Override
            public void onClickItem(VideoFile item) {
                openMedia(item.getPath());
            }

            @Override
            public void onCLickMore(VideoFile item, int pos, View view) {
                showPopupMenuMore(item, pos, view);
            }
        });
        binding.rcvVideo.setAdapter(videoAdapter);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    getData();
                } else {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).askPermissionStorageMain();
                    }
                }
            } else {
                getData();
            }
            binding.swipeRefresh.setRefreshing(false);
        });
        binding.swipeRefresh.setColorSchemeResources(R.color.color_accent,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getData();
            }
        } else {
            getData();
        }
    }

    private void getData() {
        binding.progress.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            getAllFilesInTrim()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap(this::mapFiles)
                    .subscribe(datas -> {
                        if (datas.isEmpty()) {
                            binding.rcvVideo.setVisibility(View.GONE);
                            binding.groupNoData.setVisibility(View.VISIBLE);
                        } else {
                            binding.rcvVideo.setVisibility(View.VISIBLE);
                            binding.groupNoData.setVisibility(View.GONE);
                            videoAdapter.addDatas(datas);
                        }
                        binding.progress.setVisibility(View.GONE);
                    }, throwable -> {
                        binding.progress.setVisibility(View.GONE);
                    });
        }, 1000);
    }

    public Single<File[]> getAllFilesInTrim() {
        return Single.create(sub -> {
            File file = new File(Storage.getDirectoryFileVideoInStorage(requireContext()));
            File[] listFile = file.listFiles();
            if (listFile != null) {
                sub.onSuccess(listFile);
            } else {
                sub.onSuccess(new File[]{});
            }
        });
    }

    public Single<List<VideoFile>> mapFiles(@NonNull File[] listFile) {
        return Single.create(sub -> {
            Arrays.sort(listFile, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            List<VideoFile> videoFiles = new ArrayList<>();
            long previousDate = 0;
            for (int i = listFile.length - 1; i >= 0; i--) {
                VideoFile videoFile = new VideoFile();
                String duration = Toolbox.getDurationVideoFile(getContext(), listFile[i]);
                if (duration != null) {
                    if (!Toolbox.isSameDay(previousDate, listFile[i].lastModified())) {
                        VideoFile header = new VideoFile();
                        header.setHeader(true);
                        header.setLastModified(listFile[i].lastModified());
                        videoFiles.add(header);
                    }
                    videoFile.setPath(listFile[i].getAbsolutePath());
                    videoFile.setName(listFile[i].getName());
                    videoFile.setSize(listFile[i].length());
                    videoFile.setDuration(duration);

                    videoFile.setResolution(Toolbox.getResolutionVideo(getContext(), listFile[i]));
                    videoFile.setLastModified(listFile[i].lastModified());
                    videoFiles.add(videoFile);
                    previousDate = listFile[i].lastModified();
                }
            }
            sub.onSuccess(videoFiles);
        });
    }

    private void requestDeletePermission(List<Uri> uriList) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                PendingIntent pi = MediaStore.createDeleteRequest(getActivity().getContentResolver(), uriList);
                startIntentSenderForResult(pi.getIntentSender(), REQUEST_PERMISSION_DELETE, null, 0, 0,
                        0, null);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        }
    }

    private void showPopupMenuMore(VideoFile videoFile, int pos, View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popup_menu_more);
        popupMenu.show();
        popupMenu.getMenu().getItem(3).setEnabled(PreferencesHelper.getBoolean(PreferencesHelper.KEY_SAVE_AS_GIF, true));
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.share:
                    shareMedia(videoFile.getPath());
                    break;
                case R.id.delete:
                    this.pos = pos;
                    deleteMedia(videoFile, pos);
                    break;
                case R.id.edit:
                    // Mình get activity đúng ko
                    AdmobHelp.getInstance().showInterstitialAd(getActivity(), () -> {
                        openEditVideo(videoFile.getPath());
                    });

                    break;
                case R.id.savegif:
                    convertToGif(videoFile.getPath());
            }
            return true;
        });
    }

    private void convertToGif(String filePath) {
        Uri fileUri = FileProvider.getUriForFile(
                getContext(), getContext().getPackageName() + ".provider",
                new File(filePath));
        Mp4toGIFConverter gif = new Mp4toGIFConverter(getContext());
        gif.setVideoUri(fileUri);
        gif.convertToGif();
    }

    private void openEditVideo(String filePath) {
        Intent intent = new Intent(getContext(), VideoTrimActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Config.EXTRA_PATH, filePath);
        getContext().startActivity(intent);
    }

    private void shareMedia(String filePath) {
        Uri fileUri = FileProvider.getUriForFile(
                getContext(), getContext().getPackageName() + ".provider",
                new File(filePath));
        Intent shareIntent = new Intent()
                .setAction(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, fileUri)
                .setType(filePath.endsWith(".mp4") ? "video/mp4" : "image/*");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(shareIntent);
    }

    private void openMedia(String filePath) {
        Uri fileUri = FileProvider.getUriForFile(
                getContext(), getContext().getPackageName() + ".provider",
                new File(filePath));
        try {
            Intent openVideoIntent = new Intent();
            openVideoIntent.setAction(Intent.ACTION_VIEW)
                    .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK)
                    .setDataAndType(
                            fileUri,
                            getContext().getContentResolver().getType(fileUri));
            getContext().startActivity(openVideoIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMedia(VideoFile videoFile, int pos) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            new File(videoFile.getPath()).delete();
            removeItem(pos);
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{videoFile.getPath()}, new String[]{"video/mp4"},
                    (path1, uri) -> {

                    });
        } else {
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{videoFile.getPath()}, new String[]{"video/mp4"},
                    (path1, uri) -> {
                        if (uri != null) {
                            try {
                                if (getActivity().getContentResolver().delete(uri, null, null) != -1) {
                                    removeItem(pos);
                                }
                            } catch (SecurityException e) {
                                List<Uri> uris = new ArrayList<>();
                                uris.add(uri);
                                requestDeletePermission(uris);
                            }
                        }
                    });
        }

    }

    private void removeItem(int pos) {
        getBaseActivity().runOnUiThread(() -> {
            videoAdapter.getList().remove(pos);
            videoAdapter.notifyItemRemoved(pos);
            if (videoAdapter.getList().get(videoAdapter.getList().size() - 1).isHeader()) {
                videoAdapter.getList().remove(videoAdapter.getList().size() - 1);
                videoAdapter.notifyItemRemoved(videoAdapter.getList().size() - 1);
            }
            if (videoAdapter.getList().isEmpty()) {
                binding.rcvVideo.setVisibility(View.GONE);
                binding.groupNoData.setVisibility(View.VISIBLE);
                binding.progress.setVisibility(View.GONE);
            } else {
                binding.rcvVideo.setVisibility(View.VISIBLE);
                binding.groupNoData.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected boolean isNeedRefresh() {
        return true;
    }

    @Override
    protected FragmentVideoBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentVideoBinding.inflate(LayoutInflater.from(getContext()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_DELETE) {
            if (resultCode == Activity.RESULT_OK) {
                removeItem(pos);
            }
        }
    }

    @Override
    public void onReceivedEvent(RxBusType type, Object data) {
        switch (type) {
            case SCREEN_RECORD_SUCCESS:
            case TRIM_VIDEO:
            case NOTI_MEDIA_CHANGE:
                getData();
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
