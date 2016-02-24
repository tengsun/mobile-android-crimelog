package st.crimelog;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeCameraFragment extends Fragment {

    private static final String TAG = "CrimeCameraFragment";

    public static final String EXTRA_PHOTO_FILENAME = "st.crimelog.photoName";

    private Camera camera;
    private SurfaceView surfaceView;
    private View progressView;

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            progressView.setVisibility(View.VISIBLE);
        }
    };

    private Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String fileName = UUID.randomUUID().toString() + ".jpg";
            FileOutputStream output = null;
            boolean success = true;

            try {
                output = getActivity().openFileOutput(fileName, Context.MODE_PRIVATE);
                output.write(data);
            } catch (Exception e) {
                Log.e(TAG, "Error saving photo to file: " + fileName, e);
                success = false;
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error closing file: " + fileName, e);
                    success = false;
                }

                if (success) {
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_PHOTO_FILENAME, fileName);
                    getActivity().setResult(Activity.RESULT_OK, intent);
                    Log.i(TAG, "Save photo to " + fileName);
                } else {
                    getActivity().setResult(Activity.RESULT_CANCELED);
                }
                getActivity().finish();
            }
        }
    };

    public CrimeCameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_camera, container, false);

        surfaceView = (SurfaceView) view.findViewById(R.id.crime_camera_surface_view);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (camera != null) {
                        camera.setPreviewDisplay(holder);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error setup preview display", e);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                if (camera == null) {
                    return;
                }

                // the surface changed size, update the camera preview size
                Camera.Parameters params = camera.getParameters();
                // set preview size
                Camera.Size size = getBestSupportedSize(params.getSupportedPreviewSizes(), width, height);
                params.setPreviewSize(size.width, size.height);
                // set picture size
                size = getBestSupportedSize(params.getSupportedPictureSizes(), width, height);
                params.setPictureSize(size.width, size.height);
                camera.setParameters(params);

                try {
                    camera.startPreview();
                } catch (Exception e) {
                    Log.e(TAG, "Could not start preview", e);
                    camera.release();
                    camera = null;
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                if (camera != null) {
                    camera.stopPreview();
                }
            }
        });

        Button takePhoto = (Button) view.findViewById(R.id.crime_camera_button);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camera != null) {
                    camera.takePicture(shutterCallback, null, jpegCallback);
                }
            }
        });

        progressView = view.findViewById(R.id.crime_camera_progress_view);
        progressView.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        camera = Camera.open();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private Camera.Size getBestSupportedSize(List<Camera.Size> sizes, int width, int height) {
        Camera.Size bestSize = sizes.get(0);
        int largestArea = bestSize.width * bestSize.height;
        for (Camera.Size s : sizes) {
            int area = s.width * s.height;
            if (area > largestArea) {
                bestSize = s;
                largestArea = area;
            }
        }
        return bestSize;
    }

}
