package edu.uwyo.geckorockets.barrickmobileobserver;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.uwyo.geckorockets.barrickmobileobserver.app.Content;

/**
 * A fragment representing a single Parameter detail screen.
 * This fragment is either contained in a {@link ParameterListActivity}
 * in two-pane mode (on tablets) or a {@link ParameterDetailActivity}
 * on handsets.
 */
public class ParameterDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    private Content.Parameter mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ParameterDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = Content.ItemMap.get(getArguments().getString(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
                appBarLayout.setBackgroundColor(mItem.color);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parameter_detail, container, false);

        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.parameter_detail)).setText(mItem.details);
        }

        return rootView;
    }
}
