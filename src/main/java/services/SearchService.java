package services;

import model.exception.AuthenticationException;
import model.exception.NotAuthenticatedException;

import java.io.IOException;

public interface SearchService {

    Object search() throws IOException, AuthenticationException, NotAuthenticatedException;
}
