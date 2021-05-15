package m19.core;

import java.io.Serializable;

import m19.core.Library;

public abstract class Work implements Serializable{

    private int _id;
    private int _price;
    private int _numberOfCopies;
    private int _numberAvailableCopies;
    private String _title;
    private Categorys _category;
  
    public Work (int price, int numberOfCopies, String title, Library library, String category) {
        _price = price;
        _numberOfCopies = numberOfCopies;
        _numberAvailableCopies = numberOfCopies;
        _title = title;
        _category = Categorys.valueOf(category);
        _id = library.getNextWorkId();
        library.doNextWorkId();
    }

    /**
     * @return id of the work
     */
    protected int getId() {
        return _id;
    }

    /**
     * @return price of the work
     */
    public int getPrice() {
        return _price;
    }

    /**
     * updates the price of the work
     */
    public void setPrice() {
        _price = _price + 5;
    }

    /**
     * @return number of copies of the work
     */
    protected int getNumberOfCopies() {
        return _numberOfCopies;
    }

    /**
     * @return number of available copies of the work
     */
    protected int getAvailableCopies() {
        return _numberAvailableCopies;
    }

    /**
     * decreases the number of availeble copies when one is requested
     */
    protected void setAvailableCopies() {
        _numberAvailableCopies -= 1;
    }

    /**
     * increases the number of availeble copies when one is returned
     */
    protected void putAvailableCopies() {
        _numberAvailableCopies += 1;
    }

    /**
     * @return title of the work
     */
    public String getTitle () {
        return _title;
    }

    /**
     * @return true if the searchWord was found in the title
     */
    protected Boolean getsearch(String searchWord) {
        return getTitle().toLowerCase().matches("(.*)" + searchWord.toLowerCase() + "(.*)");
    }

    protected Categorys getCategorys() {
        return _category;
    }

    /**
     * @return discription of the work
     */
    public String getDiscription() {
        return getId() + " - " + getAvailableCopies() + " de  " + getNumberOfCopies();
    }

}