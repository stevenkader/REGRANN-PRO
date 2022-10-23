package com.nimmble.rgpro.activity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nimmble.rgpro.R;
import com.nimmble.rgpro.model.InstaItem;
import com.nimmble.rgpro.util.Util;

import java.io.File;
import java.util.ArrayList;


/**
 * @author Kishan H Dhamat , Email: kishan.dhamat105@gmail.com
 */
public class KeptPhotosAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    ArrayList<InstaItem> list;
//    private AQuery aQuery;

    public KeptPhotosAdapter(Activity activity, ArrayList<InstaItem> list) {
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int arg0) {
        return list.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.kept_list_item,
                    parent, false);
            holder = new ViewHolder();
            holder.tvAuthour = convertView
                    .findViewById(R.id.keptListAuthor);
            holder.tvVideo = convertView
                    .findViewById(R.id.videoIcon);
            holder.ivIsScheduled = convertView
                    .findViewById(R.id.ivIsScheduled);
            holder.imageView = convertView
                    .findViewById(R.id.keptListPhoto);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        InstaItem instaItem = list.get(position);
        if (instaItem.getIsScheduled() == 0) {
            holder.ivIsScheduled.setVisibility(View.GONE);
        } else {
            holder.ivIsScheduled.setVisibility(View.VISIBLE);
        }

        holder.tvAuthour.setText(instaItem.getAuthor());
        holder.tvVideo.setText(instaItem.getVideoURL());
        if (TextUtils.isEmpty(instaItem.getVideoURL())) {
            holder.tvVideo.setVisibility(View.GONE);
        } else {
            holder.tvVideo.setVisibility(View.VISIBLE);
        }
        setViewImage(holder.imageView, instaItem.getPhoto());
        return convertView;
    }

    public void setViewImage(ImageView v, String value) {
        try {
            v.setImageResource(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            v.setImageBitmap(Util.decodeFile(new File(value)));
         //   v.setImageURI(Uri.parse(value));
        }
    }

    private static class ViewHolder {
        TextView tvAuthour, tvVideo;
        ImageView imageView, ivIsScheduled;
    }
}
