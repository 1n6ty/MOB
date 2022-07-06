package com.example.mobv2.ui.callbacks;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobv2.R;
import com.example.mobv2.adapters.AddressesAdapter;

public class ChangeAddressTouchCallback extends ItemTouchHelper.SimpleCallback
{
    private final Resources resources;
    private boolean swipeBack;

    public ChangeAddressTouchCallback(
            Resources resources,
            int dragDirs,
            int swipeDirs)
    {
        super(dragDirs, swipeDirs);

        this.resources = resources;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target)
    {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder,
                         int direction)
    {
    }

    @Override
    public int convertToAbsoluteDirection(int flags,
                                          int layoutDirection)
    {
        if (swipeBack)
        {
            swipeBack = false;
            return 0;
        }

        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onChildDraw(Canvas canvas,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX,
                            float dY,
                            int actionState,
                            boolean isCurrentlyActive)
    {
        if (viewHolder.getAdapterPosition() == -1)
            return;

        View itemView = viewHolder.itemView;

        int adapterPosition = viewHolder.getAdapterPosition();
        AddressesAdapter adapter = (AddressesAdapter) recyclerView.getAdapter();
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
        {
            Drawable markBackground;
            Drawable mark;

            if (adapter.hasCheckedItem())
            {
                markBackground = new ColorDrawable(resources.getColor(R.color.positive));
                mark = resources.getDrawable(R.drawable.ic_check);
            }
            else
            {
                markBackground = new ColorDrawable(resources.getColor(R.color.negative));
                mark = resources.getDrawable(R.drawable.ic_close);
            }

            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = mark.getIntrinsicWidth();
            int intrinsicHeight = mark.getIntrinsicWidth();

            int margin = (int) resources.getDimension(R.dimen.fab_margin);
            int xMarkLeft = itemView.getRight() - margin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - margin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;

            markBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
            markBackground.draw(canvas);

            mark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
            mark.draw(canvas);

            recyclerView.setOnTouchListener((v, event) ->
            {
                int action = event.getAction();
                swipeBack = action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP;
                if (swipeBack && isCurrentlyActive)
                    adapter.onAddressItemSwiped(adapterPosition);
                return false;
            });
        }

        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
