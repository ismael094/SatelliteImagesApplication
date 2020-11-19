package services.search;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;

import java.io.IOException;

public interface SearchService {
    /**
     * Search products
     * @return Response of the search service
     * @throws IOException Error while searching
     * @throws AuthenticationException Not login in the API
     * @throws NotAuthenticatedException Not login in the API
     */
    Object search() throws IOException, AuthenticationException, NotAuthenticatedException;
}
