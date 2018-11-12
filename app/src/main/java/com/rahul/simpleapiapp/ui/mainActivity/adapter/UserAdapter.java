package com.rahul.simpleapiapp.ui.mainActivity.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rahul.simpleapiapp.R;
import com.rahul.simpleapiapp.core.models.Results;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {



    private List<Results> Listitems;
    private Context context;
    private MyClickListeners myClickListeners;


    public UserAdapter(List<Results> listitems, Context context) {
        Listitems = listitems;
        this.context = context;
    }

    public MyClickListeners getMyClickListeners() {
        return myClickListeners;
    }

    public void setMyClickListeners(MyClickListeners clickListerner) {
        this.myClickListeners = clickListerner;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.user_row_item, parent, false);


        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Results resultDataModal = Listitems.get(position);
        // Log.d("TAG", "onBindViewHolderNull: "+feedsDataModal.getDescription());
        if (resultDataModal != null) {
            //   Log.d("TAG", "onBindViewHolder: "+feedsDataModal.getDescription());
            holder.nameTv.setText(capitalizeFirstLetter(resultDataModal.name.getFirst()) + " " + capitalizeFirstLetter(resultDataModal.name.getLast()));
            holder.phoneNoTv.setText(resultDataModal.getPhone());
            holder.emailNoTv.setText(resultDataModal.getEmail());
            DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);
            try {
                Date date = format.parse(resultDataModal.getDob().getDate());
                holder.dobTv.setText(DateUtils.getRelativeTimeSpanString(date.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
                holder.dobTv.setText(resultDataModal.dob.getDate());

            }
            Glide.with(context).load(resultDataModal.picture.getMedium())
                    .into(holder.profileImageIv);

        }

    }

    public String capitalizeFirstLetter(String original) {
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }


    @Override
    public int getItemCount() {
        if (Listitems == null) {
            return 0;
        } else
            return Listitems.size();
    }

    public interface MyClickListeners {
        void itemClicked(String feedID, int position);

    }

    public void setFullList(List<Results> listitems) {
        if (Listitems != null) {
            Listitems.clear();
            if(listitems!=null && listitems.size()>0){

                for (int i = 0 ; i< listitems.size();i++){
                    Listitems.add(listitems.get(i));
                }
            }
            notifyDataSetChanged();

        }


    }



    public class MyViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.profile_image_iv)
        CircleImageView profileImageIv;
        @BindView(R.id.name_tv)
        TextView nameTv;
        @BindView(R.id.phone_no_tv)
        TextView phoneNoTv;
        @BindView(R.id.email_no_tv)
        TextView emailNoTv;
        @BindView(R.id.dob_tv)
        TextView dobTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
