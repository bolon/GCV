package com.rere.fish.gcv.imagesource;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flurgle.camerakit.CameraKit;
import com.flurgle.camerakit.CameraListener;
import com.flurgle.camerakit.CameraView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.CompositeMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.multi.SnackbarOnAnyDeniedMultiplePermissionsListener;
import com.rere.fish.gcv.OnFragmentInteractionListener;
import com.rere.fish.gcv.R;
import com.rere.fish.gcv.camera.AdditionalCameraTaskImpl;
import com.rere.fish.gcv.uicustoms.CircularPulsingButton;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CaptureImageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CaptureImageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    @BindView(R.id.parentLayoutFragmentCapture)
    CoordinatorLayout parentLayout;
    @BindView(R.id.camera)
    CameraView cameraView;
    @BindView(R.id.buttonCapture)
    CircularPulsingButton btnCapture;
    @BindView(R.id.rootRePermission)
    RelativeLayout rePermissionLayout;
    private OnFragmentInteractionListener mListener;

    private AdditionalCameraTaskImpl additionalCameraTask;
    private MultiplePermissionsListener multiplePermissionsListener;

    public CaptureImageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CaptureImageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CaptureImageFragment newInstance(String param1, String param2) {
        CaptureImageFragment fragment = new CaptureImageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @OnClick(R.id.btnRequestPermission)
    public void onClickButtonPermission() {
        Timber.i("checkpermission", checkPermission());
        if (!checkPermission()) {
            Dexter.withActivity(getActivity())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    ).withListener(new CompositeMultiplePermissionsListener(multiplePermissionsListener,
                    SnackbarOnAnyDeniedMultiplePermissionsListener.Builder.with(parentLayout, R.string.all_permissions_denied_feedback)
                            //.withOpenSettingsButton("Settings") //TODO : Handle this
                            .build()))
                    .withErrorListener(error -> Timber.e("Error request permission " + error.toString()))
                    .check();
        } else {
            rePermissionLayout.setVisibility(GONE);
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        additionalCameraTask = new AdditionalCameraTaskImpl(getActivity());

        multiplePermissionsListener = new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (!report.getDeniedPermissionResponses().isEmpty()) {
                    for (PermissionDeniedResponse p : report.getDeniedPermissionResponses()) {
                        Timber.i("denied " + p.getPermissionName());
                    }
                } else {
                    rePermissionLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                Snackbar.make(parentLayout, "Please manually allow permission(s) from settings", Snackbar.LENGTH_SHORT).show();
                token.cancelPermissionRequest();

            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_capture_image, container, false);
        ButterKnife.bind(this, v);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnCapture.setColor(getContext().getColor(R.color.alphaGray80));
            btnCapture.setDrawable(getResources().getDrawable(R.mipmap.icon_camera, getActivity().getTheme()));
        } else {
            btnCapture.setColor(getResources().getColor(R.color.alphaGray80));
            btnCapture.setDrawable(getResources().getDrawable(R.mipmap.icon_camera));
        }

        btnCapture.setAnimationDuration(300);

        if (!checkPermission()) {
            rePermissionLayout.setVisibility(View.VISIBLE);
        }

        return v;
    }

    private boolean checkPermission() {
        int resultCamera = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        int resultMedia1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int resultMedia2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (resultCamera == PackageManager.PERMISSION_GRANTED &
                resultMedia1 == PackageManager.PERMISSION_GRANTED & resultMedia2 == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @OnClick(R.id.buttonCapture)
    public void onClickCaptureButton() {
        cameraView.setZoom(CameraKit.Constants.ZOOM_PINCH);
        cameraView.setCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(byte[] picture) {
                super.onPictureTaken(picture);

                Bitmap result = BitmapFactory.decodeByteArray(picture, 0, picture.length);

                additionalCameraTask.onFinishCamera(result);
                //startActivity(PreviewActivity.createIntent(getActivity(), result));
            }
        });

        cameraView.captureImage();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission()) {
            cameraView.start();
        }
    }

    @Override
    public void onPause() {
        cameraView.stop();
        super.onPause();
    }
}
