package com.gachon.ccpp.lecture.assignment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.gachon.ccpp.dialog.LoginDialog;
import com.gachon.ccpp.listener.OnViewHolderItemClickListener;
import com.gachon.ccpp.R;
import com.gachon.ccpp.parser.TableForm;

import java.util.Map;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder> {
    // 해당 어댑터의 ViewHolder를 상속받는다.
    private TableForm list;
    private Context context;

    public AssignmentAdapter(TableForm list,Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public AssignmentAdapter.AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AssignmentAdapter.AssignmentViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentAdapter.AssignmentViewHolder holder, int position) {
        // ViewHolder 가 재활용 될 때 사용되는 메소드
        AssignmentAdapter.AssignmentViewHolder viewHolder = (AssignmentAdapter.AssignmentViewHolder)holder;
        viewHolder.onBind(list.table.get(holder.getAdapterPosition()),holder.itemView.getResources());
        int pos = holder.getAdapterPosition();
        // 뷰홀더에 아이템클릭리스너 인터페이스 붙이기
        viewHolder.setOnViewHolderItemClickListener(new OnViewHolderItemClickListener() {
            @Override
            public void onViewHolderItemClick() {
                AssignmentFragment assignmentFragment = new AssignmentFragment();
                Bundle bundle = new Bundle();
                bundle.putString("link",list.table.get(holder.getAdapterPosition()).get(2));
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
    public class AssignmentViewHolder extends RecyclerView.ViewHolder {
        // ViewHolder 에 필요한 데이터들을 적음.
        private TextView title;
        private TextView week;
        private TextView due;
        private TextView status;
        private ImageView image;

        OnViewHolderItemClickListener onViewHolderItemClickListener;

        private ConstraintLayout layout;

        AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            // 아이템 뷰에 필요한 View
            title = itemView.findViewById(R.id.assignment_title);
            week = itemView.findViewById(R.id.assignment_week);
            due = itemView.findViewById(R.id.assignment_due);
            image = itemView.findViewById(R.id.assignment_image);
            status = itemView.findViewById(R.id.assignment_status);
            layout = itemView.findViewById(R.id.assignment_layout);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onViewHolderItemClickListener.onViewHolderItemClick();
                }
            });
        }

        public void onBind(Map<Integer,String> data, Resources resource){
            String assginmentStatus = data.get(4);
            String grade = data.get(5);
            if(assginmentStatus.compareTo("No submission")==0||assginmentStatus.compareTo("미제출")==0){
                assginmentStatus="Didn't submit";
                image.setImageBitmap(BitmapFactory.decodeResource(resource,R.drawable.cross));
            }else{
                if(grade.compareTo("-")==0)assginmentStatus="Not graded yet";
                else assginmentStatus="Graded : " + grade;
            }
            title.setText(data.get(1));
            status.setText(assginmentStatus);
            week.setText(data.get(0));
            due.setText(data.get(3));
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