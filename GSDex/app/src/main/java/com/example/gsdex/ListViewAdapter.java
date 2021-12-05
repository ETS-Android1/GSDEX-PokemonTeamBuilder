package com.example.gsdex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ListViewAdapter extends ArrayAdapter<String> {

    String[] names;
    String[] types;
    int[] images;
    Context mContext;

    public ListViewAdapter(@NonNull Context context, String[] pkmnNames, String[] pkmnTypes, int[] pkmnImages) {
        super(context, R.layout.list_row);
        this.names = pkmnNames;
        this.types = pkmnTypes;
        this.images = pkmnImages;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return names.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Viewolder mViewHolder = new Viewolder();
        if(convertView == null) {

            LayoutInflater mInflator = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = mInflator.inflate(R.layout.list_row, parent, false);
            mViewHolder.mImage = (ImageView) convertView.findViewById(R.id.pkmnImage);
            mViewHolder.mName = (TextView) convertView.findViewById(R.id.name);
            mViewHolder.mTypes = (TextView) convertView.findViewById(R.id.types);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (Viewolder) convertView.getTag();
        }

        mViewHolder.mImage.setImageResource(images[position]);
        mViewHolder.mName.setText(names[position]);
        mViewHolder.mTypes.setText(types[position]);

        mViewHolder.mImage.setTag(names[position]);

        return convertView;
    }




    static class Viewolder{
        ImageView mImage;
        TextView mName;
        TextView mTypes;
    }

}
