package eu.areamobile_android_apps.logclient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentsAdapter extends BaseAdapter {

	private Comments mComments;
	private final Context mContext;
	private final LayoutInflater mInflater;

	public CommentsAdapter(Context c, Comments comments) {

		mComments = comments;
		mInflater = LayoutInflater.from(c);
		mContext = c;
	}

	public void refresh(Comments comments) {

		mComments = comments;
	}

	@Override
	public int getCount() {

		return mComments.size();
	}

	@Override
	public SingleComment getItem(int position) {

		return mComments.get(position);

	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;

		if (convertView == null) {
			convertView = (LinearLayout) mInflater.inflate(R.layout.row_layout,
					parent, false);
			holder = new ViewHolder();
			holder.tvId = (TextView) convertView
			.findViewById(R.id.row_id);
			holder.tvSender = (TextView) convertView
					.findViewById(R.id.row_sender);
			holder.tvComment = (TextView) convertView
					.findViewById(R.id.row_comment);
			convertView.setTag(holder);
			holder.tvTimestampDate = (TextView) convertView
					.findViewById(R.id.row_timestamp_date);
			holder.tvTimestampHour = (TextView) convertView
			.findViewById(R.id.row_timestamp_hour);
			
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvId.setText(getItem(position).getId());
		holder.tvSender.setText(getItem(position).getSender());
		holder.tvComment.setText(getItem(position).getComment());
		
		Long timeInMillis = Long.parseLong(getItem(position).getTimestamp());
		Date d = new Date(timeInMillis);
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String formattedDate = df.format(d);
		holder.tvTimestampDate.setText(formattedDate);
		
		df = new SimpleDateFormat("HH:mm:ss");
		formattedDate = df.format(d);
		holder.tvTimestampHour.setText(formattedDate);
		
	
		return convertView;
	}

	private class ViewHolder {

		public TextView tvId;
		public TextView tvSender;
		public TextView tvComment;
		public TextView tvTimestampDate;
		public TextView tvTimestampHour;
	}
}
