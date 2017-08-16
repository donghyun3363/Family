package com.family.donghyunlee.family.bucket;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
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

public class ShareRecyclerAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = WishListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<ToProgressItem> items;
    private int item_layout;
    private String groupId;
    private ArrayList<StorageReference> storageitem;

    public ShareRecyclerAdapter(Context context, List<ToProgressItem> items, int item_layout, String groupId) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.groupId = groupId;
        Log.i(TAG, "111111111111111111111111111111111111111111111 생성자");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_shareprogress, null);

        Log.i(TAG, "111111111111111111111111111111111111111111111 뷰홀더 생성");
        return new ViewHolder(v);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.share_profile)
        ImageView shareProfile;
        @BindView(R.id.share_nickname)
        TextView shareNickname;
        @BindView(R.id.share_delete)
        ImageButton shareDelete;
        @BindView(R.id.share_date)
        TextView shareDate;
        @BindView(R.id.share_title)
        TextView shareTitle;
        @BindView(R.id.share_dump)
        TextView shareDump;
        @BindView(R.id.share_startdate)
        TextView shareStartdate;
        @BindView(R.id.share_starttime)
        TextView shareStarttime;
        @BindView(R.id.share_enddate)
        TextView shareEnddate;
        @BindView(R.id.share_endtime)
        TextView shareEndtime;
        @BindView(R.id.share_location)
        TextView shareLocation;
        @BindView(R.id.share_memo)
        TextView shareMemo;

        @BindView(R.id.share_check)
        CheckBox shareCheck;
        @BindView(R.id.share_hide)
        Switch shareSwitch;
        @BindView(R.id.share_hidecontainer)
        LinearLayout hideContainer;


        public ViewHolder(View itemView) {
            super(itemView);

            Log.i(TAG, "111111111111111111111111111111111111111111111 홀딩");
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) { // view position
        ToProgressItem item = items.get(position);

        Log.i(TAG, "111111111111111111111111111111111111111111111 바인딩");

        Log.i(TAG, "111111111111111111111111111111111111111111111 바인딩 => "+ item.getTitle());
        Glide.with(context).using(new FirebaseImageLoader()).load(storageitem.get(position)).centerCrop()
                .crossFade().bitmapTransform(new CropCircleTransformation(context)).into(((ViewHolder)holder).shareProfile);
        ((ViewHolder)holder).shareNickname.setText(item.getNickName());
        ((ViewHolder)holder).shareDate.setText(item.getDate());
        ((ViewHolder)holder).shareTitle.setText(item.getTitle());
        ((ViewHolder)holder).shareStartdate.setText(item.getStartDate());
        ((ViewHolder)holder).shareStarttime.setText(item.getStartTime());
        ((ViewHolder)holder).shareEnddate.setText(item.getEndDate());
        ((ViewHolder)holder).shareEndtime.setText(item.getEndTime());
        ((ViewHolder)holder).shareLocation.setText(item.getLocation());
        ((ViewHolder)holder).shareMemo.setText(item.getMemo());

        if( ((ViewHolder)holder).shareEnddate.length() <= 0){
            ((ViewHolder)holder).shareDump.setText("");
        }




        ((ViewHolder)holder).shareSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        ((ViewHolder)holder).shareDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "체크 클릭", Toast.LENGTH_SHORT).show();
            }
        });
        ((ViewHolder)holder).shareCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
//        if (this.items.size() == 0) {
//            return 1;
//        } else {
//            return this.items.size();
//        }

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