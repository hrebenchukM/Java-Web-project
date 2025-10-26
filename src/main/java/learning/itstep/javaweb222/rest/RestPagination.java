package learning.itstep.javaweb222.rest;


public class RestPagination {
    private int currentPage;
    private int lastPage;
    private int perPage;
    private int totalItems;

    public int getCurrentPage() {
        return currentPage;
    }

    public RestPagination setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
        return this;
    }

    public int getLastPage() {
        return lastPage;
    }

    public RestPagination setLastPage(int lastPage) {
        this.lastPage = lastPage;
        return this;
    }

    public int getPerPage() {
        return perPage;
    }

    public RestPagination setPerPage(int perPage) {
        this.perPage = perPage;
        return this;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public RestPagination setTotalItems(int totalItems) {
        this.totalItems = totalItems;
        return this;
    }
    
}
