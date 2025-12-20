package learning.itstep.javaweb222.rest;

import jakarta.servlet.http.HttpServletRequest;
import static java.lang.Math.ceil;


public class RestPagination {
    private int currentPage;
    private int lastPage;
    private int perPage;
    private int totalItems;
    private String firstPageHref;

    public static RestPagination fromRequest(HttpServletRequest req, 
            int totalItems) throws NumberFormatException {
        return RestPagination.fromRequest(req, 2, totalItems);
    }
    
    public static RestPagination fromRequest(HttpServletRequest req, 
            int totalItems, int defaultPerpage) throws NumberFormatException {
        
        int perpage = defaultPerpage;
        String perpageParam = req.getParameter("perpage");
        if(perpageParam != null) {
            try {
                perpage = Integer.parseInt(perpageParam);
                if(perpage < 1) throw new NumberFormatException();
            }
            catch(NumberFormatException ignore) {
                throw new NumberFormatException(
                        "Parameter 'perpage' must be positive decimal number");
            }
        }
        if(totalItems < 0) {
            throw new NumberFormatException("No items to paginate");
        }
        
        int lastPage = (int)ceil( (double)totalItems / perpage );
        if(lastPage ==0)
        {
             lastPage = 1;
        }
        int page = 1;
        String pageParam = req.getParameter("page");
        if(pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
                if(page < 1 || page > lastPage) throw new NumberFormatException();
            }
            catch(NumberFormatException ignore) {
                throw new NumberFormatException("Parameter 'page' must be positive decimal number "
                            + "between 1 and " + lastPage);
            }
        }
        
        return new RestPagination()
                .setCurrentPage(page)
                .setPerPage(perpage)
                .setLastPage(lastPage)
                .setTotalItems(totalItems);        
    }
 
    
    public String getFirstPageHref() {
        return firstPageHref;
    }

    public RestPagination setFirstPageHref(String firstPageHref) {
        this.firstPageHref = firstPageHref;
        return this;
    }

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
