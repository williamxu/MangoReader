package com.william.mangoreader.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.william.mangoreader.R;
import com.william.mangoreader.activity.MangaItemActivity;
import com.william.mangoreader.activity.MangoReaderActivity;
import com.william.mangoreader.adapter.helper.ItemTouchHelperAdapter;
import com.william.mangoreader.daogen.UserLibraryManga;
import com.william.mangoreader.daogen.UserLibraryMangaDao;
import com.william.mangoreader.model.MangaEdenMangaListItem;
import com.william.mangoreader.parse.MangaEden;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.dao.query.QueryBuilder;

/**
 * Layout adapter for adding cards
 */
public class CardLayoutAdapter extends RecyclerView.Adapter<CardLayoutAdapter.CardViewHolder> implements ItemTouchHelperAdapter, Filterable {

    private List<MangaEdenMangaListItem> allManga;
    private List<MangaEdenMangaListItem> filteredManga;
    private Activity activity;

    public CardLayoutAdapter(Activity activity) {
        filteredManga = new ArrayList<>();
        this.activity = activity;
        // Pass context or other static stuff that will be needed.
    }

    public void setAllManga(List<MangaEdenMangaListItem> allManga) {
        this.allManga = allManga;
    }

    @Override
    public void onBindViewHolder(CardViewHolder viewHolder, int position) {
        viewHolder.title.setText(filteredManga.get(position).title);
        viewHolder.subtitle.setText("Placeholder");
        MangaEden.setThumbnail(filteredManga.get(position).imageUrl, activity.getApplicationContext(), viewHolder.thumbnail);
        viewHolder.manga = filteredManga.get(position);
    }


    public void clearList() {
        filteredManga.clear();
        notifyDataSetChanged();
    }

    /**
     * Creates dialog for user to select library category to add to.
     *
     * @param m
     */
    private void addToLibrary(final MangaEdenMangaListItem m) {
        final UserLibraryManga[] mangaItem = new UserLibraryManga[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose a category");
        builder.setItems(R.array.library_categories, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mangaItem[0] = new UserLibraryManga(
//                null, //TODO need more robust primary key
                        m.id,
                        activity.getResources().getStringArray(R.array.library_categories)[which],
                        m.title,
                        m.imageUrl,
                        m.status,
                        m.lastChapterDate,
                        m.hits);

                ((MangoReaderActivity) activity).userLibraryMangaDao.insert(mangaItem[0]);
                String added = "\"" + m.title + "\" added to your library.";
                Snackbar
                        .make(activity.findViewById(R.id.drawer_layout), added, Snackbar.LENGTH_LONG)
                                // .setAction() //TODO add undo on click
                        .show();
            }
        });
        builder.show();


    }

    private void removeFromLibrary(MangaEdenMangaListItem m) {
        QueryBuilder qb = ((MangoReaderActivity) activity).userLibraryMangaDao.queryBuilder();
        qb.where(UserLibraryMangaDao.Properties.Title.eq(m.title), UserLibraryMangaDao.Properties.ImageURL.eq(m.imageUrl));
        List l = qb.list();
        if (l.size() == 0) {
            Log.e("MangoReader", "No manga found in user library.");
        } else {
            UserLibraryManga mangaItem = (UserLibraryManga) l.get(0);
            ((MangoReaderActivity) activity).userLibraryMangaDao.delete(mangaItem);
            String removed = "\"" + m.title + "\" removed from your library.";
            Snackbar
                    .make(activity.findViewById(R.id.drawer_layout), removed, Snackbar.LENGTH_LONG)
                            // .setAction() //TODO add undo on click
                    .show();
        }
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.manga_card, parent, false);
        final CardViewHolder holder = new CardViewHolder(itemView);

        final CardView cardView = (CardView) itemView.findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, MangaItemActivity.class);
                intent.putExtra("mangaId", holder.manga.id);
                activity.startActivity(intent);
            }
        });

        ImageButton bookmarkToggle = (ImageButton) cardView.findViewById(R.id.card_bookmark_toggle);

        bookmarkToggle.setImageResource(R.drawable.bookmark_toggle);

        bookmarkToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    addToLibrary(holder.manga);
                } else {
                    removeFromLibrary(holder.manga);
                }
            }
        });

        return holder;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        //TODO implement for dragging in library
        return false;
    }

    @Override
    public void onItemDismiss(int position) {
        //TODO add to user library
        Toast.makeText(activity, "\"" + filteredManga.get(position).title + "\" added to your library.", Toast.LENGTH_SHORT).show();
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return filteredManga.size();
    }

    @Override
    public Filter getFilter() {
        return new CardLayoutFilter();
    }


    /**
     * Performs the filtering of the cards by the query term
     */
    private class CardLayoutFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence query) {
            filteredManga.clear();
            FilterResults results = new FilterResults();

            if (query.length() == 0) {
                // If search term empty, show all manga
                filteredManga.addAll(allManga);
            } else {
                // Otherwise, do a case-blind search
                // TODO replace with better fuzzy match algorithm
                String filterPattern = query.toString().toLowerCase().trim();

                for (MangaEdenMangaListItem item : allManga) {
                    if (item.title.toLowerCase().contains(filterPattern)) {
                        filteredManga.add(item);
                    }
                }
            }
            results.values = filteredManga;
            results.count = filteredManga.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notifyDataSetChanged();
        }
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {

        public TextView title;
        public TextView subtitle;
        public ImageView thumbnail;
        public MangaEdenMangaListItem manga;

        public CardViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.card_title);
            subtitle = (TextView) itemView.findViewById(R.id.card_subtitle);
            thumbnail = (ImageView) itemView.findViewById(R.id.card_thumbnail);

        }
    }
}
