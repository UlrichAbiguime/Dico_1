package dico.wule.tg.dico.entities;

/**
 * Created by lakiu_000 on 4/11/2015.
 */
public class FavoriteSave {


    public FavoriteSave(String title, String body) {
        this.title = title;
        this.body = body;
    }

    @Override
    public String toString() {
        return "FavoriteSave{" +
                "title='" + title + '\'' +
                ", body='" + body + '\'' +
                '}';
    }

    public String title, body;

}
