package thientt.app.android.checkrankgoogle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import thientt.app.android.checkrankgoogle.R;
import thientt.app.android.checkrankgoogle.model.Website;

/**
 * Created by thientran on 5/17/16.
 */
public class WebAdapter extends ArrayAdapter<Website> {
    private Context context;
    private ArrayList<Website> websites;
    private int resource;

    public WebAdapter(Context context, int resource, ArrayList<Website> websites) {
        super(context, resource, websites);
        this.context = context;
        this.websites = websites;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(resource, parent, false);
        TextView tev1 = (TextView) rowView.findViewById(R.id.tev1);

        tev1.setText(String.format("%d  -   %s", position+1, websites.get(position).getWebName()));


        return rowView;
    }
}
