package com.mtg.screenrecorder.view.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.BaseFragment;
import com.mtg.screenrecorder.base.rx.RxBusType;
import com.mtg.screenrecorder.databinding.FragmentPictureBinding;
import com.mtg.screenrecorder.view.adapter.PictureAdapter;
import com.mtg.screenrecorder.view.activity.MainActivity;
import com.mtg.screenrecorder.utils.Storage;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class PictureFragment extends BaseFragment<FragmentPictureBinding> {
    private PictureAdapter pictureAdapter;

    @Override
    protected void initView() {
        pictureAdapter = new PictureAdapter(new ArrayList<>(), getContext());
        pictureAdapter.setCallBackAdapter(item -> openMedia(item));
        binding.rcvPicture.setAdapter(pictureAdapter);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    getData();
                }else {
                    if (getActivity() instanceof MainActivity){
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
        getAllFilesInPicture()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(this::mapFiles)
                .subscribe(datas -> {
                    if (datas.isEmpty()) {
                        binding.rcvPicture.setVisibility(View.GONE);
                        binding.groupNoData.setVisibility(View.VISIBLE);
                    } else {
                        binding.rcvPicture.setVisibility(View.VISIBLE);
                        binding.groupNoData.setVisibility(View.GONE);
                        pictureAdapter.addDatas(datas);
                    }
                },throwable -> {
                });
    }

    public Single<File[]> getAllFilesInPicture() {
        return Single.create(sub -> {
            File[] listFile = Storage.getFilesImageInStorage(requireContext());
            if (listFile != null) {
                sub.onSuccess(listFile);
            } else {
                sub.onSuccess(new File[]{});
            }
        });
    }

    public Single<List<String>> mapFiles(@NonNull File[] listFile) {
        return Single.create(sub -> {
            Arrays.sort(listFile, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            List<String> pictureFile = new ArrayList<>();
            for (int i = listFile.length - 1; i >= 0; i--) {
                pictureFile.add(listFile[i].getAbsolutePath());
            }
            sub.onSuccess(pictureFile);
        });
    }

    @Override
    protected void initControl() {

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

    @Override
    protected boolean isNeedRefresh() {
        return true;
    }

    @Override
    protected FragmentPictureBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentPictureBinding.inflate(LayoutInflater.from(getContext()));
    }

    @Override
    public void onReceivedEvent(RxBusType type, Object data) {
        switch (type) {
            case SCREEN_SHOT:
            case NOTI_MEDIA_CHANGE:
                getData();
                break;
        }
    }
}
