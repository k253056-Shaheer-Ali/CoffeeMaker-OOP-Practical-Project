public class Filter {

    private boolean isFiltered;

    public Filter() {
        this.isFiltered = false;
    }

    public boolean isFiltered() {
        return isFiltered;
    }

    public void applyFilter() {
        this.isFiltered = true;
    }

    public void removeFilter() {
        this.isFiltered = false;
    }
}
