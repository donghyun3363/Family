package com.family.donghyunlee.family.photoalbum;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.family.donghyunlee.family.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.family.donghyunlee.family.R.id.img_memory_album;

/**
 * Created by DONGHYUNLEE on 2017-08-01.
 */

public class MemoryAlbumRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = MemoryAlbumRecyclerAdapter.class.getSimpleName();
    private Context context;
    private List<MemoryAlbumItem> items;
    private int item_layout;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    // 어뎁터 생성자
    public MemoryAlbumRecyclerAdapter(Context context, List<MemoryAlbumItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }

    // 뷰홀더 생성(인플레터 후)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.temp, null);
            Log.d(TAG, "Header");
            return new HeaderViewHolder(v);
        } else if (viewType == TYPE_ITEM) {   // TYPE_BODY
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_memoryphoto, null);
            Log.d(TAG, "Body");
            return new ViewHolder(v);
        }
        return null;
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder {
        // 뷰홀더에서 각 뷰들을 참조 함수
        public HeaderViewHolder(View itemView) {
            super(itemView);

        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // 뷰홀더에서 각 뷰들을 참조 함수

        @BindView(img_memory_album)
        ImageView imgMemoryAlbum;
        @BindView(R.id.title_memory_album)
        TextView titleMemoryAlbum;
        @BindView(R.id.num_memory_album)
        TextView numMemoryAlbum;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) { // view position

        if (holder instanceof HeaderViewHolder) {

        } else if (holder instanceof ViewHolder) {
            position--;

            final MemoryAlbumItem item = items.get(position);
            Log.d(TAG, ">>>>>>>>> " + items.get(position).getAlbumNum());
            if(item.getFilePath()!=null) {
                Glide.with(context).load(R.drawable.img_temp2).into(((ViewHolder) holder).imgMemoryAlbum);//  holder.storeImage.
            }
            ((ViewHolder) holder).titleMemoryAlbum.setText(item.getTitle());
            ((ViewHolder) holder).numMemoryAlbum.setText(item.getAlbumNum());
        }
    }

    // header 여부 체크 메소드
    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemViewType(int position) {

        if (isPositionHeader(position))
            return TYPE_HEADER;
        else
            return TYPE_ITEM;
    }


    // 아이템 수 카운트
    @Override
    public int getItemCount() {
        return this.items.size() + 1;
    }



}