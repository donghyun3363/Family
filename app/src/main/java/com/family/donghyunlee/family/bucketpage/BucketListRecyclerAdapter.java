package com.family.donghyunlee.family.bucketpage;

import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.family.donghyunlee.family.R;
import com.family.donghyunlee.family.data.BucketListRecyclerItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by DONGHYUNLEE on 2017-08-10.
 */
public class BucketListRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>   {

    private static final String TAG = BucketListRecyclerAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<BucketListRecyclerItem> items;
    private String[] answerItem;
    private String[] questionItem;
    private int items_size;
    private int item_layout;

    public BucketListRecyclerAdapter(Context context, ArrayList<BucketListRecyclerItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
        this.items_size = 0;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_bucketlist, null);

        return new ViewHolder(v);
    }

    public void setItems_size(int items_size) {
        this.items_size = items_size;
        answerItem = new String[items_size];
        questionItem = new String[items_size];
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.bucketcard_question)
        TextView cardQuestion;
        @BindView(R.id.bucketcard_answer_hint)
        TextInputLayout cardAnswerHint;
        @BindView(R.id.bucketcard_answer)
        EditText cardAnswer;
        public ViewHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        BucketListRecyclerItem item = items.get(position);


        ((ViewHolder) holder).cardQuestion.setText(item.getQuestion());
        ((ViewHolder) holder).cardAnswerHint.setHint(item.getAnswerHint());

        questionItem[position] = ((ViewHolder) holder).cardQuestion.getText().toString();

        ((ViewHolder) holder).cardAnswer.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>   hhhhhh:"+s.toString());
                answerItem[position] = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>   hhhhhhhhhh:");
    }


    public String[] getAnswer(){
        return answerItem;
    }
    public String[] getQuestion(){
        return questionItem;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

}
