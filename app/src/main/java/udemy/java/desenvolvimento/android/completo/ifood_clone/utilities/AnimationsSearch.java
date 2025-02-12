package udemy.java.desenvolvimento.android.completo.ifood_clone.utilities;

import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.SearchView;

public class AnimationsSearch {

    public void animateSearchView(boolean expand, SearchView searchView) {
        Animation animation;
        if (expand) {
            animation = new AlphaAnimation(0f, 1f);
            searchView.setIconified(false); // Expand the search view
        } else {
            animation = new AlphaAnimation(1f, 0f);
            searchView.setIconified(true); // Collapse the search view

        }

        animation.setDuration(300);
        animation.setFillAfter(true);
        searchView.startAnimation(animation);

    }

    public void animateSearchViewExpand(boolean expand, SearchView searchView) {
        float scaleX = expand ? 1.0f : 0.0f;
        float scaleY = expand ? 1.0f : 0.0f;

        searchView.animate()
                .scaleX(scaleX)
                .scaleY(scaleY)
                .setDuration(300)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        searchView.setIconified(!expand); // Collapse or expand the search view
                    }
                })
                .start();
    }

}
