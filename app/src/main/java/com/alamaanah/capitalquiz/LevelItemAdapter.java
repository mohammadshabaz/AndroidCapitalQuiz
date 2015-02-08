package com.alamaanah.capitalquiz;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LevelItemAdapter extends ArrayAdapter<LevelItem>{

    Context context; 
    int layoutResourceId;    
    LevelItem data[] = null;
    
    public LevelItemAdapter(Context context, int layoutResourceId, LevelItem[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LevelItemHolder holder = null;
        
        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            
            holder = new LevelItemHolder();
            holder.imgLevel = (ImageView)row.findViewById(R.id.imgLevel);
            holder.txtLevel = (TextView)row.findViewById(R.id.txtLevel1);
            holder.txtScore = (TextView) row.findViewById(R.id.txtScore1);
            
            row.setTag(holder);
        }
        else
        {
            holder = (LevelItemHolder)row.getTag();
        }
        
        LevelItem LevelItem = data[position];
        holder.txtLevel.setText("Level " +  LevelItem.level);
        
        if(LevelItem.isLocked)
        	holder.imgLevel.setImageResource(R.drawable.lock);
        else if(LevelItem.score > Integer.parseInt(context.getResources().getString(R.string.time_limit)) || LevelItem.score == 0)
        	holder.imgLevel.setImageResource(R.drawable.unlock);
    	else
    		holder.imgLevel.setImageResource(R.drawable.cleared);
        
        if(LevelItem.score != 0)
        	holder.txtScore.setText(Html.fromHtml("Best score: <b>" + Math.round(LevelItem.score) + "</b>"));
        else
        	holder.txtScore.setText("");
        
        Typeface fontObelix = Typeface.createFromAsset(context.getAssets(), "fonts/ObelixPro.ttf");
        holder.txtLevel.setTypeface(fontObelix);
        
        Typeface fontExo = Typeface.createFromAsset(context.getAssets(), "fonts/Exo.ttf");
        holder.txtScore.setTypeface(fontExo);
        
        return row;
    }
    
    static class LevelItemHolder
    {
        ImageView imgLevel;
        TextView txtLevel, txtScore;
    }
}