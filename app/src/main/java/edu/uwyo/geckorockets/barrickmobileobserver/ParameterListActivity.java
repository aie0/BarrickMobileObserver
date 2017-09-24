package edu.uwyo.geckorockets.barrickmobileobserver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import edu.uwyo.geckorockets.barrickmobileobserver.app.Content;

import java.util.HashMap;
import java.util.List;

/**
 * An activity representing a list of Parameters. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ParameterDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ParameterListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    private enum statuses {OK, ALERT, WARNING, DOWN, UNKNOWN}
    private HashMap<statuses, Integer> colorMap = new HashMap<>();

    private TextView statusText;
    private ImageView statusPane;

    public RecyclerView recyclerView;

    // TEMP
    statuses status = statuses.OK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parameter_list);

        recyclerView = (RecyclerView) findViewById(R.id.parameter_list);
        assert recyclerView != null;

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Content.nextRow();
                setupRecyclerView(recyclerView);
            }
        });

        setupRecyclerView((RecyclerView) findViewById(R.id.parameter_list));

        if (findViewById(R.id.parameter_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }

        colorMap.put(statuses.OK, R.color.colorOk);
        colorMap.put(statuses.ALERT, R.color.colorAlert);
        colorMap.put(statuses.WARNING, R.color.colorWarn);
        colorMap.put(statuses.DOWN, R.color.colorDown);
        colorMap.put(statuses.UNKNOWN, R.color.colorUnknown);

        statusText = (TextView) findViewById(R.id.currentStatus);
        statusPane = (ImageView) findViewById(R.id.statusPane);
    }

    public void setStatus (statuses newStatus) {
        statusText.setText(newStatus.toString());
        statusPane.setImageResource(colorMap.get(newStatus));
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(Content.Items));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Content.Parameter> mValues;

        public SimpleItemRecyclerViewAdapter(List<Content.Parameter> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.parameter_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = mValues.get(position);
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);
            holder.mUnitView.setText(mValues.get(position).unit);

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTwoPane) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ParameterDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ParameterDetailFragment fragment = new ParameterDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.parameter_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ParameterDetailActivity.class);
                        intent.putExtra(ParameterDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public final TextView mUnitView;
            public Content.Parameter mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.parameterId);
                mContentView = (TextView) view.findViewById(R.id.parameterValue);
                mUnitView = (TextView) view.findViewById(R.id.parameterUnit);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }
}
