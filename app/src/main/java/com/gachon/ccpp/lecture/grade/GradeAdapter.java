package com.gachon.ccpp.lecture.grade;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.listener.OnViewHolderItemClickListener;
import com.gachon.ccpp.R;
import com.gachon.ccpp.lecture.assignment.AssignmentFragment;
import com.gachon.ccpp.parser.TableForm;

import java.util.Map;

public class GradeAdapter extends RecyclerView.Adapter<GradeAdapter.GradeViewHolder> {
    // 해당 어댑터의 ViewHolder를 상속받는다.
    private TableForm list;
    private Context context;

    public GradeAdapter(TableForm list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public GradeAdapter.GradeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GradeAdapter.GradeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.grade_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull GradeAdapter.GradeViewHolder holder, int position) {
        // ViewHolder 가 재활용 될 때 사용되는 메소드
        GradeAdapter.GradeViewHolder viewHolder = (GradeAdapter.GradeViewHolder)holder;
        viewHolder.onBind(list.table.get(holder.getAdapterPosition()));
        int pos = holder.getAdapterPosition();
        // 뷰홀더에 아이템클릭리스너 인터페이스 붙이기
        viewHolder.setOnViewHolderItemClickListener(new OnViewHolderItemClickListener() {
            @Override
            public void onViewHolderItemClick() {
                AssignmentFragment assignmentFragment = new AssignmentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("link",list.table.get(holder.getAdapterPosition()).get(1));
                assignmentFragment.setArguments(bundle);
                deployFragment(assignmentFragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.table.size(); // 전체 데이터의 개수 조회
    }
    public void changeItem(TableForm data) {
        // 외부에서 item을 추가시킬 함수입니다.
        list = data;
        notifyDataSetChanged();
    }

    // 아이템 뷰를 저장하는 클래스
    public class GradeViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder 에 필요한 데이터들을 적음.
        private TextView title;
        private TextView Grade;
        private TextView percentage;
        private TextView feedback;

        OnViewHolderItemClickListener onViewHolderItemClickListener;

        private ConstraintLayout layout;

        GradeViewHolder(@NonNull View itemView) {
            super(itemView);
            // 아이템 뷰에 필요한 View
            title = itemView.findViewById(R.id.grade_title);
            Grade = itemView.findViewById(R.id.grade_grade);
            percentage = itemView.findViewById(R.id.grade_percentage);
            feedback = itemView.findViewById(R.id.grade_feedback);
            layout = itemView.findViewById(R.id.grade_layout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderItemClickListener.onViewHolderItemClick();
                }
            });
        }

        public void onBind(Map<Integer,String> data){
            String Grade_text = data.get(3);
            String title_text = data.get(0);
            if(title_text.compareTo("")==0)title_text="Total Score";
            title.setText(title_text);
            if(data.get(4).compareTo("–")!=0) Grade_text = Grade_text +"/"+data.get(4).split("–")[1];
            Grade.setText(Grade_text);
            percentage.setText(data.get(5));
            feedback.setText(Html.fromHtml(data.get(6)));
        }

        public void setOnViewHolderItemClickListener(OnViewHolderItemClickListener onViewHolderItemClickListener) {
            this.onViewHolderItemClickListener = onViewHolderItemClickListener;
        }
    }

    public void deployFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragLayout, fragment).addToBackStack(null).commit();
    }

}