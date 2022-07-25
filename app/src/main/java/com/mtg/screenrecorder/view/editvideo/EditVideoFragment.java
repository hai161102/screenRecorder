package com.mtg.screenrecorder.view.editvideo;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.ads.control.AdmobHelp;
import com.mtg.screenrecorder.R;
import com.mtg.screenrecorder.base.BaseFragment;
import com.mtg.screenrecorder.databinding.FragmentEditVideoBinding;
import com.mtg.screenrecorder.view.adapter.EditAdapter;
import com.mtg.screenrecorder.view.trimvideo.VideoTrimActivity;
import com.mtg.screenrecorder.utils.Config;

import java.util.Arrays;

import static android.app.Activity.RESULT_OK;

public class EditVideoFragment extends BaseFragment<FragmentEditVideoBinding> {
    private EditAdapter editAdapter;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 100;
    private static final int VIDEO_TRIM = 101;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();

                // MEDIA GALLERY
              String path;
                path = getPathFromUri(selectedImageUri);
                if (TextUtils.isEmpty(path)) {
                    path = selectedImageUri.getPath();
                }
                if (path != null) {
                    String finalPath = path;
//                    AdmobHelp.getInstance().showInterstitialAd(getActivity(), () -> {
//
//                    });
                    startActivityForResult(new Intent(requireActivity(),
                                        VideoTrimActivity.class).putExtra(Config.EXTRA_PATH, finalPath),
                                VIDEO_TRIM);
                } else {
                    toast(getString(R.string.not_open_video));
                }
            }
        }
    }

    public String getPathFromUri(final Uri uri) {


        // DocumentProvider
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(getContext(), contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(getContext(), contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(getContext(), uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    @Override
    protected void initView() {
        editAdapter = new EditAdapter(Arrays.asList(ItemEdit.values()), getContext());
        editAdapter.setCallBackAdapter(item -> {
            switch (item) {
                case TRIM:
                    try {
                        getBaseActivity().askPermissionStorage(() -> {
                            Intent intent = new Intent();
                            intent.setType("video/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);
                            return null;
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
//                case COMPRESS:
//                    break;
//                case CROP:
//                    break;
//                case VIDEO_TO_MP3:
//                    break;
//                case THEME:
//                    break;
//                case GIFGURU:
//                    break;
            }
        });
        binding.rcvEdit.setAdapter(editAdapter);
    }

    @Override
    protected void initControl() {

    }

    @Override
    protected boolean isNeedRefresh() {
        return false;
    }

    @Override
    protected FragmentEditVideoBinding getViewBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentEditVideoBinding.inflate(LayoutInflater.from(getContext()));
    }
}
