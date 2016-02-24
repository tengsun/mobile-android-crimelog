package st.crimelog;


import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import st.crimelog.util.PictureUtil;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhotoFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_PATH = "st.crimelog.imagePath";
    private ImageView imageView;

    public PhotoFragment() {
        // Required empty public constructor
    }

    public static PhotoFragment newInstance(String imagePath) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_PATH, imagePath);

        PhotoFragment fragment = new PhotoFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageView = new ImageView(getActivity());
        String path = (String) getArguments().getSerializable(EXTRA_IMAGE_PATH);
        BitmapDrawable image = PictureUtil.getScaledDrawable(getActivity(), path);
        imageView.setImageDrawable(image);

        return imageView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtil.cleanImageView(imageView);
    }
}
