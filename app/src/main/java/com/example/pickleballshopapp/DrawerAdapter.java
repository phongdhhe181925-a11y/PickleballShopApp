package com.example.pickleballshopapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface Callback {
        void onOpenAll(String category);
        void onOpenBrand(String category, int brandId, String brandName);
        void onOpenBallsAll();
    }

    private static final int TYPE_GROUP = 1;
    private static final int TYPE_CHILD = 2;

    private final Context context;
    private final LayoutInflater inflater;
    private final Callback callback;

    private final List<Item> items = new ArrayList<>();

    public DrawerAdapter(Context context, Callback callback) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.callback = callback;
    }

    public void setData(List<Group> groups) {
        items.clear();
        for (Group g : groups) {
            items.add(g);
            if (g.expanded) {
                items.addAll(g.children);
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (items.get(position) instanceof Group) ? TYPE_GROUP : TYPE_CHILD;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_GROUP) {
            View v = inflater.inflate(R.layout.item_drawer_group, parent, false);
            return new GroupVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_drawer_child, parent, false);
            return new ChildVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Item item = items.get(position);
        if (holder instanceof GroupVH) {
            Group g = (Group) item;
            ((GroupVH) holder).bind(g);
        } else if (holder instanceof ChildVH) {
            Child c = (Child) item;
            ((ChildVH) holder).bind(c);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void toggleGroup(Group group, int position) {
        if (group.expanded) {
            // collapse
            int start = position + 1;
            int count = group.children.size();
            group.expanded = false;
            for (int i = 0; i < count; i++) items.remove(start);
            notifyItemRangeRemoved(start, count);
            notifyItemChanged(position);
        } else {
            // expand
            int start = position + 1;
            group.expanded = true;
            items.addAll(start, group.children);
            notifyItemRangeInserted(start, group.children.size());
            notifyItemChanged(position);
        }
    }

    class GroupVH extends RecyclerView.ViewHolder {
        TextView title;
        ImageView arrow;
        Group current;

        GroupVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvGroupTitle);
            arrow = itemView.findViewById(R.id.ivArrow);
            itemView.setOnClickListener(v -> {
                if (current == null) return;
                if ("balls".equals(current.category)) {
                    // balls opens directly all products
                    if (callback != null) callback.onOpenBallsAll();
                } else {
                    toggleGroup(current, getBindingAdapterPosition());
                }
            });
        }

        void bind(Group g) {
            current = g;
            title.setText(g.title);
            arrow.setVisibility("balls".equals(g.category) ? View.INVISIBLE : View.VISIBLE);
            arrow.setRotation(g.expanded ? 180f : 0f);
        }
    }

    class ChildVH extends RecyclerView.ViewHolder {
        TextView title;
        Child current;

        ChildVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvChildTitle);
            itemView.setOnClickListener(v -> {
                if (current == null) return;
                if (current.isAll) {
                    if (callback != null) callback.onOpenAll(current.category);
                } else {
                    if (callback != null) callback.onOpenBrand(current.category, current.brandId, current.title);
                }
            });
        }

        void bind(Child c) {
            current = c;
            title.setText(c.title);
        }
    }

    // Models
    public static abstract class Item {}

    public static class Group extends Item {
        public final String title;
        public final String category; // racket | shoes | balls
        public boolean expanded;
        public final List<Child> children = new ArrayList<>();

        public Group(String title, String category, boolean expanded) {
            this.title = title;
            this.category = category;
            this.expanded = expanded;
        }
    }

    public static class Child extends Item {
        public final String title; // brand name or "Xem tất cả ..."
        public final String category; // inherit parent
        public final boolean isAll;
        public final int brandId; // 0 when isAll

        public Child(String title, String category, boolean isAll, int brandId) {
            this.title = title;
            this.category = category;
            this.isAll = isAll;
            this.brandId = brandId;
        }
    }
}
