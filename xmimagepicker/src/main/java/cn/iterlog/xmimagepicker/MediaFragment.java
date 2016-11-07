package cn.iterlog.xmimagepicker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.Collections;
import java.util.List;

import cn.iterlog.xmimagepicker.Utils.MediaController;
import cn.iterlog.xmimagepicker.adapter.MediaRecyclerAdapter;

public class MediaFragment extends Fragment implements MediasLogic.MediaListener {
    private static final String ARG_MEDIA_TYPE = "ARG_MEDIA_TYPE";
    private int mediaType = -1;
    private RecyclerView mRecy;
    private MediaRecyclerAdapter mAdapter;
    private ProgressBar mProgressBar;

    public MediaFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MediaFragment newInstance(int sectionNumber) {
        MediaFragment fragment = new MediaFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MEDIA_TYPE, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_picker, container, false);
        mRecy = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.loadProgress);
        mediaType = getArguments().getInt(ARG_MEDIA_TYPE);
        int itemId = 0;
        if (itemId == 1) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mRecy.setLayoutManager(linearLayoutManager);
        } else if (itemId == 0) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
            gridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
            mRecy.setLayoutManager(gridLayoutManager);
        }

        loadMedias();
        mAdapter = new MediaRecyclerAdapter(this.getActivity().getApplicationContext(), loadMedias());
        mRecy.setAdapter(mAdapter);
        if (MediasLogic.getInstance().isLoading()) {
            mProgressBar.setVisibility(View.GONE);
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        MediasLogic.getInstance().registerListener(this, this);
        return rootView;
    }

    private List<MediaController.PhotoEntry> loadMedias() {
        if (mediaType == Constants.TYPE_PICTURE && MediasLogic.getInstance().getPictureAlbums().size() > 0) {
            return MediasLogic.getInstance().getPictureAlbums().get(0).photos;
        } else if (mediaType == Constants.TYPE_VIDEO && MediasLogic.getInstance().getVideoAlbums().size() > 0) {
            return MediasLogic.getInstance().getVideoAlbums().get(0).photos;
        }

        return Collections.EMPTY_LIST;
    }

    @Override
    public void onDestroyView() {
        MediasLogic.getInstance().unRegisterListener(this);
        super.onDestroyView();
    }

    @Override
    public void onMediaLoaded(int type) {
        if (type == mediaType) {
            mAdapter.setmMedias(loadMedias());
            mAdapter.notifyDataSetChanged();
        }
    }

}