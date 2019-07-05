package com.bluelithalo.lumnart;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class DirectoryListAdapter extends RecyclerView.Adapter<DirectoryListAdapter.DirectoryViewHolder>
{
    private List<String> directoryFilePathList;
    private DirectoryListContainer directoryListContainer;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class DirectoryViewHolder extends RecyclerView.ViewHolder
    {
        public ImageView directoryPreviewImageView;
        public TextView directoryNameTextView;

        public DirectoryViewHolder(View v)
        {
            super(v);
            directoryPreviewImageView = (ImageView) v.findViewById(R.id.directory_preview_image_view);
            directoryNameTextView = (TextView) v.findViewById(R.id.directory_name_text_view);
        }
    }

    public DirectoryListAdapter(List<String> newPatternFilePathList, DirectoryListContainer newPatternListContainer)
    {
        directoryFilePathList = newPatternFilePathList;
        directoryListContainer = newPatternListContainer;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DirectoryListAdapter.DirectoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.directory_list_item, parent, false);
        DirectoryViewHolder dvh = new DirectoryViewHolder(v);
        return dvh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(DirectoryViewHolder holder, final int position)
    {
        final File directoryFile = new File(directoryFilePathList.get(position));
        String name = "";

        if (position == 0)
        {
            holder.directoryPreviewImageView.setImageResource(R.drawable.ic_arrow_up);
            holder.directoryPreviewImageView.setPadding(32, 32, 32, 32);
            name = "(up)";
        }
        else
        {
            holder.directoryPreviewImageView.setImageResource(R.drawable.ic_folder);
            name = directoryFile.getName();

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    directoryListContainer.onDirectoryImageLongClick(directoryFile.getAbsolutePath());
                    return true;
                }
            });
        }

        holder.directoryNameTextView.setText(name);
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                directoryListContainer.onDirectoryImageClick(directoryFile.getAbsolutePath());
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return directoryFilePathList.size();
    }

    public interface DirectoryListContainer
    {
        void onDirectoryImageClick(String directoryFilepath);
        void onDirectoryImageLongClick(String directoryFilepath);
    }
}
