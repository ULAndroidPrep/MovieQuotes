package edu.rosehulman.moviequotes;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Boutell on 12/15/2015.
 */
public class MovieQuoteAdapter extends RecyclerView.Adapter<MovieQuoteAdapter.ViewHolder> {

  private List<MovieQuote> mMovieQuotes;
    private Callback mCallback;
    // Access a Cloud Firestore instance from your Activity


    private CollectionReference mQuotesRef;

    public MovieQuoteAdapter(Callback callback) {
        mCallback = callback;
        mMovieQuotes = new ArrayList<>();
        mQuotesRef = FirebaseFirestore.getInstance().collection("quotes");
        addListener();
      Log.d(Constants.TAG, "Test log");
    }

  private void addListener() {
    mQuotesRef
        .addSnapshotListener(new EventListener<QuerySnapshot>() {
          @Override
          public void onEvent(@Nullable QuerySnapshot documentSnapshots,
                              @Nullable FirebaseFirestoreException e) {
            if (e != null) {
              Log.w(Constants.TAG, "listen:error", e);
              return;
            }

            for (DocumentChange dc : documentSnapshots.getDocumentChanges()) {
              Log.d(Constants.TAG, "Loop" + dc.getDocument().getId());
              switch (dc.getType()) {
                case ADDED:
                  Log.d(Constants.TAG, "New movie quote: " + dc.getDocument().getData());
                  MovieQuote quote = dc.getDocument().toObject(MovieQuote.class);
                  quote.id = dc.getDocument().getId();
                  mMovieQuotes.add(quote);
                  break;
                case MODIFIED:
                  Log.d(Constants.TAG, "Modified movie quote: " + dc.getDocument().getData());
                  for (MovieQuote mq : mMovieQuotes) {
                    if (mq.id.equals(dc.getDocument().getId())) {
                      mMovieQuotes.remove(mq);
                      mMovieQuotes.add(mq);
                      notifyDataSetChanged();
                      break;
                    }
                  }
                  break;
                case REMOVED:
                  Log.d(Constants.TAG, "Removed movie quote: " + dc.getDocument().getData());
                  for (MovieQuote mq : mMovieQuotes) {
                    if (mq.id.equals(dc.getDocument().getId())) {
                      mMovieQuotes.remove(mq);
                      notifyDataSetChanged();
                      break;
                    }
                  }
                  break;
              }
            }
            notifyDataSetChanged();

          }
        });
  }

  @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_quote_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MovieQuote movieQuote = mMovieQuotes.get(position);
        holder.mQuoteTextView.setText(movieQuote.quote);
        holder.mMovieTextView.setText(movieQuote.movie);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEdit(movieQuote);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(mMovieQuotes.get(position));
                return true;
            }
        });
    }

    public void remove(MovieQuote movieQuote) {
        //TODO: Remove the next line(s) and use Firebase instead
//        mMovieQuotes.remove(movieQuote);
//        notifyDataSetChanged();

      // Needs to do this...
      Log.d(Constants.TAG, "REmove" + movieQuote.quote);
      mQuotesRef.document(movieQuote.id).delete();
    }


    @Override
    public int getItemCount() {
        return mMovieQuotes.size();
    }

    public void add(MovieQuote movieQuote) {
        //TODO: Remove the next line(s) and use Firebase instead
//        mMovieQuotes.add(0, movieQuote);
//        notifyDataSetChanged();
      mQuotesRef.add(movieQuote);
    }

    public void update(MovieQuote movieQuote, String newQuote, String newMovie) {
        //TODO: Remove the next line(s) and use Firebase instead
        movieQuote.quote = newQuote;
        movieQuote.movie = newMovie;
//        notifyDataSetChanged();

      mQuotesRef.document(movieQuote.id).set(movieQuote);
      

    }

    public interface Callback {
        public void onEdit(MovieQuote movieQuote);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mQuoteTextView;
        private TextView mMovieTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuoteTextView = (TextView) itemView.findViewById(R.id.quote_text);
            mMovieTextView = (TextView) itemView.findViewById(R.id.movie_text);
        }
    }


}
