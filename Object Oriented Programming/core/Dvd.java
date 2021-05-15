package m19.core;

import m19.core.Work;

public class Dvd extends Work {

    private String _director;
    private int _igac;

    public Dvd (String title, String director,  int price, String category, int igac, int numberOfCopies, Library library) {
        super(price, numberOfCopies, title, library, category);
        _director = director;
        _igac = igac;
    }

    /**
     * @return director of the Dvd
     */
    protected String getDirector() {
        return _director;
    }


    /**
     * @return igac of the Dvd
     */
    protected int getIgac() {
        return _igac;
    }

    /**
     * @param searchWord
     * @return true if searchWord matches  Director
     */
    protected Boolean getsearch(String searchWord) {
        return super.getsearch(searchWord) || getDirector().toLowerCase().matches("(.*)" + searchWord.toLowerCase() + "(.*)");
    }

    /**
     * @return description of the Dvd
     */
    public String getDiscription (){
        return super.getDiscription() + " - DVD - " + super.getTitle()
               + " - " + super.getPrice() + " - " + super.getCategorys().toString() + " - " + _director + " - " + _igac;
    }
}