package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.ToProgressItem;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Created by DONGHYUNLEE on 2017-08-12.
 */

public class IndividualRecyclerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<ToProgressItem> items;
    private int item_layout;
    private String groupId;
    private ArrayList<StorageReference> storageitem;

    public IndividualRecyclerAdapter(Context context, List<ToProgressItem> items, int item_layout, String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_indvidualprogress, null);
        return new ViewHolder(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.individual_profile)
        ImageView individualProfile;
        @BindView(R.id.individual_nickname)
        TextView individualNickname;

        @BindView(R.id.individual_date)
        TextView individualDate;
        @BindView(R.id.individual_title)
        TextView individualTitle;
        @BindView(R.id.individual_dump)
        TextView individualDump;
        @BindView(R.id.individual_startdate)
        TextView individualStartdate;
        @BindView(R.id.individual_starttime)
        TextView individualStarttime;
        @BindView(R.id.individual_enddate)
        TextView individualEnddate;
        @BindView(R.id.individual_endtime)
        TextView individualEndtime;
        @BindView(R.id.individual_location)
        TextView individualLocation;
        @BindView(R.id.individual_memo)
        TextView individualMemo;

        @BindView(R.id.individual_check)
        CheckBox individualCheck;
        @BindView(R.id.individual_hide)
        Switch individualSwitch;
        @BindView(R.id.individual_hide_container)
        LinearLayout hideContainer;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) { // view position
        ToProgressItem item = items.get(position);

        Glide.with(context).using(new FirebaseImageLoader()).load(storageitem.get(position)).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(((ViewHolder)holder).individualProfile);
        ((ViewHolder)holder).individualNickname.setText(item.getNickName());
        ((ViewHolder)holder).individualDate.setText(item.getDate());
        ((ViewHolder)holder).individualTitle.setText(item.getTitle());
        ((ViewHolder)holder).individualStartdate.setText(item.getStartDate());
        ((ViewHolder)holder).individualStarttime.setText(item.getStartTime());
        ((ViewHolder)holder).individualEnddate.setText(item.getEndDate());
        ((ViewHolder)holder).individualEndtime.setText(item.getEndTime());
        ((ViewHolder)holder).individualLocation.setText(item.getLocation());
        ((ViewHolder)holder).individualMemo.setText(item.getMemo());

        if( ((ViewHolder)holder).individualEnddate.length() <= 0){
            ((ViewHolder)holder).individualDump.setText("");
        }


        ((ViewHolder)holder).individualSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(context, "check", Toast.LENGTH_SHORT).show();
                    ((ViewHolder)holder).hideContainer.setBackground(context.getDrawable(R.color.transparent_black2));
                } else{
                    Toast.makeText(context, "NONcheck", Toast.LENGTH_SHORT).show();
                    ((ViewHolder)holder).hideContainer.setBackground(context.getDrawable(R.color.colorWhite));
                }
            }
        });

        ((ViewHolder)holder).individualCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Toast.makeText(context, "체크 클릭", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(context, "체크 해제", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public ToProgressItem getCurItem(int position) {
        ToProgressItem item = items.get(position);
        return item;
    }

    public void setStorageitem(ArrayList<StorageReference> storageitem){
        this.storageitem = storageitem;

    }
}