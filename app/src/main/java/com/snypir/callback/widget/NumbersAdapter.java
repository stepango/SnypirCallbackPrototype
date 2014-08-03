package com.snypir.callback.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.snypir.callback.R;
import com.snypir.callback.model.CallbackNumberInfo;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by stepangoncarov on 11/07/14.
 */
public class NumbersAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private Context context;
    private List<CallbackNumberInfo> numbers;

    public NumbersAdapter(final Context context, @NotNull List<CallbackNumberInfo> numbers) {
        this.context = context;
        this.numbers = numbers;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return numbers.size();
    }

    @Override
    public Object getItem(int position) {
        return numbers;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unneccessary calls
        // to findViewById() on each row.
        ViewHolder holder;
        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.li_contact, null);
            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
//            holder.name = (TextView) convertView.findViewById(R.id.text);
//            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.


            holder = (ViewHolder) convertView.getTag();

        }


        // Bind the data efficiently with the holder.
//        holder.name.setText(myElements.get(id));
//        holder.icon.setImageBitmap( mIcon1 );

        return convertView;
    }

    private class ViewHolder {


    }
}
