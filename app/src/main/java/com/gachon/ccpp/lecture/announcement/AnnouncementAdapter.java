package com.gachon.ccpp.lecture.announcement;

import android.animation.ValueAnimator;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.listener.OnViewHolderItemClickListener;
import com.gachon.ccpp.R;
import com.gachon.ccpp.parser.ContentForm;

import java.util.ArrayList;
import java.util.Collections;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {
    // 해당 어댑터의 ViewHolder를 상속받는다.
    private ArrayList<ContentForm> list;

    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private int prePosition = -1;

    public AnnouncementAdapter(ArrayList<ContentForm> list) {
        this.list = list;
    }

    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // ViewHodler 객체를 생성 후 리턴한다.
        return new AnnouncementViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.announcement_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        // ViewHolder 가 재활용 될 때 사용되는 메소드
        AnnouncementViewHolder viewHolder = (AnnouncementViewHolder)holder;
        viewHolder.onBind(list.get(holder.getAdapterPosition()),position, selectedItems);
        int pos = holder.getAdapterPosition();
        // 뷰홀더에 아이템클릭리스너 인터페이스 붙이기
        viewHolder.setOnViewHolderItemClickListener(new OnViewHolderItemClickListener() {
            @Override
            public void onViewHolderItemClick() {
                if (selectedItems.get(pos)) {
                    // 펼쳐진 Item을 클릭 시
                    selectedItems.delete(pos);
                } else {
                    // 직전의 클릭됐던 Item의 클릭상태를 지움
                    selectedItems.delete(prePosition);
                    // 클릭한 Item의 position을 저장
                    selectedItems.put(pos, true);
                }
                // 해당 포지션의 변화를 알림
                if (prePosition != -1) notifyItemChanged(prePosition);
                notifyItemChanged(pos);
                // 클릭된 position 저장
                prePosition = pos;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size(); // 전체 데이터의 개수 조회
    }
    public void addItem(ContentForm data) {
        // 외부에서 item을 추가시킬 함수입니다.
        list.add(data);
        Collections.sort(list, (a, b) -> Integer.valueOf(b.payload)- Integer.valueOf(a.payload));
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 클래스
    public class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder 에 필요한 데이터들을 적음.
        private TextView title;
        private TextView writer;
        private TextView date;
        private TextView num;
        private TextView content;
        private TextView height;

        OnViewHolderItemClickListener onViewHolderItemClickListener;

        private LinearLayout layout;
        AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            // 아이템 뷰에 필요한 View
            title = itemView.findViewById(R.id.announcement_title);
            writer = itemView.findViewById(R.id.announcement_writer);
            date = itemView.findViewById(R.id.announcement_date);
            num = itemView.findViewById(R.id.announcement_num);
            content = itemView.findViewById(R.id.announcement_content);
            height = itemView.findViewById(R.id.height);
            layout = itemView.findViewById(R.id.announcement_item);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderItemClickListener.onViewHolderItemClick();
                }
            });
        }

        public void onBind(ContentForm data, int position, SparseBooleanArray selectedItems){
            title.setText(data.title);
            writer.setText(data.writer);
            date.setText(data.date);
            num.setText(data.payload);
            content.setText(Html.fromHtml(data.content));
            content.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int heightValue = content.getMeasuredHeight();
            height.setText(String.valueOf(heightValue));
            changeVisibility(selectedItems.get(position));
        }
        private void changeVisibility(final boolean isExpanded) {
            // height 값을 dp로 지정해서 넣고싶으면 아래 소스를 이용
            int heightValue = Integer.valueOf((String) height.getText());
            // ValueAnimator.ofInt(int... values)는 View가 변할 값을 지정, 인자는 int 배열
            ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, heightValue) : ValueAnimator.ofInt(heightValue, 0);
            // Animation이 실행되는 시간, n/1000초
            va.setDuration(600);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    content.getLayoutParams().height = heightValue;
                    content.requestLayout();
                    content.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
                }
            });
            // Animation start
            va.start();
        }

        public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
            this.onViewHolderItemClickListener = onViewHolderItemClickListener;
        }
    }


}
